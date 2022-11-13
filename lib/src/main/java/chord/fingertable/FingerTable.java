package chord.fingertable;

import java.io.Serializable;

import chord.node.RemoteChordNode;

/**
 * Finger table interface for a node. A finger table should have its predecessor
 * as the 0th entry, and for each i with 1 <= i <= degree of chord,
 * the ith entry should be successor(node.id + 2^(i-1))
 */
public interface FingerTable<K extends Serializable, V extends Serializable, T extends RemoteChordNode<K, V, T>> {

  public int size();

  public void set(int index, T chordNode);

  public T get(int index);
}
