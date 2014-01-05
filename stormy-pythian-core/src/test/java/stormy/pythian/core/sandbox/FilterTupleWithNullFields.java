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

import storm.trident.Stream;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.component.Component;
import backtype.storm.tuple.Fields;

@Documentation(name = "Filter", description = "Filters tuples with null fields")
public class FilterTupleWithNullFields implements Component {

	private static final long serialVersionUID = 663627064051598816L;

	@InputStream(expectedFeatures = {}, name = "in")
	protected Stream in;

	@OutputStream(name = "out", newFeatures = {}, from = "in")
	protected Stream out;

	@Property(name = "Fields", description = "A comma separated list of fields to check")
	protected String[] fields;

	@Override
	public void init() {
		out = in.each(new Fields(fields), new BaseFilter() {

			private static final long serialVersionUID = -8964006393560156995L;

			@Override
			public boolean isKeep(TridentTuple tuple) {
				boolean isKeep = true;
				int i = 0;

				while (i < fields.length && isKeep) {
					isKeep = tuple.getFloatByField(fields[i]) != null;
				}

				return isKeep;
			}
		});
	}

}
