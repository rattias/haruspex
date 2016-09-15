package haruspex.producer;

/**
 * When a trace is created a sampling policy associated to it decides whether the various producer API
 * calls for this trace (and all of its sub-elements) should result in records generated onto the 
 * {@link TraceSerializer} and {@link RecordSink}.
 * 
 */
public interface SamplingPolicy {
  boolean shouldTrace();
}
