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
package stormy.pythian.component.statistic.aggregation;

import static stormy.pythian.component.statistic.aggregation.Constants.COUNT_FEATURE;
import stormy.pythian.component.statistic.aggregation.StatisticAggregator.AggregableStatistic;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFeaturesMapper;

public class AggregableCount implements AggregableStatistic<Long> {

	private static final long serialVersionUID = 845845674309635484L;

	private final OutputFeaturesMapper mapper;

	public AggregableCount(OutputFeaturesMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public Long init(Number feature) {
		return 1L;
	}

	@Override
	public Long combine(Long val1, Long val2) {
		return val1 + val2;
	}

	@Override
	public Long zero() {
		return 0L;
	}

	@Override
	public Instance update(Instance original, Long count) {
		return original.withFeature(mapper, COUNT_FEATURE, count == null ? 0.0 : count);
	}

}
