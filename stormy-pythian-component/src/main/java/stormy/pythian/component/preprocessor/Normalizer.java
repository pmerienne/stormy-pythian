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

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.from;

import java.util.Collection;
import java.util.Map;

import storm.trident.Stream;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.DoubleFeature;
import stormy.pythian.model.instance.Feature;
import stormy.pythian.model.instance.FeatureProcedure;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class Normalizer implements Component {

	private static final long serialVersionUID = 3259186264532961799L;

	@InputStream(name = "in", type = USER_SELECTION)
	private Stream in;

	@OutputStream(name = "out")
	private Stream out;

	@Mapper(stream = "in")
	private InputUserSelectionFeaturesMapper mapper;

	@Override
	public void init() {
		out = in.each(new Fields(INSTANCE_FIELD), new NormalizerFunction(mapper), new Fields(Instance.NEW_INSTANCE_FIELD));
	}

	private static class NormalizerFunction extends BaseFunction {

		private static final long serialVersionUID = -5433556581807138157L;

		private final InputUserSelectionFeaturesMapper mapper;

		public NormalizerFunction(InputUserSelectionFeaturesMapper mapper) {
			this.mapper = mapper;
		}

		@SuppressWarnings("serial")
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Instance original = Instance.from(tuple);
			
			final double magnitude = 0.0;
			mapper.forEachFeatures(original, new FeatureProcedure() {
				@Override
				public void process(Feature<?> feature) {
//					magnitude += pow(((DoubleFeature) feature).getValue(), 2);
				}
			});
//			
//
//			mapper.
//			
//			Map<String, Feature<?>> features = mapper.getFeatures(instance);
//			double magnitude = magnitude(features.values());
//
//			for (String featureName : features.keySet()) {
//				Feature<?> feature = features.get(featureName);
//				if (feature instanceof DoubleFeature) {
//					DoubleFeature doubleFeature = (DoubleFeature) feature;
//					instance.set(featureName, doubleFeature.getValue() / magnitude);
//				}
//			}

			collector.emit(new Values(null));
		}

		private double magnitude(Collection<Feature<?>> features) {
			double magnitude = 0.0;

			for (Feature<?> feature : features) {
				if (feature instanceof DoubleFeature) {
					magnitude += pow(((DoubleFeature) feature).getValue(), 2);
				}
			}

			return sqrt(magnitude);
		}

	}
}
