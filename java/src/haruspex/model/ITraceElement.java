package haruspex.model;

import java.util.Iterator;

import haruspex.common.ID;
import haruspex.common.Tag;

public interface ITraceElement {
  ID getID();
  ITrace getTrace();
  ITraceElement getParent();
  Iterator<Tag> tags();
  int tagCount();
}
