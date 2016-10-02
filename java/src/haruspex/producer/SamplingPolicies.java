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
package haruspex.producer;

/**
 * This is a container for some common sampling policies
 * @author roberto
 *
 */
public class SamplingPolicies {
  
  /**
   * each trace created with this policy results into generation of records.
   */
  public static final SamplingPolicy ALWAYS = new SamplingPolicy() {
    public boolean shouldTrace() {
      return true;
    }
  };

  /*
   * traces created with this policy result in producing records with the specified probability in [0.0, 1.0]
   */
  public static final SamplingPolicy  WITH_PROBABILITY(double probability) {
    if (probability < 0.0 || probability > 1.0) {
      throw new IllegalArgumentException("Probability should be in [0.0-1.0] interval");
    }
    return new SamplingPolicy() {
      public boolean shouldTrace() {
        return Math.random() < probability;
      }
    };
  };
}
