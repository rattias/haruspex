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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haruspex.common.ID;
import haruspex.common.TagList;

public class InMemoryStorageSink implements IEventSink {
  private final static Logger logger = LoggerFactory.getLogger(InMemoryStorageSink.class);
  
  private final Map<ID, List<ITraceEvent>> map;
  
  public InMemoryStorageSink() {
    map = new HashMap<ID, List<ITraceEvent>>();
  }

  public InMemoryStorageSink(int maxCount) {
    map = new LinkedHashMap<ID, List<ITraceEvent>>(maxCount, .75f, true) {    
      @Override
      protected boolean removeEldestEntry(Map.Entry<ID, List<ITraceEvent>> eldest) {
        if (size() >= maxCount) {
          remove(eldest.getKey());
        }
        return false;
      }
    };
  }

  @Override
  public void put(
    long seqNum,
    long timeStamp,
    ID[] ids,
    TagList...tags
    ) {
    List<ITraceEvent> list = map.get(ids[0]);
    if (list == null) {
      list = new ArrayList<>();
      map.put(ids[0], list);
    }
    list.add(new TraceEvent(seqNum, timeStamp, ids, tags));
  }
  
  @Override
  public void flush() {
  }
  
  @Override
  public void close() {
  }
  
  public List<ITraceEvent> getEvents(ID traceID) {
    return map.get(traceID);
  }

}
