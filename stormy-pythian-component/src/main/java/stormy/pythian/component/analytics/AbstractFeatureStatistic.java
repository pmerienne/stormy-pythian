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
package stormy.pythian.component.analytics;

import static stormy.pythian.component.analytics.Constants.COMPUTED_FEATURE;
import static stormy.pythian.component.analytics.Constants.GROUP_BY_FEATURE;
import static stormy.pythian.component.analytics.Constants.GROUP_BY_FIELD;
import static stormy.pythian.component.analytics.Constants.STATISTIC_FIELD;
import static stormy.pythian.model.annotation.MappingType.FIXED_FEATURES;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.operation.builtin.MapGet;
import storm.trident.state.StateFactory;
import stormy.pythian.component.analytics.StatisticAggregator.AddStatisticFeatures;
import stormy.pythian.component.analytics.StatisticAggregator.AggregableStatistic;
import stormy.pythian.component.common.ExtractFeatures;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.State;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.InputFixedFeaturesMapper;
import backtype.storm.tuple.Fields;

public abstract class AbstractFeatureStatistic<T> implements Component {

	@InputStream(name = "in", type = FIXED_FEATURES, expectedFeatures = { @ExpectedFeature(name = GROUP_BY_FEATURE, type = Object.class),
			@ExpectedFeature(name = COMPUTED_FEATURE, type = Number.class) })
	private Stream in;

	@Mapper(stream = "in")
	private InputFixedFeaturesMapper inputMapper;

	@State(name = "Statistics' state")
	private StateFactory stateFactory;

	private static final long serialVersionUID = -5312700259983804231L;

	public Stream initOutputStream(AggregableStatistic<T> aggregableStatistic) {
		TridentState statistics = in //
				.each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, GROUP_BY_FEATURE), new Fields(GROUP_BY_FIELD)) //
				.groupBy(new Fields(GROUP_BY_FIELD)) //
				.persistentAggregate(stateFactory, new Fields(INSTANCE_FIELD), new StatisticAggregator<T>(inputMapper, aggregableStatistic), new Fields(STATISTIC_FIELD));

		return in //
				.each(new Fields(INSTANCE_FIELD), new ExtractFeatures(inputMapper, GROUP_BY_FEATURE), new Fields(GROUP_BY_FIELD)) //
				.stateQuery(statistics, new Fields(GROUP_BY_FIELD), new MapGet(), new Fields(STATISTIC_FIELD)) //
				.each(new Fields(INSTANCE_FIELD, STATISTIC_FIELD), new AddStatisticFeatures<T>(aggregableStatistic), new Fields(NEW_INSTANCE_FIELD));
	}

}
