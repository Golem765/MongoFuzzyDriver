package com.dgrynets.mongo.fuzzy.membership;

import java.util.function.Function;

/**
 * Created by golem765 on 2019-06-02.
 * Part of mongofuzzydriver project.
 * Module com.dgrynets.mongo.fuzzy.membership.
 */
public class RTriangleMFunction implements Function<Double, Double> {
    private final Double b;
    private final Double c;
    private final Double diff;

    public RTriangleMFunction(Double b, Double c) {
        assert b < c;

        this.b = b;
        this.c = c;

        this.diff = c - b;
    }

    @Override
    public Double apply(Double val) {
        if (val <= b) {
            return 0.0;
        } else if (val <= c) {
            return (val - b) / diff;
        } else {
            return 1.0;
        }
    }
}
