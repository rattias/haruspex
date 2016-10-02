/*
 * Copyright 2016 Roberto Attias
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package haruspex.common;

import java.util.concurrent.TimeUnit;

public class Tag {
  private static final char CORE_KEY_PREFIX = '@';
  private static final char SET_KEY_PREFIX = '+';
  private static final String KEY_CAUSE = CORE_KEY_PREFIX + "I";
  private static final String KEY_EFFECT = CORE_KEY_PREFIX + "i";
  public static final String NAME = CORE_KEY_PREFIX + "!";
  public static final String KEY_EVENT_TYPE = "?";

  public static final String TIME_UNIT = CORE_KEY_PREFIX + "TIME_UNIT";
  public static final String CLOCK_DOMAIN = CORE_KEY_PREFIX + "CLOCK_DOMAIN";
  public static final String CLOCK_MAX_SKEW = CORE_KEY_PREFIX + "CLOCK_MAX_SKEW";
 
  public static final String TAG_CONTEXT = CORE_KEY_PREFIX + "T"; 

  
  public static final Tag[] NONE = new Tag[0];
  private final String key;
  private final Object value;

  private final static boolean isValidKeyStart(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private final static boolean isValidKeyCont(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9' ) || c == '_';
  }

  Tag(String key, Object value) {
    this.key = key;
    this.value = value;
  }
  
  public String getKey() {
    return key;
  }
  
  public Object getValue() {
    if (isMulti()) {
      Object[] val = (Object[])value;
      return val[val.length-1];
    } else
    return value;
  }

  public Object getValue(int idx) {
    if (isMulti()) {
      Object[] val = (Object[])value;
      return val[idx];      
    } else if (idx == 0) {
      return value;
    } else {
      throw new ArrayIndexOutOfBoundsException();
    }
  }
  
  public int getMultiplicity() {
    return isMulti() ? ((Object[])value).length : 1;
  }
  
  public boolean isCore() {
    return key.charAt(0) == CORE_KEY_PREFIX ||
        (key.length() > 1 && key.charAt(0) == SET_KEY_PREFIX && key.charAt(1) == CORE_KEY_PREFIX);
  }

  public boolean isInteger() {
    return value instanceof Long;
  }
  
  private boolean isMulti() {
    return value instanceof Object[];
  }
  
  
  public boolean isReal() {
    return value instanceof Double;
  }

  public boolean isCause() {
    return key.equals(KEY_CAUSE);
  }

  public boolean isEffect() {
    return key.equals(KEY_EFFECT);
  }

 
  public String getName() {
    int i = 0;
    for (; i < key.length() && ! isValidKeyCont(key.charAt(i)); i++) {      
    }
    return i == 0 ? key : key.substring(i);
  }
  
  static void checkKey(String key) {
    if (key == null || key.length() == 0) {
      throw new IllegalArgumentException("Invalid null or empty key");
    }
    char c = key.charAt(0);
    if (! isValidKeyStart(c)) {
      throw new IllegalArgumentException("Invalid user tag name '" + key + "': tag keys must begin with a letter, number or '_'");
    }
    for (int i = 1; i < key.length(); i++) {
      if (! isValidKeyCont(c)) {
        throw new IllegalArgumentException("Invalid user tag name '" + key + "': tag keys must contain letters, numbers or '_'");        
      }
    }
  }
  
  public static Tag of(String key, String value) {
    checkKey(key);
    return new Tag(key, value);
  }
  
  public static Tag[] of(Tag...tags) {
    return tags;
  }
  
  public static Tag of(String key, long value) {
    checkKey(key);
    return new Tag(key, value);
  }
  
  public static Tag of(Tag tag, Object additionalValue) {
    int sz = tag.getMultiplicity() + 1;
    Object[] values = new Object[sz];
    Tag result = new Tag(tag.getKey(), values);
    for (int i = 0; i < sz - 1; i++) {
      values[i] = tag.getValue(i);
    }
    values[sz - 1] = additionalValue;
    return result;
  }
  
  public static Tag name(String value) {
    return new Tag(NAME, value);
  }

  public static Tag clockTimeUnit(TimeUnit timeUnit) {
    return new Tag(TIME_UNIT, timeUnit.toString());
  }

  public static Tag clockDomain(String name) {
    return new Tag(CLOCK_DOMAIN, name);
  }
  
  public static Tag clockMaxSkew(long skew) {
    return new Tag(CLOCK_MAX_SKEW, skew);
  }
    
  public static Tag cause(ID causeId) {
    return new Tag(KEY_CAUSE, causeId);
  }
  
  public static Tag effect(ID causeId) {
    return new Tag(KEY_EFFECT, causeId);
  }
  
  public int hashCode() {
    return key.hashCode() ^ value.hashCode();
  }
  
  public boolean equals(Object o) {
    return (o instanceof Tag && ((Tag)o).key.equals(key) && ((Tag)o).value.equals(value));
  }

  public static Tag load(String key, String value) {
    return new Tag(key, value);
  }

  public static Tag load(String key, long value) {
    return new Tag(key, value);
  }

}
