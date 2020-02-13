package com.dgrynets.mongo.fuzzy.indexes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

/**
 * Created by golem765 on 2019-06-02.
 * Part of mongofuzzydriver project.
 * Module com.dgrynets.mongo.fuzzy.indexes.
 */
public class FuzzySet {
    private final String name;
    private final Function<Double, Double> membershipFunction;

    public FuzzySet(String name, Function<Double, Double> membershipFunction) {
        this.name = name;
        this.membershipFunction = membershipFunction;
    }

    public String getName() {
        return name;
    }

    public Double computeMembership(Double val) {
        return BigDecimal.valueOf(membershipFunction.apply(val)).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
