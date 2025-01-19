package org.mateh.mingle.managers;

import org.bukkit.Location;

public class Room {
    private final String name;
    private final Location corner1;
    private final Location corner2;
    private Location doorLocation;
    private boolean isDoorLocked;

    public Room(String name, Location corner1, Location corner2) {
        this.name = name;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.isDoorLocked = false;
    }

    public String getName() {
        return name;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public Location getDoorLocation() {
        return doorLocation;
    }

    public void setDoorLocation(Location doorLocation) {
        this.doorLocation = doorLocation;
    }

    public boolean isPlayerInside(Location playerLocation) {
        double x1 = Math.min(corner1.getX(), corner2.getX());
        double x2 = Math.max(corner1.getX(), corner2.getX());
        double y1 = Math.min(corner1.getY(), corner2.getY());
        double y2 = Math.max(corner1.getY(), corner2.getY());
        double z1 = Math.min(corner1.getZ(), corner2.getZ());
        double z2 = Math.max(corner1.getZ(), corner2.getZ());

        double px = playerLocation.getX();
        double py = playerLocation.getY();
        double pz = playerLocation.getZ();

        return px >= x1 && px <= x2 && py >= y1 && py <= y2 && pz >= z1 && pz <= z2;
    }

    public void lockDoor() {
        this.isDoorLocked = true;
    }

    public void unlockDoor() {
        this.isDoorLocked = false;
    }

    public boolean isDoorLocked() {
        return isDoorLocked;
    }


}
