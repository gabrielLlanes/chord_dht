package chord.fingertable;

import java.io.Serializable;

import chord.node.RemoteChordNode;

/**
 * A decorator for the standard finger table interface that provides the
 * opportunity for a node to access the nodes pointing to it; that is, the nodes
 * whose finger table contains the node.
 */
public interface FingerTableWithClients<K extends Serializable, V extends Serializable, T extends RemoteChordNode<K, V, T>>
    extends FingerTable<K, V, T> {

  public void addClient(T chordNode);

  public void removeClient(T chordNode);

  /**
   * @return an iterable of client nodes
   */
  public Iterable<Object> clientsIterable();

}
