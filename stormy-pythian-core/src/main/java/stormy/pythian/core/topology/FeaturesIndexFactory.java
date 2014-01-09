package stormy.pythian.core.topology;

import static stormy.pythian.model.instance.FeaturesIndex.Builder.featuresIndex;
import static stormy.pythian.model.instance.FeaturesIndex.Builder.from;

import java.util.List;
import java.util.Map;

import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.core.configuration.ConnectionConfiguration;
import stormy.pythian.core.configuration.OutputStreamConfiguration;
import stormy.pythian.model.instance.FeaturesIndex;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class FeaturesIndexFactory {

	private final Table<String, String, FeaturesIndex> featuresIndexes = HashBasedTable.create();


	public Map<String, FeaturesIndex> getFeaturesIndexes(ComponentConfiguration configuration) {
		return featuresIndexes.row(configuration.getId());
	}

	
	public FeaturesIndex getFeaturesIndex(ComponentConfiguration configuration, String inputStreamName) {
		FeaturesIndex inputStreamFeaturesIndex = featuresIndexes.get(configuration.id, inputStreamName);
		return inputStreamFeaturesIndex;
	}

	public void registerBuildedComponent(ComponentConfiguration configuration, List<ConnectionConfiguration> outputConnections) {
		for (ConnectionConfiguration connection : outputConnections) {
			OutputStreamConfiguration outputStreamConfiguration = configuration.findOutputStreamByName(connection.fromStreamName);
			createFeaturesIndex(configuration.getId(), outputStreamConfiguration);
		}
	}

	private FeaturesIndex createFeaturesIndex(String componentId, OutputStreamConfiguration outputStreamConfiguration) {
		FeaturesIndex featuresIndex;

		if (outputStreamConfiguration.hasInputStreamSource()) {
			FeaturesIndex inputStreamFeaturesIndex = featuresIndexes.get(componentId, outputStreamConfiguration.getInputStreamSource());
			featuresIndex = from(inputStreamFeaturesIndex).with(outputStreamConfiguration.getNewFeatureNames()).build();
		} else {
			featuresIndex = featuresIndex().with(outputStreamConfiguration.getNewFeatureNames()).build();
		}

		featuresIndexes.put(componentId, outputStreamConfiguration.getStreamName(), featuresIndex);
		return featuresIndex;
	}

}
