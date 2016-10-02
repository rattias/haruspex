package haruspex.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IDTest {
  private final static long A_CONST = 0xCAFEBABEABADDEAFL; 
  
  @Test
  public void globalID() {
    GlobalID id = GlobalID.of(A_CONST);
    String idStr = id.toString();
    id = GlobalID.of(idStr);
    assertEquals(id.toLong(), A_CONST);
  }

}
