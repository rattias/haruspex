package haruspex.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import haruspex.common.ID;
import haruspex.common.Tag;

abstract public class ImmutableTraceElement implements ITraceElement {
  private final ImmutableTrace trace;
  private final ID id;
  private final ImmutableTraceElement parent;
  private List<Tag> tags;
  
  ImmutableTraceElement(ImmutableTrace trace, ImmutableTraceElement parent, ID id, List<Tag> tags) {
    this.trace = trace == null ? (ImmutableTrace)this : trace;
    this.parent = parent;
    this.id = id;
    this.tags = Collections.unmodifiableList(tags);
  }
  
  public ID getID() {
    return id;
  }

  public ImmutableTrace getTrace() {
    return trace;
  }
  
  public ImmutableTraceElement getParent() {
    return parent;
  }
  
  public Iterator<Tag> tags() {
    return tags.iterator();
  }
  
  public int tagCount() {
    return tags.size();
  }
}
