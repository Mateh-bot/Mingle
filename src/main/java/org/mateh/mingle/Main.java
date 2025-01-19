package org.mateh.mingle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.mateh.mingle.commands.MingleCommand;
import org.mateh.mingle.commands.RoomCommand;
import org.mateh.mingle.listeners.DoorListener;
import org.mateh.mingle.managers.GameManager;
import org.mateh.mingle.managers.RoomManager;

public final class Main extends JavaPlugin {

    private GameManager gameManager;
    private RoomManager roomManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.roomManager = new RoomManager(this);
        Location platformCenter = new Location(Bukkit.getWorld("world"),
                getConfig().getDouble("platform.centerX"),
                getConfig().getDouble("platform.centerY"),
                getConfig().getDouble("platform.centerZ"));
        double platformRadius = getConfig().getDouble("platform.radius");
        this.gameManager = new GameManager(this, Bukkit.getWorld("world"), platformCenter, platformRadius);

        getCommand("mingle").setExecutor(new MingleCommand(gameManager));
        getCommand("room").setExecutor(new RoomCommand(roomManager));

        getServer().getPluginManager().registerEvents(new DoorListener(roomManager), this);
    }

    @Override
    public void onDisable() {
    }
}
