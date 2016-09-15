package haruspex.common;

public class  CoreTags {
  public static final String SCOPE= "@a";
  public static final String TRACE = "@T";
  public static final String ENTITY = "@E";
  public static final String BLOCK = "@B";
  public static final String CLOSE = "@x";
  public static final String POINT = "@P";
  public static final String CAUSE = "@C";
  public static final String EFFECT = "@e";
  public static final String TAG = "@t";
  public static final String SEQ_NUM = "@N";
  public static final String TIMESTAMP = "@s";
  public static final String NAME = "@n";
  public static final String TIME_UNIT = "TIME_UNIT";
  public static final String CLOCK_DOMAIN = "CLOCK_DOMAIN";
  public static final String CLOCK_MAX_SKEW = "CLOCK_MAX_SKEW";
  
  public static Tag name(String value) {
    return new Tag(NAME, value);
  }
  
  public static Tag cause(ID id) {
    return new Tag(CAUSE, id);
  }

  public static Tag effect(ID id) {
    return new Tag(EFFECT, id);
  }
  
  public static Tag endBlock() {
    return new Tag(CLOSE, BLOCK);
  }

  public static Tag endEntity() {
    return new Tag(CLOSE, ENTITY);
  }

  public static Tag scope(String scope) {    
    return new Tag(SCOPE, scope);
  }
}
