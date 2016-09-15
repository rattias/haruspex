package haruspex.producer.serialization;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.common.TagList;
import haruspex.producer.StringRecordSink;
import haruspex.producer.TraceSerializer;

public class JsonSerializer implements TraceSerializer {  
  private final StringRecordSink sink;
  
  public JsonSerializer(StringRecordSink sink) {
    this.sink = sink;
  }
  
  
  private void serializeTags(JSONObject record, TagList...tags) {
    JSONObject taglist = new JSONObject();
    for (TagList tl : tags) {
      if (! tl.isEmpty()) {
        JSONObject jobj = new JSONObject();        
        for (Tag t: tl) {
          jobj.put(t.getKey(), t.getValue());
        }        
        record.put(tl.getContext(), jobj);
      }
    }
  }

  @Override
  public void serialize(
      long seqNum,
      long timestamp,
      ID[] ids,
      TagList...tags) {
    JSONObject record = new JSONObject();
    JSONArray header = new JSONArray();
    header.add((Long)seqNum);
    header.add((Long)timestamp);
    for (ID id : ids) {
      header.add(id.toString());      
    }
    record.put("h", header);
    serializeTags(record, tags);
    
    sink.put(ids[0], record.toJSONString());
  }


  @Override
  public void flush() {
    sink.flush();    
  }


  @Override
  public void close() {
    sink.close();
  }
}
