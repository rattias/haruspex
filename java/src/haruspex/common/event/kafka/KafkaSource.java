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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import haruspex.common.GlobalID;
import haruspex.common.event.IEventSource;
import haruspex.common.event.IRawTraceEvent;
import haruspex.common.event.ITraceEvent;
import haruspex.common.event.JSONEventCodec;
import haruspex.common.event.RawTraceEvent;
import haruspex.common.event.TraceEvent;

public class KafkaSource implements IEventSource {
  private KafkaConsumer<String, String> consumer;
  private final JSONEventCodec codec;
  
  public KafkaSource() {
    this.codec = new JSONEventCodec();
  }

  @Override
  public List<ITraceEvent> getAvailable() throws IOException, InterruptedException {
    ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
    ArrayList<ITraceEvent> events = new ArrayList<>();
    for (ConsumerRecord<String, String> record: records) {
      events.add((TraceEvent)codec.decode(record.value()));
    }
    return events;
  }

  @Override
  public List<IRawTraceEvent> getAvailableRaw() throws IOException, InterruptedException {
    ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
    ArrayList<IRawTraceEvent> events = new ArrayList<>();
    for (ConsumerRecord<String, String> record: records) {
      events.add(new RawTraceEvent(GlobalID.of(record.key()), record.value()));
    }
    return events;
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
  public String getCodecClassName() {
    return codec.getClass().getName();
  }
}