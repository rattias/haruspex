package haruspex.producer;

import haruspex.producer.StringRecordSink;
import haruspex.producer.Trace;
import haruspex.producer.TraceSerializer;
import haruspex.producer.serialization.JsonSerializer;
import haruspex.producer.serialization.KafkaSink;

public class KafkaProducerTest {
  public static void main(String[] args) {
    StringRecordSink kafka = new KafkaSink("trace", 512);
    TraceSerializer serializer = new JsonSerializer(kafka);
    Trace.Builder builder = new Trace.Builder().setSerializer(serializer);
    ProducerTest.test1(builder);
    ProducerTest.test2(builder);
    ProducerTest.test3(builder);
    ProducerTest.test4(builder);
    serializer.close();
    System.out.println("DONE");
  }
}
