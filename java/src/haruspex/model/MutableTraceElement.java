package haruspex.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.common.TagList;

abstract public class MutableTraceElement implements ITraceElement {
  private final static ArrayList<Tag> EMPTY_LIST = new ArrayList<>();
  private final MutableTrace trace;
  private final ID id;
  private final MutableTraceElement parent;
  private ArrayList<Tag> tags = EMPTY_LIST;
  
  MutableTraceElement(MutableTrace trace, MutableTraceElement parent, ID id) {
    this(trace, parent, id, TagList.EMPTY_ARRAY);
  }

  MutableTraceElement(MutableTrace trace, MutableTraceElement parent, ID id, Tag...tags) {
    this.trace = trace == null ? (MutableTrace)this : trace;
    this.parent = parent;
    this.id = id;
    addAll(tags);
  }
  
  public ID getID() {
    return id;
  }

  public MutableTrace getTrace() {
    return trace;
  }
  
  public MutableTraceElement getParent() {
    return parent;
  }
  
  public void addAll(Tag...tags) {
    if (this.tags == EMPTY_LIST) {
      this.tags = new ArrayList<Tag>();
    }
    this.tags.addAll(Arrays.asList(tags));
  }

  @Override
  public Iterator<Tag> tags() {
    return tags.iterator();
  }
  
  protected List<Tag> getTags() {
    return tags;
  }
  
  @Override
  public int tagCount() {
    return tags.size();
  }
}
