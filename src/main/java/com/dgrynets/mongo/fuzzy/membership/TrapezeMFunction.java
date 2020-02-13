package com.dgrynets.mongo.fuzzy.membership;

import java.util.function.Function;

/**
 * Created by golem765 on 2019-06-02.
 * Part of mongofuzzydriver project.
 * Module com.dgrynets.mongo.fuzzy.membership.
 */
public class TrapezeMFunction implements Function<Double, Double> {

    private final Double a;
    private final Double b;
    private final Double c;
    private final Double d;

    private final Double diffBA;
    private final Double diffDC;

    public TrapezeMFunction(Double a, Double b, Double c, Double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

        this.diffBA = b - a;
        this.diffDC = d - c;
    }

    @Override
    public Double apply(Double val) {
        if (val >= a && val <= b) {
            return (val - a) / diffBA;
        }

        if (val >= b && val <= c) {
            return 1.0;
        }

        if (val >= b && val <= d) {
            return (d - val) / (diffDC);
        }

        return 0.0;
    }
}
