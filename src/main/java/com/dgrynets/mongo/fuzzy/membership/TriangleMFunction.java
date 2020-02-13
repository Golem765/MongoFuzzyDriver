package com.dgrynets.mongo.fuzzy.membership;

import java.util.function.Function;

/**
 * Created by golem765 on 2019-06-02.
 * Part of mongofuzzydriver project.
 * Module com.dgrynets.mongo.fuzzy.membership.
 */
public class TriangleMFunction implements Function<Double, Double> {
    private final Double a;
    private final Double b;
    private final Double c;
    private final Double diffBA;
    private final Double diffCB;

    public TriangleMFunction(Double a, Double b, Double c) {
        this.a = a;
        this.b = b;
        this.c = c;

        this.diffBA = b - a;
        this.diffCB = c - b;
    }

    @Override
    public Double apply(Double val) {
        if (val >= a && val <= b) {
            return (val - a) / diffBA;
        }

        if (val >= b && val <= c) {
            return (c - val) / diffCB;
        }

        return 0.0;
    }
}
