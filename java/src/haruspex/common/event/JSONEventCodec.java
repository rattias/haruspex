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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import haruspex.common.EventType;
import haruspex.common.GlobalID;
import haruspex.common.ID;
import haruspex.common.LocalID;
import haruspex.common.Tag;
import haruspex.common.TagList;

@SuppressWarnings("unchecked")
public class JSONEventCodec implements IEventCodec {
  
  private final static String HEADER_KEY = "h";
  private final static String TAGS_KEY = "t";

  private void encodeTags(JSONObject record, TagList... tags) {
    JSONArray jTags = new JSONArray();
    for (TagList tl : tags) {
      JSONObject jobj = new JSONObject(new LinkedHashMap<String, Object>());
      for (Tag t : tl) {
        jobj.put(t.getKey(), t.getValue());
      }
      jTags.add(jobj);
    }
    record.put(TAGS_KEY, jTags);
  }

  @Override
  public String encode(long seqNum, long timestamp, ID[] ids, TagList... tags) throws CodecException {
    JSONObject record = new JSONObject(new LinkedHashMap<String, Object>());
    JSONArray header = new JSONArray();
    header.add((Long) seqNum);
    header.add((Long) timestamp);
    for (ID id : ids) {
      header.add(id.toString());
    }
    record.put(HEADER_KEY, header);
    encodeTags(record, tags);
    return record.toJSONString();
  }

  @Override
  public ITraceEvent decode(String encodedEvent) throws CodecException {
    JSONParser p = new JSONParser();
    JSONObject jobj;
    try {
      jobj = (JSONObject) p.parse(encodedEvent);
    } catch (ParseException e) {
      throw new CodecException(e);
    }
    JSONArray header = (JSONArray) jobj.get(HEADER_KEY);
    long seqNum = (long) header.get(0);
    long timestamp = (long) header.get(1);
    ID[] ids = new ID[header.size() - 2];
    for (int i = 0; i < ids.length; i++) {
      ids[i] = i < 2 ? 
          GlobalID.of((String)header.get(2 + i)) :
          LocalID.of(Long.parseLong((String)header.get(2 + i)));
    }
    JSONArray jTags = (JSONArray) jobj.get(TAGS_KEY);
    TagList[] tlist = new TagList[jTags.size()];
    for (int i = 0; i < tlist.length; i++) {
      JSONObject jtagSet = (JSONObject) jTags.get(i);
      Tag[] tags = new Tag[jtagSet.size()];
      int j = 0;
      for (Iterator<Map.Entry<String, String>> it = jtagSet.entrySet().iterator(); it.hasNext();) {
        Map.Entry<String, String> entry = it.next();
        String key = entry.getKey();
        Object v = entry.getValue();
        if (key.equals(Tag.KEY_EVENT_TYPE)) {
          tags[j++] = EventType.tagForValue((String)v);
        } else {
          tags[j++] = v instanceof String ? Tag.load(key, (String)v) : Tag.load(key, (long)v);
        }
      }
      tlist[i] = TagList.of(tags);
    }
    return new TraceEvent(seqNum, timestamp, ids, tlist);
  }

}
