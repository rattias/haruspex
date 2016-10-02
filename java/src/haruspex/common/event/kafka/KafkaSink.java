/*
 * Copyright 2016 Roberto Attias
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package haruspex.common.event.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haruspex.common.ID;
import haruspex.common.TagList;
import haruspex.common.event.CodecException;
import haruspex.common.event.IEventCodec;
import haruspex.common.event.IEventSink;
import haruspex.common.event.JSONEventCodec;

public class KafkaSink implements IEventSink {
  private final static Logger logger = LoggerFactory.getLogger(KafkaSink.class);
  private final static String DEFAULT_TOPIC = "trace";
  
  private final Producer<String, String> kProducer;
  private final String topic;
  private final int partitionCount;
  private final IEventCodec codec;
  
  public KafkaSink(int partitionCount) {
    this(DEFAULT_TOPIC, partitionCount);
  }
  
  public KafkaSink(String topic, int partitionCount) {
    this.topic = topic;
    this.partitionCount = partitionCount;
    codec = new JSONEventCodec();
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
  public void put(
      long seqNum,
      long timeStamp,
      ID[] context,
      TagList...tags) {
    ID traceID = context[0];
    try {
      String record = codec.encode(seqNum, timeStamp, context, tags);
      int partition = (int)(traceID.toLong() % partitionCount);
      Future<RecordMetadata> res = kProducer.send(
        new ProducerRecord<String, String>(topic, partition, traceID.toString(), record)
      );
      res.get();
    } catch (InterruptedException | ExecutionException | CodecException ex) {
      logger.error("Exception while emitting event", ex);
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
