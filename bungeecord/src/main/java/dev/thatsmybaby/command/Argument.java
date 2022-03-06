package dev.thatsmybaby.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;

@AllArgsConstructor @ToString
public abstract class Argument {

    @Getter private final String name;
    @Getter private final String permission;
    @Getter private final String[] aliases;
    @Getter private final boolean async;

    public boolean equals(String name) {
        return this.name.equalsIgnoreCase(name) || (this.aliases != null && Arrays.stream(this.aliases).anyMatch(alias -> alias.equalsIgnoreCase(name)));
    }

    public abstract void execute(ProxiedPlayer proxiedPlayer, String commandLabel, String argumentLabel, String[] args);
}