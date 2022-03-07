package dev.thatsmybaby;

import com.google.common.io.ByteStreams;
import dev.thatsmybaby.command.BungeeClanCommand;
import dev.thatsmybaby.listener.PostLoginListener;
import dev.thatsmybaby.shared.MongoDBProvider;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class BungeeClansLoader extends Plugin {

    @Getter
    private static BungeeClansLoader instance;

    @Override @SuppressWarnings("ResultOfMethodCallIgnored") @SneakyThrows
    public void onEnable() {
        instance = this;

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        this.saveResource("config.yml");
        this.saveResource("messages.yml");

        Configuration section = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml")).getSection("mongodb");

        MongoDBProvider.getInstance().init(section.getString("uri"), section.getString("dbname"), section.getString("collection"), this.getLogger());

        this.getProxy().getPluginManager().registerListener(this, new PostLoginListener());

        this.getProxy().getPluginManager().registerCommand(this, new BungeeClanCommand("clan"));
    }

    @SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
    private void saveResource(String resource) {
        File file = new File(this.getDataFolder(), resource);

        if (file.exists()) {
            return;
        }

        try {
            file.createNewFile();

            try (InputStream is = this.getResourceAsStream(resource); OutputStream os = new FileOutputStream(file)) {
                ByteStreams.copy(is, os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean released() {
        return false;
    }
}