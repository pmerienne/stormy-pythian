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
package stormy.pythian.component.learner.tridentml;

import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.model.instance.InputUserSelectionFeaturesMapperTestBuilder.inputUserSelectionFeaturesMapper;
import static stormy.pythian.model.instance.InstanceTestBuilder.instance;
import static stormy.pythian.model.instance.OutputFeaturesMapperTestBuilder.outputFixedFeaturesMapper;

import org.junit.Test;

import storm.trident.Stream;
import storm.trident.testing.MemoryMapState;
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.OutputFixedFeaturesMapper;
import stormy.pythian.testing.FixedInstanceSpout;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;

public class TridentMLPerceptronClassifierTest extends TridentIntegrationTest {

	@Test
	public void should_classify_nand() {
		// Given
		InputUserSelectionFeaturesMapper updateInputMapper = inputUserSelectionFeaturesMapper("bias", "val1", "val2").select("bias", "val1", "val2").build();
		InputUserSelectionFeaturesMapper queryInputMapper = inputUserSelectionFeaturesMapper("bias", "val1", "val2").select("bias", "val1", "val2").build();
		OutputFixedFeaturesMapper predictionOutputMapper = outputFixedFeaturesMapper("bias", "val1", "val2", "prediction").map(TridentMLClassifier.PREDICTION_FEATURE, "prediction").build();

		FixedInstanceSpout updateSpout = new FixedInstanceSpout(//
				instance().with(1.0).with(1.0).with(1.0).label(false).build(), //
				instance().with(1.0).with(-1.0).with(1.0).label(true).build(), //
				instance().with(1.0).with(1.0).with(-1.0).label(true).build(), //
				instance().with(1.0).with(-1.0).with(-1.0).label(true).build(), //
				instance().with(1.0).with(1.0).with(1.0).label(false).build(), //
				instance().with(1.0).with(-1.0).with(1.0).label(true).build(), //
				instance().with(1.0).with(1.0).with(-1.0).label(true).build(), //
				instance().with(1.0).with(-1.0).with(-1.0).label(true).build(), //
				instance().with(1.0).with(1.0).with(1.0).label(false).build(), //
				instance().with(1.0).with(-1.0).with(1.0).label(true).build(), //
				instance().with(1.0).with(1.0).with(-1.0).label(true).build(), //
				instance().with(1.0).with(-1.0).with(-1.0).label(true).build() //
		);

		FixedInstanceSpout querySpout = new FixedInstanceSpout(false, //
				instance().with(1.0).with(1.0).with(1.0).build(), //
				instance().with(1.0).with(-1.0).with(1.0).build(), //
				instance().with(1.0).with(1.0).with(-1.0).build(), //
				instance().with(1.0).with(-1.0).with(-1.0).build() //
		);

		Stream updateStream = topology.newStream("update", updateSpout);
		Stream queryStream = topology.newStream("query", querySpout);

		TridentMLPerceptronClassifier classifier = new TridentMLPerceptronClassifier();
		setField(classifier, "update", updateStream);
		setField(classifier, "query", queryStream);
		setField(classifier, "updateInputMapper", updateInputMapper);
		setField(classifier, "queryInputMapper", queryInputMapper);
		setField(classifier, "predictionOutputMapper", predictionOutputMapper);
		setField(classifier, "stateFactory", new MemoryMapState.Factory());
		setField(classifier, "classifierName", "test perceptron");
		classifier.init();

		Stream out = (Stream) getField(classifier, "prediction");
		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launchAndWait(updateSpout);

		// Then
		querySpout.start();
		this.wait(querySpout);

		assertThat(instanceCollector.getCollected()).containsOnly( //
				instance().with(1.0).with(1.0).with(1.0).with(false).build(), //
				instance().with(1.0).with(-1.0).with(1.0).with(true).build(), //
				instance().with(1.0).with(1.0).with(-1.0).with(true).build(), //
				instance().with(1.0).with(-1.0).with(-1.0).with(true).build() //
				);
	}

}
