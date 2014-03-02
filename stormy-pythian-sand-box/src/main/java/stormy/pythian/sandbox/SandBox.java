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
package stormy.pythian.sandbox;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static stormy.pythian.core.configuration.PythianStateConfigurationTestBuilder.stateConfiguration;
import static stormy.pythian.state.TransactionMode.NONE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.ConnectionConfiguration;
import stormy.pythian.core.configuration.InputStreamConfiguration;
import stormy.pythian.core.configuration.OutputStreamConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.core.description.ComponentDescription;
import stormy.pythian.core.description.ComponentDescriptionFactory;
import stormy.pythian.core.description.PythianStateDescription;
import stormy.pythian.core.description.PythianStateDescriptionFactory;
import stormy.pythian.core.ioc.CoreConfiguration;
import stormy.pythian.core.topology.PythianTopology;
import stormy.pythian.state.memory.InMemoryPythianState;
import backtype.storm.LocalCluster;
import backtype.storm.utils.Utils;

@SuppressWarnings("resource")
public class SandBox {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(CoreConfiguration.class);
		ComponentDescriptionFactory componentDescriptionFactory = context.getBean(ComponentDescriptionFactory.class);
		PythianStateDescriptionFactory stateDescriptionFactory = context.getBean(PythianStateDescriptionFactory.class);
		
		PythianStateDescription inMemoryDescription = stateDescriptionFactory.createDescription(InMemoryPythianState.class);
		ComponentDescription randomWordSource = componentDescriptionFactory.createDescription(RandomWordSource.class);
		ComponentConfiguration randomWordSourceConfiguration = new ComponentConfiguration(randomAlphabetic(6), randomWordSource);
		randomWordSourceConfiguration.add(new OutputStreamConfiguration(randomWordSource.getOutputStreams().get(0), createMappings(RandomWordSource.WORD_FEATURE, "random word")));

		ComponentDescription wordCount = componentDescriptionFactory.createDescription(WordCount.class);
		ComponentConfiguration wordCountConfiguration = new ComponentConfiguration(randomAlphabetic(6), wordCount);
		wordCountConfiguration.add(new InputStreamConfiguration(wordCount.getInputStreams().get(0), createMappings(WordCount.WORD_FEATURE, "random word")));
		wordCountConfiguration.add(new OutputStreamConfiguration(wordCount.getOutputStreams().get(0), createMappings(WordCount.COUNT_FEATURE, "word count")));
		wordCountConfiguration.add(stateConfiguration(inMemoryDescription).name("count state").with("Transaction mode", NONE).with("Name", "Word count").build());
		
		ComponentDescription consoleOutput = componentDescriptionFactory.createDescription(ConsoleOutput.class);
		ComponentConfiguration consoleOutputConfiguration = new ComponentConfiguration(randomAlphabetic(6), consoleOutput);
		consoleOutputConfiguration.add(new InputStreamConfiguration(consoleOutput.getInputStreams().get(0), Arrays.asList("random word", "word count")));

		LocalCluster cluster = new LocalCluster();

		PythianToplogyConfiguration topologyConfiguration = new PythianToplogyConfiguration();
		topologyConfiguration.add(randomWordSourceConfiguration);
		topologyConfiguration.add(wordCountConfiguration);
		topologyConfiguration.add(consoleOutputConfiguration);

		topologyConfiguration.add(new ConnectionConfiguration(randomWordSourceConfiguration.getId(), "out", wordCountConfiguration.getId(), "in"));
		topologyConfiguration.add(new ConnectionConfiguration(wordCountConfiguration.getId(), "out", consoleOutputConfiguration.getId(), "in"));

		try {
			PythianTopology pythianTopology = new PythianTopology();
			pythianTopology.build(topologyConfiguration);

			cluster.submitTopology(SandBox.class.getSimpleName(), pythianTopology.getTridentConfig(), pythianTopology.getStormTopology());

			Utils.sleep(120000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cluster.shutdown();
		}

	}

	private static Map<String, String> createMappings(String inside, String outside) {
		Map<String, String> mappings = new HashMap<>();
		mappings.put(inside, outside);
		return mappings;
	}
}
