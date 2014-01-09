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
package stormy.pythian.component;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import stormy.pythian.model.annotation.Topology;
import stormy.pythian.model.component.Component;

public abstract class InstanceProcessor implements Component {

	private static final long serialVersionUID = -538661487188973802L;

	@Topology
	private TridentTopology topology;

	@Override
	public void init() {
	}

	protected abstract Stream setInputStream();

	protected abstract void setOutputStream(Stream out);
}
