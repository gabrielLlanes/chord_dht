package chord.util;

/**
 * utility class for operations modulo a certain base.
 */
public class Modulo {
  private int base;

  public Modulo(int base) {
    this.base = base;
  }

  // a <= n < b mod base
  public boolean inLeftHalfClosed(int n, int a, int b) {
    if (a == b)
      return false;
    else if (a < b) {
      return n >= a && n < b;
    } else {
      return (n >= a && n < base) || (n >= 0 && n < b);
    }
  }

  // a < n <= b mod base
  public boolean inRightHalfClosed(int n, int a, int b) {
    if (a == b)
      return false;
    else if (a < b) {
      return n > a && n <= b;
    } else {
      return (n > a && n < base) || (n >= 0 && n <= b);
    }
  }

  // a < n < b mod base
  public boolean inOpen(int n, int a, int b) {
    if (a == b)
      return false;
    else if (a < b) {
      return n > a && n < b;
    } else {
      return (n > a && n < base) || (n >= 0 && n < b);
    }
  }

  // a <= n <= b mod base
  public boolean inClosed(int n, int a, int b) {
    if (a == b)
      return n == a;
    else if (a < b) {
      return n >= a && n <= b;
    } else {
      return (n >= a && n < base) || (n >= 0 && n <= b);
    }
  }

  // n mod base
  public int mod(int n) {
    if (n >= 0) {
      return n % base;
    } else {
      return mod(base + n);
    }
  }

  // hashcode mod base
  public int hashCode(Object o) {
    int hashCodeAbs = Math.abs(o.hashCode());
    return hashCodeAbs >= base ? hashCodeAbs % base : hashCodeAbs;
  }

  // hashcode mod base
  public static int hashCode(Object o, int base) {
    int hashCodeAbs = Math.abs(o.hashCode());
    return hashCodeAbs >= base ? hashCodeAbs % base : hashCodeAbs;
  }

}
