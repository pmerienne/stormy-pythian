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

import static java.util.Collections.EMPTY_LIST;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;

@RunWith(MockitoJUnitRunner.class)
public class TopologyLocalLauncherServiceTest {

	@InjectMocks
	private TopologyLocalLauncherService service;

	@Mock
	private TopologyRepository topologyRepository;

	@Mock
	private LocalCluster cluster;

	@SuppressWarnings("unchecked")
	@Test
	public void should_launch_topology() {
		// Given
		String topologyId = random(6);
		PythianToplogyConfiguration configuration = mock(PythianToplogyConfiguration.class);
		when(configuration.getComponents()).thenReturn(EMPTY_LIST);
		when(configuration.getConnections()).thenReturn(EMPTY_LIST);

		// When
		when(topologyRepository.findById(topologyId)).thenReturn(configuration);

		// When
		service.launch(topologyId);

		// Then
		verify(cluster).submitTopology(isA(String.class), isA(Config.class), isA(StormTopology.class));
	}
}
