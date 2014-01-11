package stormy.pythian.core.topology;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.ConnectionConfiguration;
import stormy.pythian.core.configuration.InputStreamConfiguration;
import stormy.pythian.core.configuration.OutputStreamConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.model.instance.FeaturesIndex;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class FeaturesIndexFactory {

	private Table<String, String, FeaturesIndex> outputFeaturesIndexes;
	private Table<String, String, FeaturesIndex> inputFeaturesIndexes;
	private PythianToplogyConfiguration topologyConfiguration;

	public void initTable(PythianToplogyConfiguration topologyConfiguration) {
		this.outputFeaturesIndexes = HashBasedTable.create();
		this.inputFeaturesIndexes = HashBasedTable.create();
		this.topologyConfiguration = topologyConfiguration;
	}

	public Map<String, FeaturesIndex> createInputFeaturesIndexes(ComponentConfiguration component) {
		Map<String, FeaturesIndex> indexes = new HashMap<>(component.getInputStreams().size());

		for (InputStreamConfiguration inputStream : component.getInputStreams()) {
			String streamName = inputStream.getStreamName();
			FeaturesIndex index = createFeaturesIndex(component.getId(), inputStream);
			indexes.put(streamName, index);
		}

		return indexes;
	}

	private FeaturesIndex createFeaturesIndex(String componentId, InputStreamConfiguration inputStream) {
		FeaturesIndex index = null;

		String streamName = inputStream.getStreamName();
		ConnectionConfiguration connection = topologyConfiguration.findConnectionTo(componentId, streamName);

		if (connection == null) {
			switch (inputStream.getMappingType()) {
			case FIXED_FEATURES:
				index = new FeaturesIndex(inputStream.getMappings().values());
				break;
			case USER_SELECTION:
				index = new FeaturesIndex(inputStream.getSelectedFeatures());
				break;
			}
		} else {
			index = outputFeaturesIndexes.get(connection.from, connection.fromStreamName);
		}

		inputFeaturesIndexes.put(componentId, streamName, index);

		return index;
	}

	public Map<String, FeaturesIndex> createOutputFeaturesIndexes(ComponentConfiguration component) {
		Map<String, FeaturesIndex> indexes = new HashMap<>(component.getOutputStreams().size());

		for (OutputStreamConfiguration stream : component.getOutputStreams()) {
			String streamName = stream.getStreamName();
			FeaturesIndex index = createFeaturesIndex(component.getId(), stream);
			indexes.put(streamName, index);
		}

		return indexes;
	}

	private FeaturesIndex createFeaturesIndex(String componentId, OutputStreamConfiguration outputStream) {
		FeaturesIndex index = null;

		Collection<String> newFeatures = outputStream.getMappings().values();

		if (!outputStream.hasInputStreamSource()) {
			index = new FeaturesIndex(newFeatures);
		} else {
			FeaturesIndex inputIndex = inputFeaturesIndexes.get(componentId, outputStream.getInputStreamSource());
			index = FeaturesIndex.Builder.from(inputIndex).with(newFeatures).build();
		}

		outputFeaturesIndexes.put(componentId, outputStream.getStreamName(), index);
		return index;
	}

}
