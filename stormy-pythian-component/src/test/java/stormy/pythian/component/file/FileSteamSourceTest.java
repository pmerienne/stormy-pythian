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
package stormy.pythian.component.file;

import static backtype.storm.utils.Utils.sleep;
import static java.io.File.createTempFile;
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.component.file.FileSteamSource.LINE_FEATURE;
import static stormy.pythian.model.instance.Instance.Builder.instance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import storm.trident.Stream;
import stormy.pythian.model.instance.FeaturesIndex;
import stormy.pythian.model.instance.OutputFeaturesMapper;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;
import backtype.storm.utils.Utils;

public class FileSteamSourceTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 25000;

	@Test
	public void should_read_all_file_lines() throws IOException {
		// Given
		FeaturesIndex index = FeaturesIndex.Builder.featuresIndex().with(asList(LINE_FEATURE)).build();
		List<String> lines = asList("Lorem ipsum dolor sit amet", "consectetur adipisicing elit", "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua");
		File tmpFile = createTempFile("test-", ".txt");
		tmpFile.deleteOnExit();
		writeLines(tmpFile, lines);

		Map<String, String> mappings = new HashMap<>();
		mappings.put(LINE_FEATURE, LINE_FEATURE);
		OutputFeaturesMapper mapper = new OutputFeaturesMapper(index, mappings);

		FileSteamSource component = new FileSteamSource();
		setField(component, "filename", tmpFile.getAbsolutePath());
		setField(component, "mapper", mapper);
		setField(component, "topology", topology);

		component.init();
		Stream out = (Stream) getField(component, "out");

		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.listen(out);

		// When
		this.launch();
		System.out.println(cluster.getClusterInfo().get_topologies().get(0));
		System.out.println(cluster.getClusterInfo().get_topologies().get(0).get_status());
		Utils.sleep(TOPOLOGY_START_TIME);
		System.out.println(cluster.getClusterInfo().get_topologies().get(0));
		System.out.println(cluster.getClusterInfo().get_topologies().get(0).get_status());

		// Then
		assertThat(instanceCollector.getCollected()).containsOnly( //
				instance().with(lines.get(0)).build(), //
				instance().with(lines.get(1)).build(), //
				instance().with(lines.get(2)).build() //
				);
	}

	@Test
	public void should_support_new_lines() throws IOException {
		// Given
		FeaturesIndex index = FeaturesIndex.Builder.featuresIndex().with(asList(LINE_FEATURE)).build();
		List<String> firstLines = asList("Lorem ipsum dolor sit amet", "consectetur adipisicing elit", "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua");
		List<String> lastLines = asList("Excepteur sint occaecat cupidatat non proident", "sunt in culpa qui officia deserunt mollit anim");
		File tmpFile = createTempFile("test-", ".txt");
		tmpFile.deleteOnExit();
		writeLines(tmpFile, firstLines);

		Map<String, String> mappings = new HashMap<>();
		mappings.put(LINE_FEATURE, LINE_FEATURE);
		OutputFeaturesMapper mapper = new OutputFeaturesMapper(index, mappings);

		FileSteamSource component = new FileSteamSource();
		setField(component, "filename", tmpFile.getAbsolutePath());
		setField(component, "mapper", mapper);
		setField(component, "topology", topology);

		component.init();
		Stream out = (Stream) getField(component, "out");

		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.listen(out);

		// When
		this.launch();
		sleep(TOPOLOGY_START_TIME);
		appendLines(tmpFile, lastLines);
		sleep(500);

		// Then
		assertThat(instanceCollector.getCollected()).containsOnly( //
				instance().with(firstLines.get(0)).build(), //
				instance().with(firstLines.get(1)).build(), //
				instance().with(firstLines.get(2)).build(), //
				instance().with(lastLines.get(0)).build(), //
				instance().with(lastLines.get(1)).build() //
				);
	}

	public static void appendLines(File file, Collection<String> lines) throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, true);
			IOUtils.writeLines(lines, null, fw);
		} finally {
			IOUtils.closeQuietly(fw);
		}
	}
}
