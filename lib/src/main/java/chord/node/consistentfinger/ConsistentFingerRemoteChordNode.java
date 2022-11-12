package chord.node.consistentfinger;

import java.io.Serializable;
import java.rmi.RemoteException;

import chord.node.RemoteChordNode;

public interface ConsistentFingerRemoteChordNode<K extends Serializable, V extends Serializable>
    extends RemoteChordNode<K, V, ConsistentFingerRemoteChordNode<K, V>> {

  /**
   * @param i         the index of this node's finger table which may be updated
   * @param chordNode the chordNode that might replace the ith entry of this
   *                  node's finger table
   */
  public void updateFingerTable(int i, ConsistentFingerRemoteChordNode<K, V> chordNode) throws RemoteException;

}
