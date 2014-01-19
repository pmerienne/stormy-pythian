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

import static stormy.pythian.component.statistic.aggregation.Constants.MEAN_FEATURE;
import stormy.pythian.component.statistic.aggregation.AggregableMean.MeanState;
import stormy.pythian.component.statistic.aggregation.StatisticAggregator.AggregableStatistic;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFeaturesMapper;

public class AggregableMean implements AggregableStatistic<MeanState> {

	private static final long serialVersionUID = 845845674309635484L;

	private final OutputFeaturesMapper mapper;

	public AggregableMean(OutputFeaturesMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public MeanState init(Number feature) {
		return new MeanState(feature.doubleValue(), 1L);
	}

	@Override
	public MeanState combine(MeanState val1, MeanState val2) {
		return new MeanState(val1.sum + val2.sum, val1.count + val2.count);
	}

	@Override
	public MeanState zero() {
		return new MeanState(0, 0);
	}

	@Override
	public Instance update(Instance original, MeanState statistic) {
		return original.withFeature(mapper, MEAN_FEATURE, statistic == null ? null : statistic.getMean());
	}

	public static class MeanState {
		private final double sum;
		private final long count;

		public MeanState(double sum, long count) {
			this.sum = sum;
			this.count = count;
		}

		public double getMean() {
			return sum / count;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (count ^ (count >>> 32));
			long temp;
			temp = Double.doubleToLongBits(sum);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MeanState other = (MeanState) obj;
			if (count != other.count)
				return false;
			if (Double.doubleToLongBits(sum) != Double.doubleToLongBits(other.sum))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "MeanState [sum=" + sum + ", count=" + count + "]";
		}

	}

}
