package stormy.pythian.testing;

import static java.util.Arrays.asList;
import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import stormy.pythian.model.instance.Instance;
import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class FixedInstanceSpout implements IBatchSpout {

	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_BATCH_SIZE = 1000;

	private static Map<String, AtomicInteger> PROCESSED_COUNTS = new HashMap<>();
	private static Map<String, AtomicBoolean> STARTERS = new HashMap<>();

	private final String uuid;
	private final int instanceCount;

	private final int maxBatchSize;
	private final HashMap<Long, List<List<Object>>> batches = new HashMap<Long, List<List<Object>>>();

	private int index = 0;
	private final List<Instance> instances;

	public FixedInstanceSpout(Instance... instances) {
		this(DEFAULT_BATCH_SIZE, true, instances);
	}

	public FixedInstanceSpout(boolean autoStart, Instance... instances) {
		this(DEFAULT_BATCH_SIZE, autoStart, instances);
	}

	public FixedInstanceSpout(int maxBatchSize, boolean autoStart, Instance... instances) {
		this.instances = asList(instances);

		this.uuid = UUID.randomUUID().toString();
		this.instanceCount = instances.length;

		this.maxBatchSize = maxBatchSize;

		STARTERS.put(uuid, new AtomicBoolean(autoStart));
		PROCESSED_COUNTS.put(uuid, new AtomicInteger());
	}

	public void start() {
		STARTERS.get(uuid).set(true);
	}

	@Override
	public void ack(long batchId) {
		List<List<Object>> processedInstances = this.batches.remove(batchId);
		if (processedInstances != null && !processedInstances.isEmpty()) {
			for (int i = 0; i < processedInstances.size(); i++) {
				PROCESSED_COUNTS.get(uuid).incrementAndGet();
			}
		}
	}

	public boolean allInstancesProcessed() {
		return PROCESSED_COUNTS.get(uuid).get() == instanceCount;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context) {
		index = 0;
	}

	@Override
	public void emitBatch(long batchId, TridentCollector collector) {
		if (STARTERS.get(uuid).get()) {
			List<List<Object>> batch = this.batches.get(batchId);
			if (batch == null) {
				batch = new ArrayList<List<Object>>();
				for (int i = 0; index < instances.size() && i < maxBatchSize; index++, i++) {
					batch.add(new Values(instances.get(index)));
				}
				this.batches.put(batchId, batch);
			}
			for (List<Object> list : batch) {
				collector.emit(list);
			}
		}
	}

	@Override
	public void close() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getComponentConfiguration() {
		Config conf = new Config();
		conf.setMaxTaskParallelism(1);
		return conf;
	}

	@Override
	public Fields getOutputFields() {
		return new Fields(INSTANCE_FIELD);
	}

}
