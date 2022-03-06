package dev.thatsmybaby.shared.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @ToString
public final class PluginClanStorage {

    @Getter @Setter private String name;
    @Getter @Setter private String ownerUniqueId;
    @Getter private String uniqueId;
    @Getter private int points;

    @Getter private final Set<String> membersUniqueId = new HashSet<>();

    public void increasePoints(int increase) {
        this.points += increase;
    }

    public void decreasePoints(int decrease) {
        this.points -= decrease;
    }

    public void addMember(UUID uniqueId) {
        this.membersUniqueId.add(uniqueId.toString());
    }

    public void removeMember(UUID uniqueId) {
        this.membersUniqueId.remove(uniqueId.toString());
    }

    public boolean isMember(UUID uniqueId) {
        return this.isMember(uniqueId.toString());
    }

    public boolean isMember(String uniqueId) {
        return this.membersUniqueId.contains(uniqueId);
    }
}