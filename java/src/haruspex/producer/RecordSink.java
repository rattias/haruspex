package haruspex.producer;

import haruspex.common.ID;

/**
 * {@link TraceSerializer}s delegate to {@code RecordSink} for actually "emitting" the string-encoded record.
 * A class implementing {@code RecordSink} represents an interface with a particular medium to which 
 * the trace should flow, such as stdout, a file, a database, an event bus such as Kafka. Different media can 
 * have different characteristics, and the RecordSink exposes some method for the higher layers to poll such
 * characteristics and make smart decisions on encoding. 
 * For example, a sink sending records over UDP sockets
 * to some other process may end up loosing data, while a sink behaving as a Kafka producer should not, under
 * normal circumstances, loose data. The isLossy() method indicates a {@code TraceSerializer} whether 
 * the medium may loose records.  
 *  
 */
public interface RecordSink {
  void flush();
  
  void close();
  
  public boolean isLossy();
}
