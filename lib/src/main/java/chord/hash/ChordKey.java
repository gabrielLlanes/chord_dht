package chord.hash;

import chord.util.Util;

/**
 * A class for restricting hashcode to be less than a
 * certain power of 2 for use within a chord network
 */
public abstract class ChordKey {

  private final int base;

  public ChordKey(int degree) {
    this.base = Util.powerOf2(degree);
  }

  protected abstract int _hashCode();

  @Override
  public int hashCode() {
    int hashCodeAbs = Math.abs(_hashCode());
    return hashCodeAbs >= base ? hashCodeAbs % base : hashCodeAbs;
  }

}
