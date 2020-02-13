package com.dgrynets.mongo.fuzzy.indexes;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonNumber;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by golem765 on 2019-06-02.
 * Part of mongofuzzydriver project.
 * Module com.dgrynets.mongo.fuzzy.indexes.
 */
public class FuzzyIndexes {

    public static void create(MongoCollection<Document> collection, String field, List<FuzzySet> fuzzySets) {
        Map<Double, Document> memberships = new HashMap<>();

        DistinctIterable<Double> values = collection.distinct(field, Double.class);
        for (Double number : values) {
            Document membershipDoc = new Document();
            fuzzySets.forEach(set -> {
                membershipDoc.put(set.getName(), set.computeMembership(number));
            });
            memberships.put(number, membershipDoc);
        }

        memberships.forEach((number, membership) -> {
            collection.updateMany(new Document(field, number.doubleValue()),
                                  new Document("$set", new Document("_fuzzy_" + field, membership)));
        });
    }
}
