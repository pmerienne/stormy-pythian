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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import storm.trident.Stream;
import stormy.pythian.model.instance.FixedFeaturesMapper;
import stormy.pythian.testing.InstanceCollector;
import stormy.pythian.testing.TridentIntegrationTest;
import backtype.storm.utils.Utils;

public class FileSteamSourceTest extends TridentIntegrationTest {

	private static final int TOPOLOGY_START_TIME = 5000;

	@Test
	public void should_read_all_file_lines() throws IOException {
		// Given
		List<String> lines = asList("Lorem ipsum dolor sit amet", "consectetur adipisicing elit", "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua");
		File tmpFile = createTempFile("test-", ".txt");
		tmpFile.deleteOnExit();
		writeLines(tmpFile, lines);

		Map<String, String> mappings = new HashMap<>();
		mappings.put(LINE_FEATURE, LINE_FEATURE);
		FixedFeaturesMapper mapper = new FixedFeaturesMapper(mappings);

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
		Utils.sleep(TOPOLOGY_START_TIME);

		// Then
		assertThat(instanceCollector.getCollected()).containsOnly( //
				instance().with(LINE_FEATURE, lines.get(0)).build(), //
				instance().with(LINE_FEATURE, lines.get(1)).build(), //
				instance().with(LINE_FEATURE, lines.get(2)).build() //
				);
	}

	@Test
	public void should_support_new_lines() throws IOException {
		// Given
		List<String> firstLines = asList("Lorem ipsum dolor sit amet", "consectetur adipisicing elit", "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua");
		List<String> lastLines = asList("Excepteur sint occaecat cupidatat non proident", "sunt in culpa qui officia deserunt mollit anim");
		File tmpFile = createTempFile("test-", ".txt");
		tmpFile.deleteOnExit();
		writeLines(tmpFile, firstLines);

		Map<String, String> mappings = new HashMap<>();
		mappings.put(LINE_FEATURE, LINE_FEATURE);
		FixedFeaturesMapper mapper = new FixedFeaturesMapper(mappings);

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
				instance().with(LINE_FEATURE, firstLines.get(0)).build(), //
				instance().with(LINE_FEATURE, firstLines.get(1)).build(), //
				instance().with(LINE_FEATURE, firstLines.get(2)).build(), //
				instance().with(LINE_FEATURE, lastLines.get(0)).build(), //
				instance().with(LINE_FEATURE, lastLines.get(1)).build() //
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
