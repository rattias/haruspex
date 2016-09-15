package haruspex.common;

import java.util.Base64;
import java.util.Random;

public class GlobalID implements ID {
  public final static GlobalID GHOST = new GlobalID(-1);
  
  private final static Random random = new Random();
  private final static Base64.Encoder b64enc = Base64.getEncoder().withoutPadding();
  private long id;

  public GlobalID(long id) {
    this.id = id;
  }
  
  public long toLong() {
    return id;
  }
  
  public String toString() {
	byte[] b = new byte[8];
	for (int i = 0; i < 8; i++) {
		b[i] = (byte)((id >>> i) & 0xFF);
	}
    return b64enc.encodeToString(b);
  }
  
  public static GlobalID random() {
    return new GlobalID(random.nextLong());
  }
}

