package stormy.pythian.testing;

import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import stormy.pythian.model.instance.Instance;
import backtype.storm.testing.FeederSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class InstanceFeederSpout extends FeederSpout {

	private static final long serialVersionUID = 1L;

	public InstanceFeederSpout() {
		super(new Fields(INSTANCE_FIELD));
	}

	public void feed(Instance... instances) {
		for (Instance instance : instances) {
			super.feed(new Values(instance));
		}
	}

}
