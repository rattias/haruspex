package haruspex.batchdiviner;

import haruspex.common.ID;
import haruspex.common.TagList;

public interface TraceEvent {
  long getSeqNum();
  public long getTime();  
  public int getIdLength();  
  public ID getIdElement(int index);
  public int getTagListCount();
  public TagList getTagList(int index);
}
