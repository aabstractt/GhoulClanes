package dev.thatsmybaby.shared;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.thatsmybaby.shared.storage.PluginClanStorage;
import lombok.Getter;
import org.bson.Document;

import java.lang.reflect.Field;
import java.util.UUID;

public final class MongoDBProvider {

    @Getter private final static MongoDBProvider instance = new MongoDBProvider();

    private MongoCollection<Document> collection = null;

    public void init(String uri, String dbname, String collectionName) {
        MongoClient mongoClient;

        if (uri == null || uri.equals("")) {
            mongoClient = new MongoClient();
        } else {
            mongoClient = new MongoClient(new MongoClientURI(uri));
        }

        this.collection = mongoClient.getDatabase(dbname).getCollection(collectionName);
    }

    public void saveOrCreateStorage(PluginClanStorage storage) throws IllegalAccessException {
        Document document = new Document();

        for (Field field : storage.getClass().getDeclaredFields()) {
            boolean accessible = field.isAccessible();

            if (!accessible) {
                field.setAccessible(true);
            }

            document.append(field.getName(), field.get(storage));

            if (!accessible) {
                field.setAccessible(false);
            }
        }

        if (this.collection.find(Filters.eq("uniqueId", storage.getUniqueId())).first() == null) {
            this.collection.insertOne(document);
        } else {
            this.collection.findOneAndReplace(Filters.eq("uniqueId", storage.getUniqueId()), document);
        }
    }

    public PluginClanStorage loadStorage(UUID uniqueId) {
        Document document = this.collection.find(Filters.eq("uniqueId", uniqueId.toString())).first();

        if (document == null) {
            return null;
        }

        return new Gson().fromJson(document.toJson(), PluginClanStorage.class);
    }

    public PluginClanStorage loadStorage(String name) {
        Document document = this.collection.find(Filters.eq("name", name)).first();

        if (document == null) {
            return null;
        }

        return new Gson().fromJson(document.toJson(), PluginClanStorage.class);
    }
}