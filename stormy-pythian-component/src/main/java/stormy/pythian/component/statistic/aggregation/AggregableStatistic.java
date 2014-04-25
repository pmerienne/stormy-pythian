package stormy.pythian.component.statistic.aggregation;

import java.io.Serializable;

public interface AggregableStatistic<T> extends Serializable {

    T init(Number feature);

    T combine(T val1, T val2);

    T zero();

    Object toFeature(T value);

}
