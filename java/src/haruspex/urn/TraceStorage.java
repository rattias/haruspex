package haruspex.urn;

import java.io.IOException;

import haruspex.batchdiviner.TraceEvents;
import haruspex.common.ID;

public interface TraceStorage {
  void store(TraceEvents events) throws IOException;
  TraceEvents load(ID traceId) throws IOException;
}
