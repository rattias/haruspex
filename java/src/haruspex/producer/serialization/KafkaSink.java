package haruspex.producer.serialization;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import haruspex.common.ID;
import haruspex.producer.RecordSink;
import haruspex.producer.StringRecordSink;

public class KafkaSink implements StringRecordSink {
  private final static String DEFAULT_TOPIC = "trace";
  
  private final Producer<String, String> kProducer;
  private final String topic;
  private final int numShards;
  
  public KafkaSink(int numShards) {
    this(DEFAULT_TOPIC, numShards);
  }
  
  public KafkaSink(String topic, int numShards) {
    this.topic = topic;
    this.numShards = numShards;
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    kProducer = new KafkaProducer<>(props);
  }
  
  @Override
  public void put(ID traceID, String record) {
    int shard = (int)(traceID.toLong() % numShards);
    shard = 0;
    Future<RecordMetadata> res = kProducer.send(
        new ProducerRecord<String, String>(topic, shard, traceID.toString(), record)
    );
    try {
      res.get();
    } catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void flush() {}
  
  public void close() {
    kProducer.close();
  }

  public boolean isLossy() {
    return false;
  }
}
