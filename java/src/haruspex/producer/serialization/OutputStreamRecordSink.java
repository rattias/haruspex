package haruspex.producer.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import haruspex.common.ID;
import haruspex.producer.BytesRecordSink;
import haruspex.producer.StringRecordSink;

public class OutputStreamRecordSink implements BytesRecordSink {
  private final OutputStream os;
  
  public OutputStreamRecordSink(OutputStream os) {
    this.os = os;
  }

  @Override
  public void put(ID traceID, byte[] record) {
    try {
      os.write(record);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  @Override
  public void flush() {
    try {
      os.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void close() {
    try {
      os.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public boolean isLossy() {
    return false;
  }
  

}
