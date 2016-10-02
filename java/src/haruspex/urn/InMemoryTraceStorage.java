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
package haruspex.urn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import haruspex.common.ID;
import haruspex.common.TraceEvents;

public class InMemoryTraceStorage implements TraceStorage {
  private final Map<ID, List<byte[]>> cache = new HashMap<>();
  
  @Override
  public void storeBytes(TraceEvents<byte[]> events) throws IOException {
    ID id = events.getTraceID();
    List<byte[]> trace = cache.get(id);
    if (trace == null) {
      trace = new ArrayList<>();
      cache.put(id, trace);      
    }
    trace.addAll(events.getEvents());    
  }

  @Override
  public void storeStrings(TraceEvents<String> events) throws IOException {
    ID id = events.getTraceID();
    List<byte[]> trace = cache.get(id);
    if (trace == null) {
      trace = new ArrayList<>();
      cache.put(id, trace);      
    }
    for (String str : events.getEvents()) {
      trace.add(str.getBytes());
    }
  }
  
  @Override
  public TraceEvents<byte[]> loadBytes(ID traceId) throws IOException {
    List<byte[]> events = cache.get(traceId);
    return new TraceEvents<byte[]>(traceId, -1, events);
  }

  @Override
  public TraceEvents<String> loadStrings(ID traceId) throws IOException {
    List<byte[]> events = cache.get(traceId);
    List<String> strEvents = new ArrayList<String>(events.size());
    for (byte[] arr : events) {
      strEvents.add(new String(arr));
    }
    return new TraceEvents<String>(traceId, -1, strEvents);
  }

}
