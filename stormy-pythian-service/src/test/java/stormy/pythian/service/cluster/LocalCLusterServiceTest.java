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
package stormy.pythian.service.cluster;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static stormy.pythian.service.cluster.TopologyState.Status.UNDEPLOYING;
import static stormy.pythian.service.cluster.TopologyState.Status.DEPLOYED;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.service.topology.TopologyRepository;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.generated.TopologySummary;

@RunWith(MockitoJUnitRunner.class)
public class LocalCLusterServiceTest {

	@InjectMocks
	private LocalCLusterService service;

	@Mock
	private TopologyRepository topologyRepository;

	@Mock(answer = RETURNS_DEEP_STUBS)
	private LocalCluster cluster;

	@SuppressWarnings("unchecked")
	@Test
	public void should_launch_topology() throws Exception {
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

	@Test
	public void should_retrieve_topology_states() {
		// Given
		PythianToplogyConfiguration topology1 = new PythianToplogyConfiguration("1", "topology 1");
		PythianToplogyConfiguration topology2 = new PythianToplogyConfiguration("2", "topology 2");
		PythianToplogyConfiguration topology3 = new PythianToplogyConfiguration("3", "topology 3");
		given(topologyRepository.findById("1")).willReturn(topology1);
		given(topologyRepository.findById("2")).willReturn(topology2);
		given(topologyRepository.findById("3")).willReturn(topology3);

		TopologySummary expectedTopologySummary1 = new TopologySummary(random(6), "1", 1, 1, 1, 1, "ACTIVE");
		TopologySummary expectedTopologySummary2 = new TopologySummary(random(6), "2", 2, 2, 2, 2, "KILLED");
		given(cluster.getClusterInfo().get_topologies()).willReturn(asList(expectedTopologySummary1, expectedTopologySummary2));

		// When
		List<TopologyState> states = service.getTopologyStates();

		// Then
		assertThat(states).containsOnly(
				new TopologyState("1", "topology 1", DEPLOYED),
				new TopologyState("2", "topology 2", UNDEPLOYING)
				);
	}

	@Test(expected = TopologyLaunchException.class)
	public void should_not_launch_not_existing_topology() throws TopologyLaunchException {
		// Given
		String topologyId = random(6);
		given(topologyRepository.findById(topologyId)).willReturn(null);

		// When
		service.launch(topologyId);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = TopologyLaunchException.class)
	public void should_throw_launch_exception_when_storm_submit_fails() throws TopologyLaunchException {
		// Given
		String topologyId = random(6);
		PythianToplogyConfiguration configuration = mock(PythianToplogyConfiguration.class);
		when(configuration.getComponents()).thenReturn(EMPTY_LIST);
		when(configuration.getConnections()).thenReturn(EMPTY_LIST);

		given(topologyRepository.findById(topologyId)).willReturn(configuration);
		doThrow(new RuntimeException()).when(cluster).submitTopology(Mockito.anyString(), Mockito.any(Config.class), Mockito.any(StormTopology.class));

		// When
		service.launch(topologyId);
	}
}
