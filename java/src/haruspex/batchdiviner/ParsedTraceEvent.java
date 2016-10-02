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
package haruspex.batchdiviner;

import haruspex.common.ID;
import haruspex.common.TagList;
import haruspex.common.event.ITraceEvent;

public class ParsedTraceEvent  implements ITraceEvent {
  private final long seqNum;
  private final long time;
  private final ID[] id;
  private final TagList[] tagList;

  public ParsedTraceEvent(long seqNum, long time, ID id[], TagList...tagList) {
    this.seqNum = seqNum;
    this.time = time;
    this.id = id;
    this.tagList = tagList;
  }
  
  @Override
  public long getSeqNum() {
    return seqNum;
  }
  
  @Override
  public long getTime() {
    return time;
  }
  
  @Override
  public ID[] getIDs() {
    return id.clone();
  }
  
  @Override
  public TagList[] getTagLists() {
    return tagList.clone();
  }
}
