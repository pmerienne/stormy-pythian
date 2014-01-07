package stormy.pythian.core.topology;

import static stormy.pythian.model.instance.Instance.INSTANCE_FIELD;
import static stormy.pythian.model.instance.Instance.NEW_INSTANCE_FIELD;
import storm.trident.Stream;
import storm.trident.operation.Assembly;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class ReplaceInstanceField implements Assembly {

	@Override
	public Stream apply(Stream input) {
		return input //
				.project(new Fields(NEW_INSTANCE_FIELD)) //
				.each(new Fields(NEW_INSTANCE_FIELD), new Duplicate(), new Fields(INSTANCE_FIELD)) //
				.project(new Fields(INSTANCE_FIELD));
	}

	private static class Duplicate extends BaseFunction {

		private static final long serialVersionUID = 4238149014859176374L;

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Object field = tuple.getValue(0);
			collector.emit(new Values(field));
		}

	}
}
