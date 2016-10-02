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
package haruspex.common.event;

import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.common.TagList;

public interface ITraceEvent {
  public enum Context {
    TRACE, ENTITY, BLOCK, POINT, INTERACTION
  }
  public enum Action {
    BEGIN, END, ANNOTATE
  }
  long getSeqNum();
  long getTime();  
  ID[] getIDs();  
  TagList[] getTagLists();
  
  default String getCauseIdAsString() {
    if (getIDs().length < 3) {
      return null;
    }
    for (TagList tl : getTagLists()) {
      for (Tag tag : tl) {
        if (tag.isCause())
          return tag.getValue().toString();
      }
    }
    return null;
  }


  default String getEffectIdAsString() {
    if (getIDs().length < 3) {
      return null;
    }
    for (TagList tl : getTagLists()) {
      for (Tag tag : tl) {
        if (tag.isEffect())
          return tag.getValue().toString();
      }
    }
    return null;
  }
  
  default ID getEntityId() {
    if (getIDs().length < 2) {
      return null;
    }
    return getIDs()[1];
  }

}
