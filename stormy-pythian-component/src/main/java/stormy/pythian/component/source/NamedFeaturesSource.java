/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stormy.pythian.component.source;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
import java.util.List;
import java.util.Map;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.Topology;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.Instance;
import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@SuppressWarnings("serial")
public abstract class NamedFeaturesSource implements Component {

    @OutputStream(name = "output")
    private transient Stream out;

    @Property(name = "Max batch size")
    protected Integer maxBatchSize = 1000;

    @Topology
    private transient TridentTopology topology;

    @Override
    public void init() {
        PythianBatchSpout spout = new PythianBatchSpout(this);
        out = topology.newStream(this.getClass() + "-spout-" + randomAlphabetic(5), spout);
    }

    protected abstract List<Instance> nextBatch();

    protected abstract void close();

    protected abstract void open();

    private static class PythianBatchSpout implements IBatchSpout {

        private final NamedFeaturesSource streamSource;

        public PythianBatchSpout(NamedFeaturesSource streamSource) {
            this.streamSource = streamSource;
        }

        @Override
        public void emitBatch(long batchId, TridentCollector collector) {
            List<Instance> instances = streamSource.nextBatch();

            for (Instance instance : instances) {
                collector.emit(new Values(instance));
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Map getComponentConfiguration() {
            Config conf = new Config();
            conf.setMaxTaskParallelism(1);
            return conf;
        }

        @Override
        public void close() {
            streamSource.close();
        }

        @Override
        public void ack(long batchId) {
        }

        @Override
        public Fields getOutputFields() {
            return new Fields(NEW_INSTANCE_FIELD);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public void open(Map conf, TopologyContext context) {
            streamSource.open();
        }
    }

}
