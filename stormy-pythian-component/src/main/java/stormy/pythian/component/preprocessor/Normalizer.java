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

import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import storm.trident.Stream;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.FeatureFunction;
import stormy.pythian.model.instance.FeatureProcedure;
import stormy.pythian.model.instance.InputUserSelectionFeaturesMapper;
import stormy.pythian.model.instance.Instance;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.google.common.util.concurrent.AtomicDouble;

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

			final AtomicDouble atomicMagnitude = new AtomicDouble(0.0);
			original.process(mapper, new FeatureProcedure<Double>() {
				@Override
				public void process(Double feature) {
					atomicMagnitude.getAndAdd(feature * feature);
				}
			});
			final double magnitude = Math.sqrt(atomicMagnitude.get());

			Instance newInstance = original.transform(mapper, new FeatureFunction<Double>() {
				@Override
				public Double transform(Double feature) {
					return feature / magnitude;
				}
			});

			collector.emit(new Values(newInstance));
		}

	}
}
