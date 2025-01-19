package org.mateh.mingle.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.mateh.mingle.Main;

import java.util.HashSet;
import java.util.Set;

public class PlayerRotationManager {
    private final Main main;
    private World world;
    private double centerX;
    private double centerZ;
    private double platformRadius;
    private double rotationSpeed;

    private BukkitTask rotationTask;
    private final Set<Player> rotatingPlayers = new HashSet<>();

    public PlayerRotationManager(Main main, World world, double centerX, double centerZ, double platformRadius) {
        this.main = main;
        this.world = world;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.platformRadius = platformRadius;
        this.rotationSpeed = main.getConfig().getDouble("rotationSpeed");
    }

    public void updatePlatformData(World world, double centerX, double centerZ, double platformRadius) {
        this.world = world;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.platformRadius = platformRadius;
    }

    public void startPlayerRotation() {
        rotationTask = Bukkit.getScheduler().runTaskTimer(main, () -> {
            for (Player player : world.getPlayers()) {
                if (isPlayerOnPlatform(player)) {
                    rotatePlayer(player);
                }
            }
        }, 0L, 1L);
    }

    public void stopPlayerRotation() {
        if (rotationTask != null) {
            rotationTask.cancel();
        }
    }

    private boolean isPlayerOnPlatform(Player player) {
        Location location = player.getLocation();
        if (!location.getWorld().equals(world)) return false;

        double dx = location.getX() - centerX;
        double dz = location.getZ() - centerZ;

        return dx * dx + dz * dz <= platformRadius * platformRadius;
    }

    private void rotatePlayer(Player player) {
        Location currentLocation = player.getLocation();

        double dx = currentLocation.getX() - centerX;
        double dz = currentLocation.getZ() - centerZ;

        Vector tangent = new Vector(-dz, 0, dx).normalize();

        Vector movement = tangent.multiply(rotationSpeed);

        player.setVelocity(movement);
    }
}
