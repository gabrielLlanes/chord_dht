package chord.node;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Node in a chord network. Remote interface to be exposed.
 * 
 * @param <K> the type of key of the key-value pairs that will be stored in the
 *            chord
 * @param <V> the type of value of the key-value pairs that will be stored in
 *            the chord
 * @param <T> the extending interface
 * @see <a href="https://dl.acm.org/doi/pdf/10.1145/383059.383071">Chord: A
 *      Scalable Peer-to-peer Lookup Service for Internet Applications</a>
 */
public interface RemoteChordNode<K extends Serializable, V extends Serializable, T extends RemoteChordNode<K, V, T>>
    extends Remote {

  /**
   * 
   * @return the id of this node within the chord network
   * @throws RemoteException
   */
  public int getId() throws RemoteException;

  /**
   * @return this node's immediate successor node in the chord network
   */
  public T getImmediateSuccessor() throws RemoteException;

  /**
   * @return this node's immediate precedessor node in the chord network
   */
  public T getImmediatePredecessor() throws RemoteException;

  /**
   * set the immediate sucessor of this node within the chord network.
   * Useful for updating the finger table.
   * 
   * @param successor the node which will be the new predecessor of this node
   */
  public void setImmediateSuccessor(T successor) throws RemoteException;

  /**
   * set the immediate predecessor of this node within the chord network.
   * Useful for updating the finger table.
   * 
   * @param predecessor the node which will be the new predecessor of this node
   */
  public void setImmediatePredecessor(T predecessor) throws RemoteException;

  /**
   *
   * @param n the value whose closest preceding node in this node's finger table
   *          should be found
   * @return the node of this node's finger table which is the closest
   *         preceding finger table node of n
   */
  public T closestPrecedingFingerNode(int n) throws RemoteException;

  /**
   * request for this node to find the successor of the value n.
   * 
   * @param n the value whose successor within the chord network should be found
   */
  public T findSuccessor(int n) throws RemoteException;

  /**
   * request for this node to find the predecessor of the value n.
   * 
   * @param n the value whose precedessor within the chord network should be found
   */
  public T findPredecessor(int n) throws RemoteException;

  /**
   * asks this node to put a key-value pair in the chord
   */
  public void put(K key, V val) throws RemoteException;

  /**
   * put operation that should only be called on the successor of the key.
   */
  public void localPut(K key, V val) throws RemoteException;

  /**
   * asks this node to lookup a key in the chord
   */
  public V lookup(K key) throws RemoteException;

  /**
   * lookup operation that should only be called on the successor of the key.
   */
  public V localLookup(K key) throws RemoteException;

  /**
   * asks this node to remove the key and its associated value from the chord
   */
  public V remove(K key) throws RemoteException;

  /**
   * remove operation that should only be called on the successor of the key.
   */
  public V localRemove(K key) throws RemoteException;

  /**
   * 
   * @param lower the lower bound of the interval of keys to be transferred
   * @param upper the upper bound of the interval of keys to be transferred
   * @return a map containing the buckets that were transferred
   */
  public Map<Integer, Object> transferControl(int lower, int upper);
}
