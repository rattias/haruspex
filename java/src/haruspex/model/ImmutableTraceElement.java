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
package haruspex.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import haruspex.common.Tag;
import haruspex.common.TagList;

abstract public class ImmutableTraceElement implements ITraceElement {
  private final static HashMap<String, Tag> NO_TAGS = new HashMap<>();
  private final ImmutableTraceElement parent;
  
  private HashMap<String, Tag> tags = NO_TAGS;
  
  ImmutableTraceElement(ImmutableTraceElement parent, Tag...tags) {
    this.parent = parent;
    addAll(tags);
  }
  

  public ImmutableTrace getTrace() {
    ITraceElement el; 
    for (el = this; !(el instanceof ImmutableTrace); el = el.getParent()) {      
    }
    return (ImmutableTrace)el;
  }
  
  public ImmutableTraceElement getParent() {
    return parent;
  }
  
  public void addAll(Tag...tags) {
    if (this.tags == NO_TAGS) {
      this.tags = new HashMap<>();
    }
    for (Tag tag : tags) {
      Tag previous = this.tags.get(tag.getKey());
      if (previous == null) {
        this.tags.put(tag.getKey(), tag);
      } else {
        this.tags.put(tag.getKey(), Tag.of(previous, tag.getValue()));
      }
    }
  }

  public void addAll(TagList...tls) {
    for (TagList tl : tls) {
      addAll(tl.toTagArray());
    }
  }

  
  @Override
  public Iterator<Tag> getTagIterator() {
    return tags.values().iterator();
  }
  
//  protected HashMap<String, Tag> getTags() {
//    return tags;
//  }
//  
  @Override
  public int getTagCount() {
    return tags.size();
  }

  public void remove(String key) {
    tags.remove(key);
  }
  
  public Object getTagValue(String key) {
    Tag t = tags.get(key);
    if (t == null) {
      return null;
    }
    Object v = t.getValue();
    if (v instanceof List) {
      return ((List<Object>)v).get(0);
    } else {
      return v;
    }
  }
  
  public int getTagValueCount(String key) {
    Tag t = tags.get(key);
    if (t == null) {
      return 0;
    }
    Object v = t.getValue();
    if (v instanceof List) {
      return ((List)v).size();
    } else if (v != null) {
      return 1;
    } else {
      return 0;
    }    
  }
  
  public Object getTagValue(String key, int idx) {
    Object t = tags.get(key);
    if (t == null) {
      throw new NoSuchElementException();
    }
    Object v = tags.get(key).getValue();
    if (v instanceof List) {
      return ((List)v).get(idx);
    } else if (v != null && idx == 0) {
      return v;
    } else {
      throw new NoSuchElementException();
    }
  }
}
