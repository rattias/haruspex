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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TraceEvents<T> implements Iterable<T> {
  private ID traceId;
  private int shard;
  private List<T> events;
  
  public TraceEvents(ID traceId, int shard, List<T> events) {
    this.traceId = traceId;
    this.shard = shard;
    this.events = events;
  }
  
  public ID getTraceID() {
    return traceId;
  }
  
  public int getShard() {
    return shard;
  }

  @Override
  public Iterator<T> iterator() {
    return events.iterator();
  }
  
  public List<T> getEvents() {
    return Collections.unmodifiableList(events);
  }
  
  public int getEventCount() {
    return events.size();
  }
   
}
