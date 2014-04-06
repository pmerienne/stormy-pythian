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
package stormy.pythian.component.source.file;

import static com.google.common.base.Objects.firstNonNull;
import static org.apache.commons.io.FilenameUtils.getName;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static stormy.pythian.model.annotation.ComponentType.STREAM_SOURCE;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.Mapper;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.Topology;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.OutputFixedFeaturesMapper;
import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@Documentation(name = "File source", description = "Read and parse a given file", type = STREAM_SOURCE)
public class FileSource implements Component {

	private static final long serialVersionUID = -1157270927374405269L;

	public static final String LINE_FEATURE = "file line";

	private static final int DEFAULT_MAX_BATCH_SIZE = 500;

	@OutputStream(name = "lines", newFeatures = { @ExpectedFeature(name = LINE_FEATURE, type = String.class) })
	private Stream out;

	@Mapper(stream = "lines")
	private OutputFixedFeaturesMapper mapper;

	@Property(name = "File", description = "The full path of the file to read", mandatory = true)
	private String filename;

	@Property(name = "Max batch size")
	private Integer maxBatchSize;

	@Topology
	private TridentTopology topology;

	@Override
	public void init() {
		FileSpout spout = new FileSpout(filename, mapper, firstNonNull(maxBatchSize, DEFAULT_MAX_BATCH_SIZE));
		out = topology.newStream(getName(filename) + "-spout-" + randomAlphabetic(5), spout);
	}

	private static class FileSpout implements IBatchSpout {

		private static final long serialVersionUID = -4793933286470308346L;

		private final static Logger LOGGER = Logger.getLogger(FileSpout.class);

		private final String filename;
		private final OutputFixedFeaturesMapper mapper;
		private final int maxBatchSize;

		private Long currentPosition = 0L;

		public FileSpout(String filename, OutputFixedFeaturesMapper mapper, int maxBatchSize) {
			this.filename = filename;
			this.mapper = mapper;
			this.maxBatchSize = maxBatchSize;
		}

		@Override
		public void emitBatch(long batchId, TridentCollector collector) {
			if (this.hasNext()) {
				List<Instance> instances = this.nextBatch();

				for (Instance instance : instances) {
					collector.emit(new Values(instance));
				}
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Map getComponentConfiguration() {
			Config conf = new Config();
			conf.setMaxTaskParallelism(1);
			return conf;
		}

		@Override
		public void close() {
		}

		@Override
		public void ack(long batchId) {
		}

		@Override
		public Fields getOutputFields() {
			return new Fields(NEW_INSTANCE_FIELD);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void open(Map conf, TopologyContext context) {
		}

		public boolean hasNext() {
			RandomAccessFile file = null;
			try {
				file = new RandomAccessFile(filename, "r");
				file.seek(currentPosition);
				return file.readLine() != null;
			} catch (IOException e) {
				LOGGER.error("Error while reading " + filename, e);
				return false;
			} finally {
				try {
					file.close();
				} catch (IOException e) {
					LOGGER.error("Error while closing " + filename, e);
				}
			}
		}

		public List<Instance> nextBatch() {
			List<Instance> instances = new ArrayList<>(maxBatchSize);

			RandomAccessFile file = null;
			try {
				file = new RandomAccessFile(filename, "r");
				file.seek(currentPosition);

				String line;
				while (instances.size() < maxBatchSize && (line = file.readLine()) != null) {
					try {
						Map<String, Object> features = new HashMap<>();
						features.put(LINE_FEATURE, line);

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
				return null;
			} finally {
				try {
					file.close();
				} catch (IOException e) {
					LOGGER.error("Error while closing " + filename, e);
				}
			}

		}

	}

}
