package com.Address.demo.config;

import com.mongodb.client.MongoClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


@Component
public class MongoTemplateFactory {

    private final MongoClient mongoClient;
    private final DatabaseProperties databaseProperties;

    public MongoTemplateFactory(MongoClient mongoClient,
                                DatabaseProperties databaseProperties) {
        this.mongoClient = mongoClient;
        this.databaseProperties = databaseProperties;
    }

    // 🔹 Default DB (as is)
    public MongoTemplate getDefaultTemplate() {
        String dbName = databaseProperties.getActualDbName();
        if (dbName == null || dbName.isEmpty()) {
            throw new IllegalArgumentException("Default DB mapping is missing!");
        }
        return new MongoTemplate(mongoClient, dbName);
    }

    // 🔹 Dynamic DB
    public MongoTemplate getTemplateByKey(String dbKey) {

        String actualDb = databaseProperties.getDatabases().get(dbKey);

        if (actualDb == null) {
            throw new IllegalArgumentException("Invalid DB key: " + dbKey);
        }

        return new MongoTemplate(mongoClient, actualDb);
    }


    public MongoTemplate getTemplates() {

        String dbName = databaseProperties.getActualDbName();

        if (dbName == null || dbName.isEmpty()) {
            throw new IllegalArgumentException("Default DB mapping is missing or empty!");
        }

        return new MongoTemplate(mongoClient, dbName);
    }
}

