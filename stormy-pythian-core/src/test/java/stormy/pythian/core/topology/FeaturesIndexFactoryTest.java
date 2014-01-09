package stormy.pythian.core.topology;

import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;
import static stormy.pythian.model.instance.FeatureType.INTEGER;
import static stormy.pythian.model.instance.FeatureType.TEXT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.Stream;
import stormy.pythian.core.configuration.ComponentConfiguration;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.ExpectedFeature;
import stormy.pythian.model.annotation.InputStream;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.component.Component;

@RunWith(MockitoJUnitRunner.class)
public class FeaturesIndexFactoryTest {

	@InjectMocks
	private FeaturesIndexFactory factory;

	/**
	 * <pre>
	 * 				c1out1-f1, c1out1-f2
	 * |	out1 |--------------------------| in1	|
	 * |		 |							|		|
	 * | C1		 |							|	C2	|
	 * |		 |							|		|
	 * | 	out2 |							| in2	|
	 * |		 |							|		|
	 * 
	 * </pre>
	 */
	@Test
	public void should_register_features_indexes() {
		// Hard to test!! I'm too tired!
	}

	@Documentation(name = "file stream source")
	private static class FileStreamSource implements Component {

		@OutputStream(name = "lines", newFeatures = { @ExpectedFeature(name = "line", type = TEXT) })
		private Stream lines;

		@Override
		public void init() {
		}

	}

	@Documentation(name = "file stream source")
	private static class LogParser implements Component {

		@InputStream(name = "lines", expectedFeatures = { @ExpectedFeature(name = "line", type = TEXT) })
		private Stream lines;

		@OutputStream(name = "data", from = "lines", newFeatures = { @ExpectedFeature(name = "path", type = TEXT), @ExpectedFeature(name = "response_time", type = INTEGER) })
		private Stream data;

		@Override
		public void init() {
		}
	}

	@Documentation(name = "file stream source")
	private static class StoreData implements Component {

		@InputStream(name = "data", type = USER_SELECTION)
		private Stream data;

		@Override
		public void init() {
		}
	}

	// private ComponentConfiguration createConfiguration(Class<? extends
	// Component> componentClass) {
	// // ComponentDescriptionFactory
	// }
}
