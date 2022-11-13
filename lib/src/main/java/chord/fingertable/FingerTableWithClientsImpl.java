package chord.fingertable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import chord.node.RemoteChordNode;

/**
 * Implementation of the {@link chord.fingertable.FingerTableWithClients}
 * interface. Unlike the nodes to which a node points, it is not necessary to
 * traverse the nodes that point to a node in an ordered fashion.
 */
public class FingerTableWithClientsImpl<K extends Serializable, V extends Serializable, T extends RemoteChordNode<K, V, T>>
    implements FingerTableWithClients<K, V, T> {

  private final FingerTable<K, V, T> fingerTable;

  private final Set<Object> clients = new HashSet<>();

  public FingerTableWithClientsImpl(int degree, int id) {
    this.fingerTable = new FingerTableImpl<>(degree, id);
  }

  public FingerTableWithClientsImpl(int degree, int id, Object[] fingerTable) {
    this.fingerTable = new FingerTableImpl<>(degree, id, fingerTable);
  }

  @Override
  public int size() {
    return fingerTable.size();
  }

  @Override
  public void set(int index, T chordNode) {
    fingerTable.set(index, chordNode);
  }

  @Override
  public T get(int index) {
    return fingerTable.get(index);
  }

  @Override
  public void addClient(T chordNode) {
    clients.add(chordNode);
  }

  @Override
  public void removeClient(T chordNode) {
    clients.remove(chordNode);
  }

  @Override
  public Iterable<Object> clientsIterable() {
    return Collections.unmodifiableSet(clients);
  }

  @SuppressWarnings("unchecked")
  @Override
  public String toString() {
    Set<Integer> clientIds = new HashSet<>();
    try {
      for (Object client : clients) {
        clientIds.add(((T) client).getId());
      }
    } catch (RemoteException e) {
      return "error occurred in toString()";
    }
    return String.format("{%s, %s}", fingerTable, clients);
  }

}
