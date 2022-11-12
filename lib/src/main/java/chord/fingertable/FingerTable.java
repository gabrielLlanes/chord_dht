package chord.fingertable;

import java.io.Serializable;
import java.rmi.RemoteException;

import chord.node.RemoteChordNode;
import chord.util.Util;

public class FingerTable<K extends Serializable, V extends Serializable, T extends RemoteChordNode<K, V, T>> {

  private final int id;

  private final Object[] fingerTable;

  public FingerTable(int degree, int id) {
    this.id = id;
    this.fingerTable = new Object[degree + 1];
  }

  public FingerTable(int degree, int id, Object[] fingerTable) {
    this.id = id;
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
          s.append(
              String.format("%d successor: %d", (id + Util.powerOf2(i - 1)) % Util.powerOf2((fingerTable.length - 1)),
                  ((T) fingerTable[i]).getId()));
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
