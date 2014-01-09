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
package stormy.pythian.component.preprocessor;

import static java.io.File.createTempFile;
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.file.FileSteamSource.LINE_FEATURE;
import static stormy.pythian.model.instance.Instance.Builder.instance;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import storm.trident.Stream;
import storm.trident.testing.FixedBatchSpout;
import stormy.pythian.component.file.FileSteamSource;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.TextFeature;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;

public class NormalizerTest extends TridentIntegrationTest {
//
//	private static final int TOPOLOGY_START_TIME = 5000;
//
//	public void should_normalize_features() {
//		// Given
//				Stream in = topology.newStream("test", new FixedBatchSpout(new Fields(Instance.INSTANCE_FIELD), 5, outputs))
//
//				Map<String, String> mappings = new HashMap<>();
//				mappings.put(LINE_FEATURE, LINE_FEATURE);
//				FixedFeaturesMapper mapper = new FixedFeaturesMapper(mappings);
//
//				FileSteamSource component = new FileSteamSource();
//				setField(component, "filename", tmpFile.getAbsolutePath());
//				setField(component, "mapper", mapper);
//				setField(component, "topology", topology);
//
//				component.init();
//				Stream out = (Stream) getField(component, "out");
//
//				InstanceCollector instanceCollector = new InstanceCollector();
//				instanceCollector.listen(out);
//
//				// When
//				this.launch();
//				Utils.sleep(TOPOLOGY_START_TIME);
//
//				// Then
//				assertThat(instanceCollector.getCollected()).containsOnly( //
//						instance().with(LINE_FEATURE, lines.get(0)).build(), //
//						instance().with(LINE_FEATURE, lines.get(1)).build(), //
//						instance().with(LINE_FEATURE, lines.get(2)).build() //
//						);
//	}
//
//	private Values createValues(String sentence) {
//		Instance instance = new Instance();
//		mapper.setFeature(instance, WORD_FEATURE, new TextFeature(sentence));
//		return new Values(instance);
//	}
}
