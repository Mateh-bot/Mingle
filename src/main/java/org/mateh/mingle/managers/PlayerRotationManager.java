package org.mateh.mingle.managers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.mateh.mingle.Main;

import java.util.HashSet;
import java.util.Set;

public class PlayerRotationManager {
    private final Main main;
    private final World world;
    private final double centerX;
    private final double centerY;
    private final double centerZ;
    private final double radius;
    private final double rotationSpeed = 0.5;
    private BukkitRunnable rotationTask;
    private final Set<Player> rotatingPlayers = new HashSet<>();

    public PlayerRotationManager(Main main, World world, double centerX, double centerY, double centerZ, double radius) {
        this.main = main;
        this.world = world;
        this.centerX = main.getConfig().getDouble("platform.centerX");
        this.centerY = main.getConfig().getDouble("platform.centerY");
        this.centerZ = main.getConfig().getDouble("platform.centerZ");
        this.radius = main.getConfig().getDouble("platform.radius");
    }

    public void startPlayerRotation() {
        stopPlayerRotation();
        rotatingPlayers.addAll(getPlayersOnPlatform());

        rotationTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : rotatingPlayers) {
                    rotatePlayer(player);
                }
            }
        };
        rotationTask.runTaskTimer(main, 0L, 1L);
    }

    public void stopPlayerRotation() {
        if (rotationTask != null) {
            rotationTask.cancel();
            rotationTask = null;
        }
        rotatingPlayers.clear();
    }

    private void rotatePlayer(Player player) {
        Location currentLocation = player.getLocation();

        double dx = currentLocation.getX() - centerX;
        double dz = currentLocation.getZ() - centerZ;

        Vector tangent = new Vector(-dz, 0, dx).normalize();

        Vector movement = tangent.multiply(rotationSpeed);

        player.setVelocity(movement);
    }

    private Set<Player> getPlayersOnPlatform() {
        Set<Player> players = new HashSet<>();
        for (Player player : world.getPlayers()) {
            Location loc = player.getLocation();
            double dx = loc.getX() - centerX;
            double dz = loc.getZ() - centerZ;
            double dy = Math.abs(loc.getY() - main.getConfig().getDouble("platform.centerY"));
            if (dx * dx + dz * dz <= radius * radius && dy <= 2) {
                players.add(player);
            }
        }
        return players;
    }
}
