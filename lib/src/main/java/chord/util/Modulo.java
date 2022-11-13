package chord.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for operations modulo a certain modulus.
 */
public class Modulo {

  private int modulus;

  public Modulo(int modulus) {
    this.modulus = modulus;
  }

  // a <= n < b mod modulus
  public boolean inLeftHalfClosed(int n, int a, int b) {
    if (a == b)
      return false;
    else if (a < b) {
      return n >= a && n < b;
    } else {
      return (n >= a && n < modulus) || (n >= 0 && n < b);
    }
  }

  // a < n <= b mod modulus
  public boolean inRightHalfClosed(int n, int a, int b) {
    if (a == b)
      return false;
    else if (a < b) {
      return n > a && n <= b;
    } else {
      return (n > a && n < modulus) || (n >= 0 && n <= b);
    }
  }

  // a < n < b mod modulus
  public boolean inOpen(int n, int a, int b) {
    if (a == b)
      return false;
    else if (a < b) {
      return n > a && n < b;
    } else {
      return (n > a && n < modulus) || (n >= 0 && n < b);
    }
  }

  // a <= n <= b mod modulus
  public boolean inClosed(int n, int a, int b) {
    if (a == b)
      return n == a;
    else if (a < b) {
      return n >= a && n <= b;
    } else {
      return (n >= a && n < modulus) || (n >= 0 && n <= b);
    }
  }

  public Iterable<Integer> intervalIterable(int a, int b) {
    List<Integer> ints = new ArrayList<>();
    if (a <= b) {
      for (int i = a; i <= b; i++) {
        ints.add(i);
      }
    } else {
      for (int i = a; i < modulus; i++) {
        ints.add(i);
      }
      for (int i = 0; i <= b; i++) {
        ints.add(i);
      }
    }
    return ints;
  }

  // n mod modulus
  public int mod(int n) {
    if (n >= 0) {
      return n % modulus;
    } else {
      return mod(modulus + n);
    }
  }

  // hashcode mod modulus
  public int hashCode(Object o) {
    int hashCodeAbs = Math.abs(o.hashCode());
    return hashCodeAbs >= modulus ? hashCodeAbs % modulus : hashCodeAbs;
  }

  // hashcode mod modulus
  public static int hashCode(Object o, int modulus) {
    int hashCodeAbs = Math.abs(o.hashCode());
    return hashCodeAbs >= modulus ? hashCodeAbs % modulus : hashCodeAbs;
  }

}
