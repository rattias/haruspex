package haruspex.producer.serialization;

import java.io.PrintWriter;

import haruspex.common.ID;
import haruspex.producer.StringRecordSink;

public class PrintWriterSink implements StringRecordSink {
  private final PrintWriter pw;
  
  public PrintWriterSink(PrintWriter pw) {
    this.pw = pw;
  }

  @Override
  public void put(ID traceID, String record) {
    pw.println(record);
  }
  
  @Override
  public void flush() {
    pw.flush();
  }
  
  public void close() {
    pw.close();
  }
  
  public boolean isLossy() {
    return false;
  }
  

}
