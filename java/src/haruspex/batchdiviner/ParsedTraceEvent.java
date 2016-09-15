package haruspex.batchdiviner;

import haruspex.common.ID;
import haruspex.common.TagList;

public class ParsedTraceEvent  implements TraceEvent {
  private final long seqNum;
  private final long time;
  private final ID[] id;
  private final TagList[] tagList;

  public ParsedTraceEvent(long seqNum, long time, ID id[], TagList...tagList) {
    this.seqNum = seqNum;
    this.time = time;
    this.id = id;
    this.tagList = tagList;
  }
  
  @Override
  public long getSeqNum() {
    return seqNum;
  }
  
  @Override
  public long getTime() {
    return time;
  }
  
  @Override
  public int getIdLength() {
    return id.length;
  }
  
  @Override
  public ID getIdElement(int index) {
    return id[index];
  }
  
  @Override
  public int getTagListCount() {
    return tagList.length;
  }
  
  @Override
  public TagList getTagList(int index) {
    return tagList[index];
  }
}
