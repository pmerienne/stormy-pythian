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

import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.component.common.AddFeature;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.github.pmerienne.trident.ml.classification.Classifier;
import com.github.pmerienne.trident.ml.classification.ClassifierUpdater;
import com.github.pmerienne.trident.ml.classification.ClassifyQuery;

public abstract class TridentMLClassifier<L> implements Component {

	private static final long serialVersionUID = 1L;

	public static final String PREDICTION_FEATURE = "Prediction";

	private static final String TRIDENT_ML_INSTANCE_FIELD = "TRIDENT_ML_INSTANCE_FIELD";
	private static final String TRIDENT_ML_PREDICTION_FIELD = "TRIDENT_ML_PREDICTION_FIELD";

	@InputStream(name = "update", type = USER_SELECTION)
	private Stream update;

	@Mapper(stream = "update")
	private InputUserSelectionFeaturesMapper updateInputMapper;

	@InputStream(name = "query", type = USER_SELECTION)
	private Stream query;

	@Mapper(stream = "query")
	private InputUserSelectionFeaturesMapper queryInputMapper;

	@OutputStream(name = "prediction", from = "query", newFeatures = { @ExpectedFeature(name = PREDICTION_FEATURE) })
	private Stream prediction;

	@Mapper(stream = "prediction")
	private OutputFeaturesMapper predictionOutputMapper;

	@State(name = "Classifier's state")
	private StateFactory stateFactory;

	@Property(name = "Classifier name", mandatory = true)
	private String classifierName;

	public void initClassifierStreams(Classifier<L> classifier) {
		TridentState classifierState = update //
				.each(new Fields(INSTANCE_FIELD), new TridentMLInstanceCreator<L>(updateInputMapper), new Fields(TRIDENT_ML_INSTANCE_FIELD)) //
				.partitionPersist(stateFactory, new Fields(TRIDENT_ML_INSTANCE_FIELD), new ClassifierUpdater<L>(classifierName, classifier));

		prediction = query //
				.each(new Fields(INSTANCE_FIELD), new TridentMLInstanceCreator<L>(queryInputMapper), new Fields(TRIDENT_ML_INSTANCE_FIELD)) //
				.stateQuery(classifierState, new Fields(TRIDENT_ML_INSTANCE_FIELD), new ClassifyQuery<L>(classifierName), new Fields(TRIDENT_ML_PREDICTION_FIELD)) //
				.each(new Fields(INSTANCE_FIELD, TRIDENT_ML_PREDICTION_FIELD), new AddFeature(predictionOutputMapper, TRIDENT_ML_PREDICTION_FIELD, PREDICTION_FEATURE), new Fields(NEW_INSTANCE_FIELD));
	}

	private static class TridentMLInstanceCreator<L> extends BaseFunction {

		private static final long serialVersionUID = 1L;

		private final InputUserSelectionFeaturesMapper inputMapper;

		public TridentMLInstanceCreator(InputUserSelectionFeaturesMapper inputMapper) {
			this.inputMapper = inputMapper;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance pythianInstance = Instance.from(tuple);

			L label = (L) pythianInstance.getLabel();

			Object[] selectedFeatures = pythianInstance.getSelectedFeatures(inputMapper);
			double[] features = new double[selectedFeatures.length];
			for (int i = 0; i < selectedFeatures.length; i++) {
				if (selectedFeatures[i] instanceof Number) {
					features[i] = ((Number) selectedFeatures[i]).doubleValue();
				} else {
					features[i] = 0.0;
				}
			}

			com.github.pmerienne.trident.ml.core.Instance<L> tridentMLInstance = new com.github.pmerienne.trident.ml.core.Instance<L>(label, features);
			collector.emit(new Values(tridentMLInstance));
		}
	}
}
