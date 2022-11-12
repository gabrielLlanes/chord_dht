package chord.node.consistentfinger;

import java.io.Serializable;
import java.rmi.RemoteException;

import chord.node.AbstractChordNode;
import chord.util.Util;

public class ConsistentFingerChordNodeImpl<K extends Serializable, V extends Serializable>
    extends AbstractChordNode<K, V, ConsistentFingerRemoteChordNode<K, V>>
    implements ConsistentFingerRemoteChordNode<K, V>, ConsistentFingerChordNode<K, V> {

  public ConsistentFingerChordNodeImpl(int degree, int id) throws RemoteException {
    super(degree, id);
  }

  public ConsistentFingerChordNodeImpl(int degree, int id, boolean initial) throws RemoteException {
    super(degree, id, initial);
  }

  @Override
  public void initFingerTable(ConsistentFingerRemoteChordNode<K, V> chordNode) throws RemoteException {
    ConsistentFingerRemoteChordNode<K, V> successor = chordNode.findSuccessor(getId());
    ConsistentFingerRemoteChordNode<K, V> predecessor = successor.getImmediatePredecessor();
    setImmediatePredecessor(predecessor);
    setImmediateSuccessor(successor);
    /* this new node can do a trivial update for its successor and predecessor. */
    predecessor.setImmediateSuccessor(this);
    successor.setImmediatePredecessor(this);
    /*
     * setting the finger table entries of this node using the previous entry,
     * if applicable. if not applicable, ask the arbitrary node for the successor of
     * the current finger table "start" position (i.e, this.id + 2^i-1 for finger
     * table entry i)
     */
    for (int i = 2; i <= degree; i++) {
      int currFingerStart = (id + Util.powerOf2(i - 1)) % modulus;
      ConsistentFingerRemoteChordNode<K, V> prevFinger = fingerTable.get(i - 1);
      int prevFingerId = prevFinger.getId();
      if (modulo.inOpen(currFingerStart, id, prevFingerId) || (id == prevFingerId)) {
        fingerTable.set(i, prevFinger);
      } else {
        fingerTable.set(i, chordNode.findSuccessor(currFingerStart));
      }
    }

  }

  @Override
  public void updateOthers() throws RemoteException {
    for (int i = 1; i <= degree; i++) {
      /*
       * predecessor(id - 2^(i-1)) is the first node whose ith finger table
       * entry MIGHT be updated with this node.
       */
      ConsistentFingerRemoteChordNode<K, V> nodeToCheck = findPredecessor(modulo.mod(getId() - Util.powerOf2(i - 1)));
      nodeToCheck.updateFingerTable(i, this);
    }
  }

  @Override
  public void updateFingerTable(int i, ConsistentFingerRemoteChordNode<K, V> chordNode) throws RemoteException {
    int chordNodeId = chordNode.getId();
    int fingerTableIthId = fingerTable.get(i).getId();
    /*
     * only update the fingerTable(i) if chordNode.id < fingerTable(i)
     */
    if (modulo.inOpen(chordNodeId, id, fingerTableIthId) || id == fingerTableIthId) {
      fingerTable.set(i, chordNode);
      /*
       * predecessor may need to apply the update only if this node applied the
       * update.
       */
      getImmediatePredecessor().updateFingerTable(i, chordNode);
    }
  }

  @Override
  public void join(ConsistentFingerRemoteChordNode<K, V> chordNode) throws RemoteException {
    // initialize the finger table of this node
    initFingerTable(chordNode);
    // and then update the other nodes of this node's entry into the chord.
    updateOthers();
  }

  @Override
  protected ConsistentFingerRemoteChordNode<K, V> self() {
    return this;
  }

}
