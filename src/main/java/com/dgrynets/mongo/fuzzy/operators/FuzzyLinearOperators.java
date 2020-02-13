package com.dgrynets.mongo.fuzzy.operators;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by golem765 on 2019-06-02.
 * Part of mongofuzzydriver project.
 * Module com.dgrynets.mongo.fuzzy.operators.
 */
public class FuzzyLinearOperators {
    private static double getDiff(Double number) {
        if (number.compareTo(0.0) <= 0) {
            return number / 10.0;
        }
        int length = (int) (Math.log10(number) + 1);
        return number / (Math.max(1, (length - 1)) * 10.0);
    }

    public static Bson feq(String field, Double value, Double leftEdge, Double rightEdge, Double threshold) {
        if (leftEdge == null) {
            leftEdge = value - getDiff(value);
        }
        if (rightEdge == null) {
            rightEdge = value + getDiff(value);
        }
        if (threshold == null) {
            threshold = 0.0;
        }

        var leftDiff = value - leftEdge;
        var rightDiff = rightEdge - value;

        NumberFormat formatter = new DecimalFormat("#0.00");

        return Filters.where(
                String.format(
                        "function() {" +
                                "var val = this['%s'];" +
                                "var membership = 0;" +
                                "if (val == %s) {return true;}" +
                                "if (val >= %s && val <= %s) {" +
                                "   membership = (val - %s) / (%s);" +
                                "} else if (val >= %s && val <= %s) {" +
                                "   membership = (%s - val) / (%s);" +
                                "}" +
                                "return membership > %s;" +
                                "}",
                        field,
                        formatter.format(value),
                        formatter.format(leftEdge),
                        formatter.format(value),
                        formatter.format(leftEdge),
                        formatter.format(leftDiff),
                        formatter.format(value),
                        formatter.format(rightEdge),
                        formatter.format(rightEdge),
                        formatter.format(rightDiff),
                        formatter.format(threshold)));
    }

    public static Bson feq(String field, Double value) {
        return feq(field, value, null, null, null);
    }

    public static Bson feq(String field, Double value, Double threshold) {
        return feq(field, value, null, null, threshold);
    }

    public static Bson fgt(String field, Double value, Double leftEdge, Double threshold) {
        if (leftEdge == null) {
            leftEdge = value - getDiff(value);
        }
        if (threshold == null) {
            threshold = 0.0;
        }

        var diff = value - leftEdge;
        NumberFormat formatter = new DecimalFormat("#0.00");

        return Filters.where(
                String.format(
                        "function() {" +
                                "var val = this['%s'];" +
                                "var membership = 0;" +
                                "if (val >= %s) {return true;}" +
                                "if (val >= %s && val <= %s) {" +
                                "   membership = (val - %s) / (%s);" +
                                "}" +
                                "return membership > %s;" +
                                "}",
                        field,
                        formatter.format(value),
                        formatter.format(leftEdge),
                        formatter.format(value),
                        formatter.format(leftEdge),
                        formatter.format(diff),
                        formatter.format(threshold)));
    }

    public static Bson fgt(String field, Double value) {
        return fgt(field, value, null, null);
    }

    public static Bson fgt(String field, Double value, Double threshold) {
        return fgt(field, value, null, threshold);
    }

    public static Bson flt(String field, Double value, Double rightEdge, Double threshold) {
        if (rightEdge == null) {
            rightEdge = value + value / getDiff(value);
        }
        if (threshold == null) {
            threshold = 0.0;
        }

        var diff = rightEdge - value;

        NumberFormat formatter = new DecimalFormat("#0.00");

        return Filters.where(
                String.format(
                        "function() {" +
                                "var val = this['%s'];" +
                                "var membership = 0;" +
                                "if (val <= %s) {return true;}" +
                                "if (val >= %s && val <= %s) {" +
                                "   membership = (%s - val) / (%s);" +
                                "}" +
                                "return membership > %s;" +
                                "}",
                        field,
                        formatter.format(value),
                        formatter.format(value),
                        formatter.format(rightEdge),
                        formatter.format(rightEdge),
                        formatter.format(diff),
                        formatter.format(threshold)));
    }

    public static Bson flt(String field, Double value) {
        return flt(field, value, null, null);
    }

    public static Bson flt(String field, Double value, Double threshold) {
        return flt(field, value, null, threshold);
    }

    public static Bson fin(String field, String fuzzySet, Double threshold) {
        if (threshold == null) {
            threshold = 0.0;
        }

        return new Document("_fuzzy_" + field + "." + fuzzySet, new Document("$gt", threshold));
    }

    public static Bson fin(String field, String fuzzySet) {
        return fin(field, fuzzySet, null);
    }
}
