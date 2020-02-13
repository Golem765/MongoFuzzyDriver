package com.dgrynets.mongo.fuzzy.operators;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by golem765 on 2019-06-02.
 * Part of mongofuzzydriver project.
 * Module com.dgrynets.mongo.fuzzy.operators.
 */
public class FuzzyLogicalOperators {

    public static Bson fuzzyOr(Map<String, String> conditions, Double threshold) {
        if (threshold == null) {
            threshold = 0.0;
        }

        NumberFormat formatter = new DecimalFormat("#0.00");

        String conditionBuilder = "Math.max(" +
                conditions.entrySet().stream().map(entry -> String.format(
                        "this['_fuzzy_%s']['%s']",
                        entry.getKey(),
                        entry.getValue())
                ).collect(Collectors.joining(",")) +
                ")";
        return Filters.where(
                String.format(
                        "function() {" +
                                "var membership = %s;" +
                                "return membership > %s;" +
                                "}",
                        conditionBuilder,
                        formatter.format(threshold)));
    }

    public static Bson fuzzyOr(Map<String, String> conditions) {
        return fuzzyOr(conditions, null);
    }

    public static Bson fand(Map<String, String> conditions, Double threshold) {
        if (threshold == null) {
            threshold = 0.0;
        }

        NumberFormat formatter = new DecimalFormat("#0.00");

        String conditionBuilder = "Math.min(" +
                conditions.entrySet().stream().map(entry -> String.format(
                        "this['_fuzzy_%s']['%s']",
                        entry.getKey(),
                        entry.getValue())
                ).collect(Collectors.joining(",")) +
                ")";
        return Filters.where(
                String.format(
                        "function() {" +
                                "var membership = %s;" +
                                "return membership > %s;" +
                                "}",
                        conditionBuilder,
                        formatter.format(threshold)));
    }

    public static Bson fand(Map<String, String> conditions) {
        return fand(conditions, null);
    }
}
