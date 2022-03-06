package dev.thatsmybaby.test;

import dev.thatsmybaby.BungeeClansLoader;
import dev.thatsmybaby.shared.MongoDBProvider;
import dev.thatsmybaby.shared.storage.PluginClanStorage;

import java.util.UUID;
import java.util.logging.Logger;

public final class BungeeTester {

    public BungeeTester() throws IllegalAccessException {
        Logger logger = BungeeClansLoader.getInstance().getLogger();

        UUID uniqueId = UUID.randomUUID();
        MongoDBProvider.getInstance().saveOrCreateStorage(new PluginClanStorage(uniqueId.toString(), "hola"));
        logger.warning("Saving or creating a new storage");

        PluginClanStorage storage = MongoDBProvider.getInstance().loadStorage(uniqueId);

        if (storage == null) {
            logger.warning("Clan Storage for '" + uniqueId + "' not found...");
        } else {
            logger.warning("Clan storage for '" + uniqueId + "' found!");
        }

        storage = MongoDBProvider.getInstance().loadStorage("hola");

        if (storage == null) {
            logger.warning("Clan Storage for 'hola' not found...");
        } else {
            logger.warning("Clan storage for 'hola' found!");
        }
    }
}