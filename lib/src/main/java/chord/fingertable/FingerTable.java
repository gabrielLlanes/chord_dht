package chord.fingertable;

import java.io.Serializable;
import java.rmi.RemoteException;

import chord.node.RemoteChordNode;

public class FingerTable<K extends Serializable, V extends Serializable, T extends RemoteChordNode<K, V, T>> {

  private final Object[] fingerTable;

  public FingerTable(int degree) {
    this.fingerTable = new Object[degree + 1];
  }

  public FingerTable(int degree, Object[] fingerTable) {
    if (fingerTable.length != degree + 1) {
      throw new IllegalArgumentException("Finger table should be one greater than the degree.");
    }
    this.fingerTable = fingerTable;
  }

  public int size() {
    return fingerTable.length;
  }

  @SuppressWarnings("unchecked")
  public synchronized T get(int index) {
    return (T) fingerTable[index];
  }

  public synchronized void set(int index, T chordNode) {
    fingerTable[index] = chordNode;
  }

  @Override
  @SuppressWarnings("unchecked")
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append('[');
    for (int i = 0; i < fingerTable.length; i++) {
      if (fingerTable[i] == null) {
        s.append("null");
      } else {
        try {
          s.append(((T) fingerTable[i]).getId());
        } catch (RemoteException e) {
          return "error occurred in toString()";
        }
      }
      if (i == fingerTable.length - 1) {
      } else {
        s.append(", ");
      }
    }
    s.append(']');
    return s.toString();
  }
}
