package haruspex.batchdiviner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaTraceSource implements TraceEventSource {
  private KafkaConsumer<String, String> consumer;
  
  public KafkaTraceSource() {
  }

  @Override
  public TraceEvents[] poll(long timeoutMs) {
//    try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
//      consumer.subscribe(Arrays.asList("foo", "bar"));
//      while (true) {
//        ConsumerRecords<String, String> records = consumer.poll(timeoutMs);
//        List<TraceEvents> events = new ArrayList<>();
//        for (ConsumerRecord<String, String> record : records) {
//          //events.add(new TraceEvents())
//          System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
//          // process here
//          consumer.commitSync();
//        }
//      }
//    }
    return null;
  }

  @Override
  public void configure(Properties props) {
    String[][] kValues = {
        {"bootstrap.servers", "localhost:9092"},
        {"group.id", "test"},
        {"enable.auto.commit", "false"},
        {"session.timeout.ms", "30000"},
        {"key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"},
        {"key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"}
    };
    Properties kProps = new Properties();
    for (String[] kv : kValues) {
      kProps.put(kv[0], props.getProperty("tracesource." + kv[0], kv[1]));
    }
    consumer = new KafkaConsumer<>(props);
    String[] categories = props.getProperty("tracesource.categories", "trace").split(" ");
    consumer.subscribe(Arrays.asList(categories));
  }


  @Override
  public boolean supportCheckpointing() {
    return true;
  }

  @Override
  public void checkpoint() {
    consumer.commitSync();    
  }

  @Override
  public TraceEvents<String>[] pollRaw(long timeoutMs) {
    // TODO Auto-generated method stub
    return null;
  }
}