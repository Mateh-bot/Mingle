package org.mateh.mingle.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.mateh.mingle.Main;
import org.mateh.mingle.interfaces.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    private final Main main;
    private final List<Room> rooms;

    public RoomManager(Main main) {
        this.main = main;
        this.rooms = new ArrayList<>();
        loadRoomsFromConfig();
    }

    public void addRoom(Location corner1, Location corner2) {
        Room room = new Room(corner1, corner2);
        rooms.add(room);
        saveRoomToConfig(room);
    }

    public void setRoomDoor(Location doorLocation) {
        if (rooms.isEmpty()) {
            throw new IllegalStateException("No room has been defined yet.");
        }

        Room lastRoom = rooms.get(rooms.size() - 1);
        lastRoom.setDoor(doorLocation);
        saveRoomToConfig(lastRoom);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    private void loadRoomsFromConfig() {
        FileConfiguration config = main.getConfig();
        if (!config.isConfigurationSection("rooms")) return;

        for (String key : config.getConfigurationSection("rooms").getKeys(false)) {
            Location corner1 = getLocationFromConfig(config, "rooms." + key + ".corner1");
            Location corner2 = getLocationFromConfig(config, "rooms." + key + ".corner2");
            Location door = getLocationFromConfig(config, "rooms." + key + ".door");

            Room room = new Room(corner1, corner2);
            room.setDoor(door);
            rooms.add(room);
        }
    }

    private void saveRoomToConfig(Room room) {
        FileConfiguration config = main.getConfig();
        int roomIndex = rooms.indexOf(room);

        config.set("rooms." + roomIndex + ".corner1", locationToConfigString(room.getCorner1()));
        config.set("rooms." + roomIndex + ".corner2", locationToConfigString(room.getCorner2()));
        if (room.getDoor() != null) {
            config.set("rooms." + roomIndex + ".door", locationToConfigString(room.getDoor()));
        }

        main.saveConfig();
    }

    private Location getLocationFromConfig(FileConfiguration config, String path) {
        if (!config.isString(path)) return null;

        String[] parts = config.getString(path).split(",");
        World world = Bukkit.getWorld(parts[0]);
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);

        return new Location(world, x, y, z);
    }

    private String locationToConfigString(Location location) {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ();
    }
}
