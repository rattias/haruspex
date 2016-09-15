package haruspex.producer;

import java.io.PrintWriter;

import haruspex.producer.RecordSink;
import haruspex.producer.Trace;
import haruspex.producer.TraceSerializer;
import haruspex.producer.serialization.JsonSerializer;
import haruspex.producer.serialization.PrintWriterSink;

public class StdoutProducerTest {
  public static void main(String[] args) {
    StringRecordSink stdout = new PrintWriterSink(new PrintWriter(System.out));
    TraceSerializer serializer = new JsonSerializer(stdout);
    Trace.Builder builder = new Trace.Builder().setSerializer(serializer);
    ProducerTest.test1(builder);
    ProducerTest.test2(builder);
    ProducerTest.test3(builder);
    ProducerTest.test4(builder);
    serializer.close();
  }
}
