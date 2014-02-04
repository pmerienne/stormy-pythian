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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;

@Repository
public class TopologyRepository {

    private final static String TOPOLOGY_RAW_KEY = "pythian-topology";

    @Autowired
    private RedisTemplate<String, PythianToplogyConfiguration> redisTemplate;

    public void save(PythianToplogyConfiguration configuration) {
        redisTemplate.opsForHash().put(TOPOLOGY_RAW_KEY, configuration.getId(), configuration);
    }

    public void delete(String configurationId) {
        redisTemplate.opsForHash().delete(TOPOLOGY_RAW_KEY, configurationId);
    }

    public Collection<PythianToplogyConfiguration> findAll() {

        List<PythianToplogyConfiguration> topologies = new ArrayList<PythianToplogyConfiguration>();
        for (Object topology : redisTemplate.opsForHash().values(TOPOLOGY_RAW_KEY)) {
            topologies.add((PythianToplogyConfiguration) topology);
        }

        return topologies;
    }

    public PythianToplogyConfiguration findById(String configurationId) {
        return (PythianToplogyConfiguration) redisTemplate.opsForHash().get(TOPOLOGY_RAW_KEY, configurationId);
    }
}
