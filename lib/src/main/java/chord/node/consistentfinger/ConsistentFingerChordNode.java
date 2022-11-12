package chord.node.consistentfinger;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Node in a chord network where finger tables are aggressively maintained.
 * 
 * @see <a href="https://dl.acm.org/doi/pdf/10.1145/383059.383071">Chord: A
 *      Scalable Peer-to-peer Lookup Service for Internet Applications</a>
 */
public interface ConsistentFingerChordNode<K extends Serializable, V extends Serializable> {

  /**
   * initializes the finger table of this node using information
   * from the arbitrary node
   * 
   * @param chordNode an already existing node in the chord
   */
  public void initFingerTable(ConsistentFingerRemoteChordNode<K, V> chordNode) throws RemoteException;

  /**
   * update all other nodes in the chord network to have their finger tables
   * reflect the joining of this node to the chord network
   */
  public void updateOthers() throws RemoteException;

  // /**
  // * @param i the index of this node's finger table which may be updated
  // * @param chordNode the chordNode that might replace the ith entry of this
  // * node's finger table
  // */
  // public void updateFingerTable(int i, ConsistentFingerRemoteChordNode<K, V>
  // chordNode) throws RemoteException;

}
