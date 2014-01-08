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
package stormy.pythian.service.topology;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import stormy.pythian.core.configuration.PythianToplogyConfiguration;

@Service
public class TopologyService {

	private Map<String, PythianToplogyConfiguration> dao = new HashMap<>();

	public void save(PythianToplogyConfiguration configuration) {
		checkNotNull(configuration, "topology's is mandatory");
		configuration.ensureId();

		dao.put(configuration.getId(), configuration);
	}

	public void delete(String configurationId) {
		checkNotNull(configurationId, "topology's id is mandatory");

		dao.remove(configurationId);
	}

	public Collection<PythianToplogyConfiguration> findAll() {
		return dao.values();
	}

	public PythianToplogyConfiguration findById(String configurationId) {
		return dao.get(configurationId);
	}
}
