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

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

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
import stormy.pythian.core.ioc.StormyPythianCoreConfig;
import stormy.pythian.core.topology.PythianTopology;
import backtype.storm.LocalCluster;
import backtype.storm.utils.Utils;

public class SandBox {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(StormyPythianCoreConfig.class);
		
		ComponentDescriptionFactory componentDescriptionFactory = context.getBean(ComponentDescriptionFactory.class);

		ComponentDescription randomWordSource = componentDescriptionFactory.createDeclaration(RandomWordSource.class);
		ComponentConfiguration randomWordSourceConfiguration = new ComponentConfiguration(randomAlphabetic(6), randomWordSource);
		randomWordSourceConfiguration.outputStreams.add(new OutputStreamConfiguration(randomWordSource.outputStreamDescriptions.get(0), createMappings(RandomWordSource.WORD_FEATURE, "random word")));

		ComponentDescription wordCount = componentDescriptionFactory.createDeclaration(WordCount.class);
		ComponentConfiguration wordCountConfiguration = new ComponentConfiguration(randomAlphabetic(6), wordCount);
		wordCountConfiguration.inputStreams.add(new InputStreamConfiguration(wordCount.inputStreamDescriptions.get(0), createMappings(WordCount.WORD_FEATURE, "random word")));
		wordCountConfiguration.outputStreams.add(new OutputStreamConfiguration(wordCount.outputStreamDescriptions.get(0), createMappings(WordCount.COUNT_FEATURE, "word count")));

		ComponentDescription consoleOutput = componentDescriptionFactory.createDeclaration(ConsoleOutput.class);
		ComponentConfiguration consoleOutputConfiguration = new ComponentConfiguration(randomAlphabetic(6), consoleOutput);
		consoleOutputConfiguration.inputStreams.add(new InputStreamConfiguration(consoleOutput.inputStreamDescriptions.get(0), Arrays.asList("random word", "word count")));

		LocalCluster cluster = new LocalCluster();

		PythianToplogyConfiguration topologyConfiguration = new PythianToplogyConfiguration();
		topologyConfiguration.getComponents().add(randomWordSourceConfiguration);
		topologyConfiguration.getComponents().add(wordCountConfiguration);
		topologyConfiguration.getComponents().add(consoleOutputConfiguration);

		topologyConfiguration.getConnections().add(new ConnectionConfiguration(randomWordSourceConfiguration.id, "out", wordCountConfiguration.id, "in"));
		topologyConfiguration.getConnections().add(new ConnectionConfiguration(wordCountConfiguration.id, "out", consoleOutputConfiguration.id, "in"));

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
