package org.mateh.mingle.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class RoomManager {
    private final HashMap<Location, List<Player>> roomOccupants = new HashMap<>();
    private final List<Location> roomLocations = new ArrayList<>();

    public RoomManager() {
        setupRooms();
    }

    public void setupRooms() {
        roomLocations.add(new Location(Bukkit.getWorld("world"), 50, 53, 0));
        roomLocations.add(new Location(Bukkit.getWorld("world"), 50, 53, 6));
    }

    public void updateRoomOccupants(Collection<? extends Player> players) {
        roomOccupants.clear();
        for (Location room : roomLocations) {
            List<Player> playersInRoom = new ArrayList<>();
            for (Player player : players) {
                if (player.getLocation().distance(room) < 2.5) {
                    playersInRoom.add(player);
                }
            }
            roomOccupants.put(room, playersInRoom);
        }
    }

    public void checkRooms(int requiredGroupSize, PlayerManager playerManager) {
        for (Map.Entry<Location, List<Player>> entry : roomOccupants.entrySet()) {
            List<Player> playersInRoom = entry.getValue();

            if (playersInRoom.size() != requiredGroupSize) {
                for (Player player : playersInRoom) {
                    playerManager.eliminatePlayer(player);
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isInAnyRoom(player)) {
                playerManager.eliminatePlayer(player);
            }
        }
    }

    private boolean isInAnyRoom(Player player) {
        for (List<Player> players : roomOccupants.values()) {
            if (players.contains(player)) {
                return true;
            }
        }
        return false;
    }
}
