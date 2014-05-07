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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;

@Service
public class TopologyService {

    @Autowired
    private TopologyRepository repository;

    public PythianToplogyConfiguration save(PythianToplogyConfiguration topology) {
        checkNotNull(topology, "topology is mandatory");
        topology.ensureId();

        repository.save(topology);
        return topology;
    }

    public void delete(String topologyId) {
        checkNotNull(topologyId, "topology's id is mandatory");
        repository.delete(topologyId);
    }

    public Collection<PythianToplogyConfiguration> findAll() {
        return repository.findAll();
    }

    public PythianToplogyConfiguration findById(String topologyId) {
        checkNotNull(topologyId, "topology's id is mandatory");
        return repository.findById(topologyId);
    }
    
}
