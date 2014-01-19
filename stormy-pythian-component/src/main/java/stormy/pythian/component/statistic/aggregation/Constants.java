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

public interface Constants {

	public static final String DATE_FEATURE = "Date";
	public static final String GROUP_BY_FEATURE = "Group by";
	public static final String COMPUTED_FEATURE = "Computed feature";

	public static final String MEAN_FEATURE = "Feature mean";

	public static final String MEDIAN_FEATURE = "Feature median";
	public static final String QUANTILE_75th_FEATURE = "Feature 75th percentiles";
	public static final String QUANTILE_90th_FEATURE = "Feature 90th percentiles";
	public static final String QUANTILE_95th_FEATURE = "Feature 95th percentiles";
	public static final String QUANTILE_99th_FEATURE = "Feature 99th percentiles";
	public static final String QUANTILE_99th9_FEATURE = "Feature 99.9th percentiles";

	public static final String COUNT_FEATURE = "Non-null feature count";

	public static final String GROUP_BY_FIELD = "GROUP_BY_FIELD";
	public static final String SLOT_FIELD = "SLOT_FIELD";
	public static final String COMPUTED_FEATURE_FIELD = "COMPUTED_FEATURE_FIELD";
	public static final String STATISTIC_FIELD = "STATISTIC_FIELD";
	public static final String SLOT_STATISTIC_FIELD = "SLOT_STATISTIC_FIELD";
	public static final String DATE_FIELD = "DATE_FIELD";
}