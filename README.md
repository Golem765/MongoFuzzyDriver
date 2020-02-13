# Fuzzy driver for MongoDB
Provides fuzzy operations that are compatible with bson mongoDB driver for Java. 

For more on fuzzy logic just google it.
## Examples
#### Create fuzzy indices
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

#### Find young customers
    customersCollection
        .find(fin("age", "young"))
        .sort(new Document("_fuzzy_age.young", 1))
        .sort(new Document("age", 1));

#### Find customers approximately 26 years old
    customersCollection
            .find(feq("age", 26.0));
            
#### Find young customers who spent a lot

    Map<String, String> youngHighSpent = new HashMap<>();
    youngHighSpent.put("age", "young");
    youngHighSpent.put("spent", "high");

    customersCollection
            .find(fand(youngHighSpent));
#### Find either old customers or those who've made a few orders

    Map<String, String> oldOrFewOrders = new HashMap<>();
    oldOrFewOrders.put("age", "old");
    oldOrFewOrders.put("orders", "few");

    customersCollection
            .find(FuzzyLogicalOperators.fuzzyOr(oldOrFewOrders));
