package chord.node.consistentfinger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import chord.node.RemoteChordNode;

public class ConsistentFingerTest {
  @Test
  public void test() throws RemoteException {
    ConsistentFingerRemoteChordNode<String, String> c1 = new ConsistentFingerChordNodeImpl<>(5, 1, true);
    ConsistentFingerChordNodeImpl<String, String> c4 = new ConsistentFingerChordNodeImpl<>(5, 4);
    ConsistentFingerChordNodeImpl<String, String> c9 = new ConsistentFingerChordNodeImpl<>(5, 9);
    ConsistentFingerChordNodeImpl<String, String> c11 = new ConsistentFingerChordNodeImpl<>(5, 11);
    ConsistentFingerChordNodeImpl<String, String> c14 = new ConsistentFingerChordNodeImpl<>(5, 14);
    ConsistentFingerChordNodeImpl<String, String> c18 = new ConsistentFingerChordNodeImpl<>(5, 18);
    ConsistentFingerChordNodeImpl<String, String> c20 = new ConsistentFingerChordNodeImpl<>(5, 20);
    ConsistentFingerChordNodeImpl<String, String> c21 = new ConsistentFingerChordNodeImpl<>(5, 21);
    ConsistentFingerChordNodeImpl<String, String> c28 = new ConsistentFingerChordNodeImpl<>(5, 28);

    c4.join(c1);
    c28.join(c4);
    c21.join(c28);
    c20.join(c28);
    c11.join(c28);
    c18.join(c28);
    c14.join(c28);
    c9.join(c28);

    printNodes(c1, c4, c9, c11, c14, c18, c20, c21, c28);
  }

  private void printNodes(RemoteChordNode<?, ?, ?>... chordNodes) {
    for (RemoteChordNode<?, ?, ?> chordNode : chordNodes) {
      System.out.println(chordNode);
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void transferKeys() throws IOException {
    ConsistentFingerRemoteChordNode<String, String> c1 = new ConsistentFingerChordNodeImpl<>(5, 1, true);
    ConsistentFingerChordNodeImpl<String, String> c28 = new ConsistentFingerChordNodeImpl<>(5, 28);
    Map<String, String> added = new HashMap<>();
    c28.join(c1);
    Random rg = new Random();
    for (int i = 0; i < 1000; i++) {
      String key = RandomString.getRandomASCIIString(25 + rg.nextInt(26));
      String val = RandomString.getRandomASCIIString(25 + rg.nextInt(26));
      c1.put(key, val);
      added.put(key, val);
    }
    ConsistentFingerChordNodeImpl<String, String> c14 = new ConsistentFingerChordNodeImpl<>(5, 14);
    c14.join(c28);
    for (int i = 0; i < 1000; i++) {
      String key = RandomString.getRandomASCIIString(25 + rg.nextInt(26));
      String val = RandomString.getRandomASCIIString(25 + rg.nextInt(26));
      c14.put(key, val);
      added.put(key, val);
    }
    ConsistentFingerChordNodeImpl<String, String> c17 = new ConsistentFingerChordNodeImpl<>(5, 17);
    c17.join(c14);
    for (int i = 0; i < 1000; i++) {
      String key = RandomString.getRandomASCIIString(25 + rg.nextInt(26));
      String val = RandomString.getRandomASCIIString(25 + rg.nextInt(26));
      c17.put(key, val);
      added.put(key, val);
    }
    ConsistentFingerChordNodeImpl<String, String> c26 = new ConsistentFingerChordNodeImpl<>(5, 26);
    c26.join(c28);
    ConsistentFingerChordNodeImpl<String, String> c7 = new ConsistentFingerChordNodeImpl<>(5, 7);
    c7.join(c26);

    Object[] nodes = new Object[] { c1, c7, c14, c17, c26, c28 };
    for (Entry<String, String> entry : added.entrySet()) {
      assertEquals(entry.getValue(),
          ((RemoteChordNode<String, String, ConsistentFingerRemoteChordNode<String, String>>) nodes[rg
              .nextInt(nodes.length)]).lookup(entry.getKey()));
    }

  }

  @Test
  @SuppressWarnings("unchecked")
  public void hashTableOperations() throws Exception {
    ConsistentFingerRemoteChordNode<String, String> c1 = new ConsistentFingerChordNodeImpl<>(5, 1, true);
    ConsistentFingerChordNodeImpl<String, String> c4 = new ConsistentFingerChordNodeImpl<>(5, 4);
    ConsistentFingerChordNodeImpl<String, String> c9 = new ConsistentFingerChordNodeImpl<>(5, 9);
    ConsistentFingerChordNodeImpl<String, String> c11 = new ConsistentFingerChordNodeImpl<>(5, 11);
    ConsistentFingerChordNodeImpl<String, String> c14 = new ConsistentFingerChordNodeImpl<>(5, 14);
    ConsistentFingerChordNodeImpl<String, String> c18 = new ConsistentFingerChordNodeImpl<>(5, 18);
    ConsistentFingerChordNodeImpl<String, String> c20 = new ConsistentFingerChordNodeImpl<>(5, 20);
    ConsistentFingerChordNodeImpl<String, String> c21 = new ConsistentFingerChordNodeImpl<>(5, 21);
    ConsistentFingerChordNodeImpl<String, String> c28 = new ConsistentFingerChordNodeImpl<>(5, 28);

    c4.join(c1);
    c28.join(c4);
    c21.join(c28);
    c20.join(c28);
    c11.join(c28);
    c18.join(c28);
    c14.join(c28);
    c9.join(c28);

    c4.put("key1", "val1");
    assertEquals(c9.lookup("key1"), "val1");

    c20.put("asdfasdfasdf", "fdsafdsafdsa");
    assertEquals(c20.lookup("asdfasdfasdf"), "fdsafdsafdsa");

    c11.put("whyhellothere", "ohidontthinkso");
    assertEquals(c28.lookup("whyhellothere"), "ohidontthinkso");

    c4.remove("key1");
    c28.remove("asdfasdfasdf");
    c14.remove("whyhellothere");

    assertNull(c11.lookup("key1"));
    assertNull(c4.lookup("asdfasdfasdf"));
    assertNull(c18.lookup("whyhellothere"));

    Object[] nodes = new Object[] { c1, c4, c9, c11, c14, c18, c20, c21, c28 };
    Random rg = new Random();
    for (int i = 0; i < 10_000; i++) {
      String key = RandomString.getRandomASCIIString(50 + rg.nextInt(50));
      String val = RandomString.getRandomASCIIString(50 + rg.nextInt(50));
      int putNode = rg.nextInt(nodes.length);
      int lookupNode = rg.nextInt(nodes.length);
      /*
       * System.out.print(String.format("Key %d: %s", i, key));
       * System.out.print(String.format("Val %d: %s", i, val));
       * System.out.println();
       * System.out.print(String.format("Putting on node %d, looking up on node %d",
       * putNode, lookupNode));
       */
      ((ConsistentFingerRemoteChordNode<String, String>) nodes[putNode]).put(key, val);
      assertEquals(((ConsistentFingerRemoteChordNode<String, String>) nodes[lookupNode]).lookup(key), val);
      // Thread.sleep(0, 1);
    }
  }
}
