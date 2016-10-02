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

public enum  EventType {
  BEGIN_TRACE("T"),
  ANNOTATE_TRACE("aT"),
  END_TRACE("t"),
  BEGIN_ENTITY("E"),
  ANNOTATE_ENTITY("aE"),
  END_ENTITY("e"),
  BEGIN_BLOCK("B"),
  ANNOTATE_BLOCK("aB"),
  END_BLOCK("b"),
  ANNOTATE_POINT("P");
  
  class ExtTag extends Tag {
    private final EventType type;
    
    public ExtTag(EventType type, String key, String value) {
      super(key, value);
      this.type = type;
    }
    
    public String toString() {
      return "TYPE=" + type.toString();
    }
  }
  
  private final Tag tag;
  
  private EventType(String value) {
    this.tag = new ExtTag(this, Tag.KEY_EVENT_TYPE, value);
  }
  
  public Tag getTag() {
    return tag;
  }
  
  public static EventType forTag(Tag tag) {
    return ((ExtTag)tag).type;
  }

  public static Tag tagForValue(String value) {
    for (EventType t : EventType.values()) {
      if (t.tag.getValue().equals(value)) {
        return t.tag;
      }
    }
    return null;
  }

}
