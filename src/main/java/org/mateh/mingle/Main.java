package org.mateh.mingle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mateh.mingle.managers.GameManager;

public final class Main extends JavaPlugin {

    private GameManager gameManager;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        World world = Bukkit.getWorld(config.getString("platform.world"));
        double centerX = config.getDouble("platform.centerX");
        double centerZ = config.getDouble("platform.centerZ");
        double radius = config.getDouble("platform.radius");
        int minPlayersToEnd = config.getInt("minPlayersToEnd");

        Location platformCenter = new Location(world, centerX, world.getHighestBlockYAt((int) centerX, (int) centerZ), centerZ);
        gameManager = new GameManager(this, world, platformCenter, radius, minPlayersToEnd);

        getLogger().info("Mingle plugin enabled!");
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mingle")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                config = getConfig();
                gameManager.reloadConfig(config);
                sender.sendMessage("Configuration reloaded.");
                return true;
            } else if (args.length == 0) {
                gameManager.startGame();
                return true;
            }
        }
        return false;
    }
}
