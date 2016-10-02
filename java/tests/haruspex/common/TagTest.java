package haruspex.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TagTest {
  private final static String KEY = "key";
  private final static String VALUE = "value";
  
  @Test
  public void testInvalidName() {
    boolean gotException = false;
    try {
      Tag.of("@" + KEY, VALUE);
    } catch (IllegalArgumentException ex) {
      gotException = true;
    }
    assertTrue(gotException);
  }

  @Test
  public void testStartWithLetter() {
    boolean gotException = false;
    try {
      for (int i = 0; i < 26; i++) {
        Tag.of("" + (char)('a' + i), VALUE);
        Tag.of("" + (char)('A' + i), VALUE);
      }
    } catch (IllegalArgumentException ex) {
      gotException = true;
    }
    assertFalse(gotException);
  }

  @Test
  public void testStartWithNumber() {
    boolean gotException = false;
    try {
      for (int i = 0; i < 10; i++) {
        Tag.of("" + (char)('0' + i), VALUE);
      }
    } catch (IllegalArgumentException ex) {
      gotException = true;
    }
    assertTrue(gotException);
  }

  @Test
  public void followWithLetterNumberOrUnderscore() {
    boolean gotException = false;
    try {
      for (int i = 0; i < 36; i++) {
        if (i < 26) {
          Tag.of("a" + (char)('a' + i), VALUE);
          Tag.of("a" + (char)('A' + i), VALUE);
        } else {
          Tag.of("a" + (char)('0' + i), VALUE);
        }
      }
      Tag.of("a_", VALUE);
    } catch (IllegalArgumentException ex) {
      gotException = true;
    }
    assertFalse(gotException);
  }
  
  @Test
  public void testMultiplicity() {
    Tag t = Tag.of(KEY, VALUE);
    Tag t1 = Tag.of(t, VALUE);
    Tag t2 = Tag.of(t1, VALUE);
    assertEquals(3,  t2.getMultiplicity());
  }

  @Test
  public void testIsCore() {
    Tag t = Tag.name(VALUE);
    assertTrue(t.isCore());
  }

  @Test
  public void testIsCause() {
    Tag t = Tag.cause(GlobalID.random());
    assertTrue(t.isCause());
  }

  @Test
  public void testIsEffect() {
    Tag t = Tag.effect(GlobalID.random());
    assertTrue(t.isEffect());
  }
  
  @Test
  public void testEqual() {
    Tag t1 = Tag.of(KEY,  VALUE);
    Tag t2 = Tag.of(KEY,  VALUE);
    assertEquals(t1, t2);
  }

}
