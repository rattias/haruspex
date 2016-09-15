package haruspex.producer;

import java.io.FileOutputStream;
import java.io.IOException;

import haruspex.producer.Trace;
import haruspex.producer.TraceSerializer;
import haruspex.producer.serialization.OutputStreamRecordSink;
import haruspex.producer.serialization.flatbuf.FlatbufSerializer;

public class FlatbufProducerTest {
  public static void main(String[] args) throws IOException {
    FileOutputStream fos = new FileOutputStream(args[0]);
    BytesRecordSink sink = new OutputStreamRecordSink(fos);
    TraceSerializer serializer = new FlatbufSerializer(sink);
    Trace.Builder builder = new Trace.Builder().setSerializer(serializer);
    ProducerTest.test1(builder);
    ProducerTest.test2(builder);
    ProducerTest.test3(builder);
    ProducerTest.test4(builder);
    serializer.close();
  }
}
