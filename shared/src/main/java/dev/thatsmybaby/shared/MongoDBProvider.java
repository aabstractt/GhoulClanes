package dev.thatsmybaby.shared;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dev.thatsmybaby.shared.storage.PluginClanStorage;
import lombok.Getter;
import org.bson.Document;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Filter;

public final class MongoDBProvider {

    @Getter private final static MongoDBProvider instance = new MongoDBProvider();

    private final Gson mapper = new Gson();

    private MongoCollection<Document> collection = null;
    private MongoCollection<Document> playersCollection = null;

    public void init(String uri, String dbname, String collectionName) {
        MongoClient mongoClient;

        if (uri == null || uri.equals("")) {
            mongoClient = new MongoClient();
        } else {
            mongoClient = new MongoClient(new MongoClientURI(uri));
        }

        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbname);

        this.collection = mongoDatabase.getCollection(collectionName);
        this.playersCollection = mongoDatabase.getCollection("players");
    }

    public void createOrSaveStorage(PluginClanStorage storage) throws IllegalAccessException {
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

        return this.mapper.fromJson(document.toJson(), PluginClanStorage.class);
    }

    public PluginClanStorage loadStorage(String name) {
        Document document = this.collection.find(Filters.eq("name", name)).first();

        if (document == null) {
            return null;
        }

        return this.mapper.fromJson(document.toJson(), PluginClanStorage.class);
    }

    public void createOrSave(String name, UUID uniqueId) {
        this.createOrSave(name, uniqueId, null, new ArrayList<>());
    }

    public void createOrSave(UUID uniqueId, UUID clanUniqueId) {
        this.createOrSave(null, uniqueId, clanUniqueId, new ArrayList<>());
    }

    public void createOrSave(UUID uniqueId, List<String> pendingInvites) {
        this.createOrSave(null, uniqueId, null, pendingInvites);
    }

    public void createOrSave(String name, UUID uniqueId, UUID clanUniqueId, List<String> pendingInvites) {
        Document document = this.playersCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();

        if (document == null) {
            this.playersCollection.insertOne(new Document("uniqueId", uniqueId.toString()).append("name", name));

            return;
        }

        if (clanUniqueId != null) document.put("clanUniqueId", clanUniqueId.toString());

        if (!pendingInvites.isEmpty()) document.put("pendingInvites", pendingInvites);

        if (name != null) document.put("name", name);

        this.playersCollection.findOneAndReplace(Filters.eq("uniqueId", uniqueId.toString()), document);
    }

    public PluginClanStorage getPlayerClan(UUID uniqueId) {
        Document document = this.playersCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();

        if (document == null) {
            return null;
        }

        String clanUniqueId = document.getString("clanUniqueId");

        return clanUniqueId != null ? this.loadStorage(UUID.fromString(clanUniqueId)) : null;
    }

    public UUID getTargetPlayer(String name) {
        Document document = this.playersCollection.find(Filters.eq("name", name)).first();

        if (document == null) {
            return null;
        }

        String uniqueId = document.getString("uniqueId");

        return uniqueId != null ? UUID.fromString(uniqueId) : null;
    }

    public String getTargetPlayer(UUID uniqueId) {
        Document document = this.playersCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();

        if (document == null) {
            return null;
        }

        return document.getString("name");
    }

    @SuppressWarnings("unchecked")
    public List<String> getTargetPendingInvites(UUID uniqueId) {
        Document document = this.playersCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();

        if (document == null) {
            return new ArrayList<>();
        }

        return (List<String>) document.getOrDefault("pendingInvites", new ArrayList<>());
    }
}