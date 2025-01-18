package org.mateh.mingle.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {
    private final Set<UUID> alivePlayers = new HashSet<>();

    public void resetPlayers(Collection<? extends Player> players) {
        alivePlayers.clear();
        for (Player player : players) {
            alivePlayers.add(player.getUniqueId());
        }
    }

    public void eliminatePlayer(Player player) {
        alivePlayers.remove(player.getUniqueId());
        player.setHealth(0);
        Bukkit.broadcastMessage(player.getName() + " has been eliminated!");
    }

    public Set<UUID> getAlivePlayers() {
        return alivePlayers;
    }
}
