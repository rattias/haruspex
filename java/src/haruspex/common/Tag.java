package haruspex.common;

public class Tag {
  public static final Tag[] NONE = new Tag[0];
  private final String key;
  private final Object value;
  
  Tag(String key, Object value) {
    this.key = key;
    this.value = value;
  }
  
  public String getKey() {
    return key;
  }
  
  public Object getValue() {
    return value;
  }
    
  public static Tag of(String key, Object value) {
    return new Tag(key, value);
  }
}
