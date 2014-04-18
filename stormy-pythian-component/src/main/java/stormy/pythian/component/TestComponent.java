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
package stormy.pythian.component;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static stormy.pythian.model.annotation.ComponentType.NO_TYPE;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.ListMapper;
import stormy.pythian.model.annotation.NameMapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.annotation.Topology;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.ListedFeaturesMapper;
import stormy.pythian.model.instance.NamedFeaturesMapper;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

@SuppressWarnings("serial")
@Documentation(name = "Test", description = "Use me as a test for component edition", type = NO_TYPE)
public class TestComponent implements Component {

    @InputStream(name = "Input 1")
    private Stream input1;

    @NameMapper(stream = "Input 1", expectedFeatures = {
            @ExpectedFeature(name = "Double", type = Double.class),
            @ExpectedFeature(name = "Integer", type = Integer.class),
            @ExpectedFeature(name = "String", type = String.class),
    })
    private NamedFeaturesMapper intput1Mapper;

    @InputStream(name = "Input 2")
    private Stream input2;

    @ListMapper(stream = "Input 2")
    private ListedFeaturesMapper intput2Mapper;

    @OutputStream(name = "Output 1", from = "Input 1")
    private Stream output1;

    @NameMapper(stream = "Output 1", expectedFeatures = { @ExpectedFeature(name = "Date", type = Date.class) })
    private NamedFeaturesMapper output1Mapper;

    @OutputStream(name = "Output 2", from = "Input 2")
    private Stream output2;

    @NameMapper(stream = "Output 2", expectedFeatures = { @ExpectedFeature(name = "Feature count", type = Integer.class) })
    private NamedFeaturesMapper output2Mapper;

    @OutputStream(name = "Output 3")
    private Stream output3;

    @ListMapper(stream = "Output 3")
    private ListedFeaturesMapper output3Mapper;

    @Property(name = "String property", description = "I'm a mandatory string", mandatory = true)
    private String stringProp;

    @Property(name = "Integer property", description = "I'm a integer", mandatory = false)
    private Integer integerProp;

    @Property(name = "Double property", description = "I'm a double", mandatory = false)
    private Double doubleProp;

    @Property(name = "Number property", description = "I'm a number", mandatory = false)
    private Number numberProp;

    @Property(name = "Boolean property", description = "I'm a mandatory boolean", mandatory = true)
    private Boolean booleanProp;

    @Property(name = "Enum property 1", description = "I'm a enum", mandatory = false)
    private TestEnum enumProp1;

    @Property(name = "Enum property 2", description = "I'm a mandatory enum", mandatory = true)
    private TestEnum enumProp2;

    @State(name = "Test's state")
    private StateFactory stateFactory;

    @Topology
    private TridentTopology topology;

    @Override
    public void init() {
        output1 = input1.each(new BaseFunction() {
            @Override
            public void execute(TridentTuple tuple, TridentCollector collector) {
                Instance instance = Instance.get(tuple, (NamedFeaturesMapper) null, output1Mapper);
                instance.setFeature("Date", new Date());
                collector.emit(new Values(instance));
            }
        }, new Fields(NEW_INSTANCE_FIELD));

        output2 = input2.each(new BaseFunction() {
            @Override
            public void execute(TridentTuple tuple, TridentCollector collector) {
                Instance instance = Instance.get(tuple, (NamedFeaturesMapper) null, output2Mapper);
                instance.setFeature("Feature count", output2Mapper.size());
                collector.emit(new Values(instance));
            }
        }, new Fields(NEW_INSTANCE_FIELD));

        output3 = topology.newStream(randomAlphabetic(6), new BaseRichSpout() {

            private SpoutOutputCollector _collector;

            @Override
            public void declareOutputFields(OutputFieldsDeclarer declarer) {
                declarer.declare(new Fields(NEW_INSTANCE_FIELD));
            }

            @Override
            public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector) {
                _collector = collector;
            }

            @Override
            public void nextTuple() {
                Utils.sleep(200);
                List<Integer> features = new ArrayList<>();
                for (int i = 0; i < output2Mapper.size(); i++) {
                    features.add(i);
                }

                Instance newInstance = Instance.create(output3Mapper);
                newInstance.addFeatures(features);

                _collector.emit(new Values(newInstance));
            }
        });

    }

    public static enum TestEnum {
        VALUE1, VALUE2, VALUE3, VALUE4;
    }
}
