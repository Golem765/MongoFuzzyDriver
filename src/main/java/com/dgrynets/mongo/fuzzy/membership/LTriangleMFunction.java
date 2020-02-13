package com.dgrynets.mongo.fuzzy.membership;

import java.util.function.Function;

/**
 * Created by golem765 on 2019-06-02.
 * Part of mongofuzzydriver project.
 * Module com.dgrynets.mongo.fuzzy.membership.
 */
public class LTriangleMFunction implements Function<Double, Double> {
    private final Double a;
    private final Double b;
    private final Double diff;

    public LTriangleMFunction(Double a, Double b) {
        assert a < b;

        this.a = a;
        this.b = b;
        this.diff = b - a;
    }

    @Override
    public Double apply(Double val) {
        if (val <= a) {
            return 1.0;
        } else if (val <= b) {
            return (b - val) / diff;
        } else {
            return 0.0;
        }
    }
}
