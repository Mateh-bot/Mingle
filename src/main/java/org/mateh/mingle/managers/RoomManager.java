package org.mateh.mingle.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.mateh.mingle.Main;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private final Main main;
    private final Map<String, Room> rooms = new HashMap<>();
    private Room lastRoom;

    public RoomManager(Main main) {
        this.main = main;
        loadRoomsFromConfig();
    }

    public void addRoom(Location corner1, Location corner2) {
        String roomName = "room_" + (rooms.size() + 1);
        Room room = new Room(roomName, corner1, corner2);
        rooms.put(roomName, room);
        lastRoom = room;
        saveRoomToConfig(room);
    }

    public void setLastRoomDoor(Location doorLocation) {
        if (lastRoom != null) {
            lastRoom.setDoorLocation(doorLocation);
            saveRoomToConfig(lastRoom);
        }
    }

    private void loadRoomsFromConfig() {
        FileConfiguration config = main.getConfig();

        if (!config.contains("rooms")) {
            return;
        }

        for (String key : config.getConfigurationSection("rooms").getKeys(false)) {
            String path = "rooms." + key;
            Location corner1 = deserializeLocation(config.getString(path + ".corner1"));
            Location corner2 = deserializeLocation(config.getString(path + ".corner2"));
            Location door = deserializeLocation(config.getString(path + ".door"));

            if (corner1 != null && corner2 != null) {
                Room room = new Room(key, corner1, corner2);
                if (door != null) {
                    room.setDoorLocation(door);
                }
                rooms.put(key, room);
            }
        }
    }

    private void saveRoomToConfig(Room room) {
        String path = "rooms." + room.getName();
        main.getConfig().set(path + ".corner1", serializeLocation(room.getCorner1()));
        main.getConfig().set(path + ".corner2", serializeLocation(room.getCorner2()));
        if (room.getDoorLocation() != null) {
            main.getConfig().set(path + ".door", serializeLocation(room.getDoorLocation()));
        }
        main.saveConfig();
    }

    private String serializeLocation(Location location) {
        if (location == null) {
            return null;
        }
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    private Location deserializeLocation(String serialized) {
        if (serialized == null) {
            return null;
        }

        try {
            String[] parts = serialized.split(",");
            return new Location(
                    Bukkit.getWorld(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3])
            );
        } catch (Exception e) {
            main.getLogger().warning("Failed to deserialize location: " + serialized);
            return null;
        }
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public void lockAllDoors() {
        for (Room room : rooms.values()) {
            room.lockDoor();
        }
    }

    public void unlockAllDoors() {
        for (Room room : rooms.values()) {
            room.unlockDoor();
        }
    }
}
