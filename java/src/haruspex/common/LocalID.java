package haruspex.common;

public class LocalID implements ID {
  public static final LocalID GHOST = new LocalID(-1);
  
  private long id;

  public LocalID(long id) {
    this.id = id;
  }
  
  public long toLong() {
    return id;
  }
  
  public String toString() {
    return Long.toHexString(id);
  }
}

