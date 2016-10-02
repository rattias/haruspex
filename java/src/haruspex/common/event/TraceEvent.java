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

import haruspex.common.EventType;
import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.common.TagList;

public class TraceEvent implements ITraceEvent {
  private final long seqNum;
  private final long time;
  private final ID[] ids;
  private final TagList[] tagList;
  
  public TraceEvent(long seqNum, long time, ID[] ids, TagList...tagList) {
    this.seqNum = seqNum;
    this.time = time;
    this.ids = ids;
    this.tagList = tagList;
  }
  
  public long getSeqNum() {
    return seqNum;
  }
  
  public long getTime() {
    return time;
  }
  
  public ID[] getIDs() {
    return ids;
  }
  
  public TagList[] getTagLists() {
    return tagList;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String sep = "";
    sb.append("ID = {");
    for (ID id : ids) {
      sb.append(sep + id.toString());
      sep = ",";
    }
    sb.append("} ");
    for (TagList tl : tagList) {
      sb.append("Tags = {");
      Tag type = tl.get(Tag.KEY_EVENT_TYPE);
      sb.append(type + ":");
      for (Tag t : tl) {
        if (t != type) {
          sb.append(t.getKey());
          sb.append("=");
          sb.append(t.getValue());
          sb.append(" ");
        }
      }
      sb.append("} ");
    }
    return sb.toString();
  }
}
