package stormy.pythian.component.statistic.aggregation;

import java.io.Serializable;

public interface AggregableStatistic<T> extends Serializable {

    T init(Double feature);

    T combine(T val1, T val2);

    T zero();

    Double toFeature(T value);

}
