package stormy.pythian.component.source;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static stormy.pythian.model.annotation.MappingType.USER_SELECTION;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;

import java.util.List;
import java.util.Map;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import stormy.pythian.model.annotation.OutputStream;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.annotation.Topology;
import stormy.pythian.model.component.Component;
import stormy.pythian.model.instance.Instance;
import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@SuppressWarnings("serial")
public abstract class NamedFeaturesSource implements Component {

	@OutputStream(name = "output", type = USER_SELECTION)
	private transient Stream out;

	@Property(name = "Max batch size")
	protected Integer maxBatchSize = 1000;

	@Property(name = "Max task parallelism")
	protected Integer maxTaskParallelism = 1;

	@Topology
	private transient TridentTopology topology;

	@Override
	public void init() {
		PythianBatchSpout spout = new PythianBatchSpout(this, maxTaskParallelism);
		out = topology.newStream(this.getClass() + "-spout-" + randomAlphabetic(5), spout);
	}

	protected abstract List<Instance> nextBatch();

	protected abstract void close();

	protected abstract void open();

	private static class PythianBatchSpout implements IBatchSpout {

		private final NamedFeaturesSource streamSource;
		private final int maxTaskParallelism;

		public PythianBatchSpout(NamedFeaturesSource streamSource, int maxTaskParallelism) {
			this.streamSource = streamSource;
			this.maxTaskParallelism = maxTaskParallelism;
		}

		@Override
		public void emitBatch(long batchId, TridentCollector collector) {
			List<Instance> instances = streamSource.nextBatch();

			for (Instance instance : instances) {
				collector.emit(new Values(instance));
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Map getComponentConfiguration() {
			Config conf = new Config();
			conf.setMaxTaskParallelism(maxTaskParallelism);
			return conf;
		}

		@Override
		public void close() {
			streamSource.close();
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
			streamSource.open();
		}
	}

}