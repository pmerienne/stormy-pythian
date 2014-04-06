package stormy.pythian.component.source.file;

import static com.google.common.collect.Lists.newArrayList;
import static stormy.pythian.model.annotation.ComponentType.STREAM_SOURCE;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import stormy.pythian.component.source.ListedFeaturesSource;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.instance.Instance;

import com.google.common.base.Splitter;

@SuppressWarnings("serial")
@Documentation(name = "Csv stream source", description = "Read and parse a given csv file", type = STREAM_SOURCE)
public class CsvSource extends ListedFeaturesSource {

	private final static Logger LOGGER = Logger.getLogger(CsvSource.class);

	@Property(name = "File", description = "The full path of the file to read", mandatory = true)
	private String filename;

	private Long currentPosition = 0L;

	@Override
	protected List<Instance> nextBatch() {
		List<Instance> instances = new ArrayList<>(maxBatchSize);

		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(filename, "r");
			file.seek(currentPosition);

			String line;
			while (instances.size() < maxBatchSize && (line = file.readLine()) != null) {
				try {
					List<?> features = newArrayList(Splitter.on(",").limit(mapper.size()).trimResults().split(line));

					Instance instance = Instance.newInstance(mapper, features);
					instances.add(instance);
				} catch (Exception ex) {
					LOGGER.warn("Skipped instance : " + line);
				}
			}

			currentPosition = file.getFilePointer();
			LOGGER.debug("Progress : " + new DecimalFormat("##.##").format(100.0 * (double) currentPosition / (double) file.length()) + "%");
			return instances;
		} catch (IOException e) {
			LOGGER.error("Error while reading " + filename, e);
			return Collections.emptyList();
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				LOGGER.error("Error while closing " + filename, e);
			}
		}
	}

	@Override
	protected void close() {
	}

	@Override
	protected void open() {
	}

}
