package haruspex.producer;

import haruspex.common.ID;

/**
 * Common superclass to {@link Trace}, {@link Entity}, {@link Block}.
 * 
 */
abstract public class TraceElement {
  private final Trace trace;
  private final ID id;
  private final TraceSerializer serializer;
  private final TraceElement parent;
  
  /**
   * creates a trace element.
   * @param trace the {@link Trace) object this model element belongs to, or {@code null} if this is 
   * in fact the {@code Trace} object.
   * @param parent the parent object for this one. 
   * @param id the ID for the object
   * @param serializer the serializer used for event generation.
   */
  TraceElement(Trace trace, TraceElement parent, ID id, TraceSerializer serializer) {
    this.trace = trace == null ? (Trace)this : trace;
    this.parent = parent;
    this.id = id;
    this.serializer = serializer;
  }
  
  /**
   * @return the object ID
   */
  public ID getID() {
    return id;
  }

  /**
   * 
   * @return the {@link TraceSerializer} for this object.
   */
  public TraceSerializer getSerializer() {
    return serializer;
  }
  
  /**
   * @return the {@link Trace} instance this element belongs to or is.
   */
  public Trace getTrace() {
    return trace;
  }
  
  /**
   * @return the parent model element.
   */
  public TraceElement getParent() {
    return parent;
  }
}
