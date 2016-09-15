package haruspex.producer;

import haruspex.common.ID;

public interface BytesRecordSink extends RecordSink {  
  void put(ID traceID, byte[] record);
}
