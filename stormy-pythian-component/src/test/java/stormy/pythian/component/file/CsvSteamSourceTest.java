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

import static java.io.File.createTempFile;
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static stormy.pythian.model.instance.InstanceTestBuilder.instance;
import static stormy.pythian.model.instance.OutputUserSelectionFeaturesMapperTestBuilder.outputUserSelectionFeaturesMapper;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import storm.trident.Stream;
import stormy.pythian.model.instance.OutputUserSelectionFeaturesMapper;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;
import backtype.storm.utils.Utils;

public class CsvSteamSourceTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@Test
	public void should_read_all_file_lines() throws IOException {
		// Given
		File tmpFile = createTempFile("test-", ".txt");
		tmpFile.deleteOnExit();
		writeLines(tmpFile, asList("pmerienne, pierre, merienne", "jchanut,julie,chanut", "bduteil, Brice, Duteil"));

		OutputUserSelectionFeaturesMapper outputMapper = outputUserSelectionFeaturesMapper("login", "firstname", "lastname").select("login", "firstname", "lastname").build();

		CsvSteamSource component = new CsvSteamSource();
		setField(component, "filename", tmpFile.getAbsolutePath());
		setField(component, "mapper", outputMapper);
		setField(component, "topology", topology);

		component.init();
		Stream out = (Stream) getField(component, "out");

		InstanceCollector instanceCollector = new InstanceCollector();
		instanceCollector.collect(out);

		// When
		this.launch();
		Utils.sleep(TOPOLOGY_START_TIME);

		// Then
		assertThat(instanceCollector.getCollected()).containsOnly( //
				instance().with("pmerienne").with("pierre").with("merienne").build(), //
				instance().with("jchanut").with("julie").with("chanut").build(), //
				instance().with("bduteil").with("Brice").with("Duteil").build() //
				);
	}

}
