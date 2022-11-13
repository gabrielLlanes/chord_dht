package chord.node;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import chord.fingertable.FingerTable;
import chord.fingertable.FingerTableImpl;
import chord.util.Modulo;
import chord.util.Util;

/**
 * Base class for universal chord operations and members
 */
public abstract class AbstractChordNode<K extends Serializable, V extends Serializable, T extends RemoteChordNode<K, V, T>>
    implements RemoteChordNode<K, V, T> {

  /* the identifier of this node in the chord network */
  protected final int id;

  /* the exponent for 2 for which this chord network is based on */
  protected final int degree;

  /*
   * the modulus value for which this chord network is based on, and which all
   * modular arithmetic by modulo done relative to
   */
  protected final int modulus;

  /* the finger table of this node */
  transient protected FingerTable<K, V, T> fingerTable;

  /*
   * contains the keys and values falling under jurisdiction of this node.
   */
  transient protected final ConcurrentMap<Integer, LinkedList<Entry<K, V>>> managed = new ConcurrentHashMap<>();

  /**
   * utility for arithmetic modulo the modulus
   */
  transient protected final Modulo modulo;

  protected AbstractChordNode(int degree, int id) throws RemoteException {
    this.degree = degree;
    int modulus = Util.powerOf2(degree);
    this.modulus = modulus;
    this.id = id;
    this.fingerTable = new FingerTableImpl<>(degree, id);
    this.modulo = new Modulo(modulus);
  }

  protected AbstractChordNode(int degree, int id, boolean initial) throws RemoteException {
    this.degree = degree;
    int modulus = Util.powerOf2(degree);
    this.modulus = modulus;
    this.id = id;
    this.fingerTable = new FingerTableImpl<>(degree, id, new Object[degree + 1]);
    for (int i = 0; i <= degree; i++) {
      fingerTable.set(i, self());
    }
    this.modulo = new Modulo(modulus);
  }

  /**
   * dynamic version of "this" to avoid need to cast
   */
  protected abstract T self();

  /**
   * join the network using information from the node
   * 
   * @param chordNode the existing node of the network utilized to join this node
   *                  to the chord
   */
  public abstract void join(T chordNode) throws RemoteException;

  @Override
  public final int getId() {
    return id;
  }

  @Override
  public T getImmediateSuccessor() {
    return fingerTable.get(1);
  }

  @Override
  public T getImmediatePredecessor() {
    return fingerTable.get(0);
  }

  @Override
  public void setImmediateSuccessor(T successor) {
    fingerTable.set(1, successor);
  }

  @Override
  public void setImmediatePredecessor(T predecessor) {
    fingerTable.set(0, predecessor);
  }

  @Override
  public T closestPrecedingFingerNode(int n) throws RemoteException {
    /*
     * find the maximum i such that id < fingerTable(i) <= n. if no entry from the
     * finger table is applicable, returns this node.
     */
    for (int i = fingerTable.size() - 1; i >= 1; i--) {
      T chordNode = fingerTable.get(i);
      if (modulo.inRightHalfClosed(chordNode.getId(), id, n)) {
        return chordNode;
      }
    }
    return self();
  }

  @Override
  public synchronized T findSuccessor(int n) throws RemoteException {
    T nPredecessor = findPredecessor(n);
    // if predecessor of n has id equal to n, then
    // the successor also has id equal to n
    if (nPredecessor.getId() == n) {
      return nPredecessor;
    } else {
      return nPredecessor.getImmediateSuccessor();
    }
  }

  @Override
  public synchronized T findPredecessor(int n) throws RemoteException {
    T currPredecessor = self();
    T currSuccessor = currPredecessor.getImmediateSuccessor();
    // while currPredecessor <= n < currSuccessor is false
    int currPredecessorId = currPredecessor.getId();
    int currSuccessorId = currSuccessor.getId();
    while (!modulo.inLeftHalfClosed(n, currPredecessorId, currSuccessorId)
        && currPredecessorId != currSuccessorId) {
      currPredecessor = currPredecessor.closestPrecedingFingerNode(n);
      currSuccessor = currPredecessor.getImmediateSuccessor();
      currPredecessorId = currPredecessor.getId();
      currSuccessorId = currSuccessor.getId();
    }
    return currPredecessor;
  }

  @Override
  public synchronized void put(K key, V val) throws RemoteException {
    int keyHashCode = modulo.hashCode(key);
    T successorOfKey = findSuccessor(keyHashCode);
    successorOfKey.localPut(key, val);
  }

  @Override
  public synchronized void localPut(K key, V val) {
    int keyHashCode = modulo.hashCode(key);
    LinkedList<Entry<K, V>> managedList = managed.get(keyHashCode);
    if (managedList == null) {
      managed.put(keyHashCode, new LinkedList<>());
      managedList = managed.get(keyHashCode);
    }
    for (Entry<K, V> entry : managedList) {
      if (entry.getKey().equals(key)) {
        entry.setValue(val);
        return;
      }
    }
    managedList.add(new SimpleEntry<>(key, val));
  }

  @Override
  public synchronized V lookup(K key) throws RemoteException {
    int keyHashCode = modulo.hashCode(key);
    T chordNode = findSuccessor(keyHashCode);
    return chordNode.localLookup(key);
  }

  @Override
  public synchronized V localLookup(K key) {
    LinkedList<Entry<K, V>> managedList = managed.get(modulo.hashCode(key));
    if (managedList != null) {
      for (Entry<K, V> entry : managedList) {
        if (entry.getKey().equals(key)) {
          return entry.getValue();
        }
      }
    }
    return null;
  }

  @Override
  public V remove(K key) throws RemoteException {
    int keyHashCode = modulo.hashCode(key);
    T keySuccessor = findSuccessor(keyHashCode);
    return keySuccessor.localRemove(key);
  }

  @Override
  public V localRemove(K key) throws RemoteException {
    LinkedList<Entry<K, V>> managedList = managed.get(modulo.hashCode(key));
    if (managedList != null) {
      for (Entry<K, V> entry : managedList) {
        if (entry.getKey().equals(key)) {
          V val = entry.getValue();
          managedList.remove(entry);
          return val;
        }
      }
    }
    return null;
  }

  @Override
  public Map<Integer, Object> transferControl(int lower, int upper) {
    Map<Integer, Object> transferred = new HashMap<>();
    Iterable<Integer> transferredInts = modulo.intervalIterable(lower, upper);
    for (int i : transferredInts) {
      LinkedList<Entry<K, V>> l = managed.get(i);
      if (l != null && l.size() > 0) {
        transferred.put(i, l);
        managed.remove(i);
      }
    }
    return transferred;
  }

  @Override
  public String toString() {
    return String.format("{chord %s: %s}", getId(), fingerTable);
  }
}
