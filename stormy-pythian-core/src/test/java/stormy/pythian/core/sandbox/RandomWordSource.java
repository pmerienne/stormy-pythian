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
package stormy.pythian.core.sandbox;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static stormy.pythian.model.annotation.ComponentType.STREAM_SOURCE;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.testing.FixedBatchSpout;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.FeaturesMapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Topology;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.FeatureType;
import stormy.pythian.model.instance.FixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.TextFeature;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@Documentation(name = "Random Word Source", description = "Emit random string", type = STREAM_SOURCE)
public class RandomWordSource implements Component {

	private static final long serialVersionUID = 3239910438778205769L;

	public static final String WORD_FEATURE = "word";

	@OutputStream(name = "out", newFeatures = { @ExpectedFeature(name = WORD_FEATURE, type = FeatureType.TEXT) })
	private Stream out;

	@FeaturesMapper(stream = "out")
	private FixedFeaturesMapper mapper;

	@Topology
	private TridentTopology topology;

	@Override
	@SuppressWarnings("unchecked")
	public void init() {
		FixedBatchSpout spout = new FixedBatchSpout(new Fields(INSTANCE_FIELD), 3, //
				createValues("jackson"), //
				createValues("nathan"), //
				createValues("pierre"), //
				createValues("julie"));
		spout.setCycle(true);

		out = topology.newStream(randomAlphabetic(6), spout);
	}

	private Values createValues(String sentence) {
		Instance instance = new Instance();
		mapper.setFeature(instance, WORD_FEATURE, new TextFeature(sentence));
		return new Values(instance);
	}

}