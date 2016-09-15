package haruspex.producer;

import java.io.IOException;

import haruspex.common.ID;
import haruspex.common.TagList;

/**
 * Whenever a {@link Trace}, {@link Entity}, {@link Block} is instantiated, closed or tagged, an
 * event is emitted onto a {@code TraceSerializer}. The {@link #serialize} method is responsible for
 * encoding the arguments it was passed as a string and pass it to the {@link RecordSink#put} method
 * of a {@link RecordSink} instance.
 */
public interface TraceSerializer {
  public final static TraceSerializer GHOST = new TraceSerializer() {    
    public void serialize(
        long seqNum,
        long timeStamp,
        ID[] context,
        TagList...tags
        ){}
    public void flush(){}
    public void close(){} 
  };
  
  public void serialize(
      long seqNum,
      long timeStamp,
      ID[] context,
      TagList...tags
      );
  public void flush();
  public void close();
}
