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

import java.util.ArrayList;
import java.util.Iterator;

public class TagList implements Iterable<Tag> {
  static final TagList EMPTY = new TagList();
  public static final Tag[] EMPTY_ARRAY = new Tag[0];
  
  private final ArrayList<Tag> tags;
  
  
  public static TagList of(Tag... tags) {
    TagList result = new TagList();
    for (int i = 0; i < tags.length; i++) {     
      result.tags.add(tags[i]);
    }
    return result;
  }
  
  public static TagList of(Tag[] someTags, Tag... otherTags) {
    TagList result = new TagList();
    for (int i = 0; i < someTags.length; i++) {     
      result.tags.add(someTags[i]);
    }
    for (int i = 0; i < otherTags.length; i++) {     
      result.tags.add(otherTags[i]);
    }
    return result;
  }
  
  
  public TagList() {
    tags = new ArrayList<Tag>();
  }
    
  public Tag get(String key) {
    for (Tag t : tags) {
      if (t.getKey().equals(key)) {
        return t;
      }
    }
    return null;
  }
  
  @Override
  public Iterator<Tag> iterator() {
    return tags.iterator();
  }

  public int size() {
    return tags.size();
  }
  
  public boolean isEmpty() {
    return tags.isEmpty();
  }  
  
  public Tag getEventType() {
    return get(Tag.KEY_EVENT_TYPE);
  }

  public Tag[] toTagArray() {   
    return tags.toArray(new Tag[tags.size()]);
  }
  
}
