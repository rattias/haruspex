package haruspex.producer;

import haruspex.common.ID;

public interface StringRecordSink extends RecordSink {  
  void put(ID traceID, String record);
}
