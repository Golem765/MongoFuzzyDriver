package com.dgrynets.mongo;

import com.dgrynets.mongo.fuzzy.indexes.FuzzyIndexes;
import com.dgrynets.mongo.fuzzy.indexes.FuzzySet;
import com.dgrynets.mongo.fuzzy.membership.LTriangleMFunction;
import com.dgrynets.mongo.fuzzy.membership.RTriangleMFunction;
import com.dgrynets.mongo.fuzzy.membership.TrapezeMFunction;
import com.dgrynets.mongo.fuzzy.operators.FuzzyLogicalOperators;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.dgrynets.mongo.fuzzy.operators.FuzzyLinearOperators.feq;
import static com.dgrynets.mongo.fuzzy.operators.FuzzyLinearOperators.fin;
import static com.dgrynets.mongo.fuzzy.operators.FuzzyLogicalOperators.fand;

/**
 * Created by golem765 on 2019-06-01.
 * Part of mongofuzzydriver project.
 * Module com.dgrynets.mongo.fuzzy.operators.
 */
public class Application {
    public static void main(String[] args) {
        MongoCredential credential = MongoCredential.createCredential(
                "shop", "shop", "1234".toCharArray()
        );
        try (var client = MongoClients.create(MongoClientSettings.builder().credential(credential).build())) {
            MongoDatabase shopDB = client.getDatabase("shop");
            MongoCollection<Document> customersCollection = shopDB.getCollection("customers");

            FuzzyIndexes.create(customersCollection, "age", Arrays.asList(
                    new FuzzySet("young", new LTriangleMFunction(24.0, 30.0)),
                    new FuzzySet("mature", new TrapezeMFunction(24.0, 30.0, 50.0, 66.0)),
                    new FuzzySet("old", new RTriangleMFunction(50.0, 66.0))
            ));

            FuzzyIndexes.create(customersCollection, "spent", Arrays.asList(
                    new FuzzySet("low", new LTriangleMFunction(200.0, 500.0)),
                    new FuzzySet("medium", new TrapezeMFunction(200.0, 500.0, 1000.0, 2000.0)),
                    new FuzzySet("high", new RTriangleMFunction(1000.0, 2000.0))
            ));

            FuzzyIndexes.create(customersCollection, "orders", Arrays.asList(
                    new FuzzySet("few", new LTriangleMFunction(2.0, 5.0)),
                    new FuzzySet("some", new TrapezeMFunction(2.0, 5.0, 10.0, 20.0)),
                    new FuzzySet("lot", new RTriangleMFunction(10.0, 20.0))
            ));

            Document projectionAgeEmail = new Document();
            projectionAgeEmail.put("_id", 0);
            projectionAgeEmail.put("name", 1);
            projectionAgeEmail.put("age", 1);
            projectionAgeEmail.put("email", 1);

            System.out.println("Find young customers:");

            customersCollection
                    .find(fin("age", "young"))
                    .sort(new Document("_fuzzy_age.young", 1))
                    .sort(new Document("age", 1))
                    .projection(projectionAgeEmail)
                    .forEach((Consumer<? super Document>) (Document document) -> {
                        System.out.println(document.toJson());
                    });

            System.out.println("Find customers approximately 26 years old:");

            customersCollection
                    .find(feq("age", 26.0))
                    .projection(projectionAgeEmail)
                    .forEach((Consumer<? super Document>) (Document document) -> {
                        System.out.println(document.toJson());
                    });

            Document projectionAgeSpent = new Document();
            projectionAgeSpent.put("_id", 0);
            projectionAgeSpent.put("name", 1);
            projectionAgeSpent.put("age", 1);
            projectionAgeSpent.put("spent", 1);

            Map<String, String> youngHighSpent = new HashMap<>();
            youngHighSpent.put("age", "young");
            youngHighSpent.put("spent", "high");

            System.out.println("Find young customers who spent a lot:");

            customersCollection
                    .find(fand(youngHighSpent))
                    .projection(projectionAgeSpent)
                    .forEach((Consumer<? super Document>) (Document document) -> {
                        System.out.println(document.toJson());
                    });

            Document projectionAgeOrders = new Document();
            projectionAgeOrders.put("_id", 0);
            projectionAgeOrders.put("name", 1);
            projectionAgeOrders.put("age", 1);
            projectionAgeOrders.put("orders", 1);

            Map<String, String> oldOrFewOrders = new HashMap<>();
            oldOrFewOrders.put("age", "old");
            oldOrFewOrders.put("orders", "few");

            System.out.println("Find either old customers or those who've made a few orders:");

            customersCollection
                    .find(FuzzyLogicalOperators.fuzzyOr(oldOrFewOrders))
                    .projection(projectionAgeOrders)
                    .forEach((Consumer<? super Document>) (Document document) -> {
                        System.out.println(document.toJson());
                    });
        }
    }
}
