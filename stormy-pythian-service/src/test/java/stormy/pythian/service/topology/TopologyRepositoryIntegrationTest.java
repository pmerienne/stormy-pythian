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

import static org.fest.assertions.Assertions.assertThat;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stormy.pythian.core.configuration.ConnectionConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.service.spring.ServiceConfiguration;

@ContextConfiguration(classes = ServiceConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TopologyRepositoryIntegrationTest {

    @Autowired
    private TopologyRepository repository;

    @Autowired
    private RedisTemplate<String, PythianToplogyConfiguration> redisTemplate;

    @After
    @Before
    public void cleanRedisDB() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    public void should_save_and_get_topologies() {
        // Given
        PythianToplogyConfiguration expectedTopology = new PythianToplogyConfiguration("test");
        expectedTopology.getConnections().add(
                new ConnectionConfiguration("from", "fromStreamName", "to", "toStreamName"));

        // When
        repository.save(expectedTopology);
        PythianToplogyConfiguration actualTopology = repository.findById("test");

        // Then
        assertThat(actualTopology).isEqualTo(expectedTopology);
    }

    @Test
    public void should_save_and_find_all_topologies() {
        // Given
        PythianToplogyConfiguration topology1 = new PythianToplogyConfiguration("test1");
        PythianToplogyConfiguration topology2 = new PythianToplogyConfiguration("test2");

        // When
        repository.save(topology1);
        repository.save(topology2);
        
        Collection<PythianToplogyConfiguration> actualTopologies = repository.findAll();

        // Then
        assertThat(actualTopologies).containsOnly(topology1, topology2);
    }
}
