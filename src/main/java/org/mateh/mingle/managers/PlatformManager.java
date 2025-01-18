package org.mateh.mingle.managers;

import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;
import org.mateh.mingle.Main;

import java.util.*;

public class PlatformManager {
    private final Main main;
    private final Location center = new Location(Bukkit.getWorld("world"), 8, 53, 3);
    private final int radius = 21;
    private BukkitTask rotationTask;
    private final List<BlockDisplay> platformDisplays = new ArrayList<>();
    private final Set<Location> platformBlocks = new HashSet<>();
    private WorldBorder border;

    public PlatformManager(Main main) {
        this.main = main;
    }

    public void createPlatform() {
        World world = center.getWorld();
        if (world == null) return;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Location blockLocation = center.clone().add(x, 0, z);
                double distanceSquared = x * x + z * z;

                if (distanceSquared <= radius * radius) {
                    platformBlocks.add(blockLocation);

                    Material blockType = (distanceSquared > (radius - 1) * (radius - 1))
                            ? Material.NETHER_BRICK_SLAB
                            : Material.RED_CONCRETE;

                    world.getBlockAt(blockLocation).setType(blockType);
                }
            }
        }
    }

    public void replaceWithBarrierAndStartRotation() {
        World world = center.getWorld();
        if (world == null) return;

        for (Location loc : platformBlocks) {
            world.getBlockAt(loc).setType(Material.BARRIER);
        }

        createWorldBorder();
        spawnDisplayPlatform();
        startRotation();
    }

    public void stopRotation() {
        stopRotationTask();
        removeDisplayPlatform();
        removeWorldBorder();
    }

    public void restorePlatform() {
        World world = center.getWorld();
        if (world == null) return;

        for (Location loc : platformBlocks) {
            Material blockType = loc.distanceSquared(center) > (radius - 1) * (radius - 1)
                    ? Material.NETHER_BRICK_SLAB
                    : Material.RED_CONCRETE;

            world.getBlockAt(loc).setType(blockType);
        }
    }

    private void createWorldBorder() {
        World world = center.getWorld();
        if (world == null) return;

        border = world.getWorldBorder();
        border.setCenter(center);
        border.setSize(radius * 2 + 2);
        border.setWarningDistance(0);
    }

    private void removeWorldBorder() {
        if (border != null) {
            border.reset();
        }
    }

    /*private void spawnDisplayPlatform() {
        World world = center.getWorld();
        if (world == null) return;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distanceSquared = x * x + z * z;

                if (distanceSquared <= radius * radius) {
                    Location blockLocation = center.clone().add(x, 0, z);

                    Material blockType = (distanceSquared > (radius - 1) * (radius - 1))
                            ? Material.NETHER_BRICK_SLAB
                            : Material.RED_CONCRETE;

                    BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(blockLocation, EntityType.BLOCK_DISPLAY);
                    blockDisplay.setBlock(blockType.createBlockData());
                    platformDisplays.add(blockDisplay);
                }
            }
        }
    }

    private void startRotation() {
        rotationTask = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
            private float angle = 0;

            @Override
            public void run() {
                angle += 5;
                if (angle >= 360) angle = 0;

                for (BlockDisplay blockDisplay : platformDisplays) {
                    Location relative = blockDisplay.getLocation().clone().subtract(center);
                    double x = relative.getX();
                    double z = relative.getZ();

                    double rotatedX = x * Math.cos(Math.toRadians(5)) - z * Math.sin(Math.toRadians(5));
                    double rotatedZ = x * Math.sin(Math.toRadians(5)) + z * Math.cos(Math.toRadians(5));

                    Location newLocation = center.clone().add(rotatedX, 0, rotatedZ);
                    blockDisplay.teleport(newLocation);
                }
            }
        }, 0L, 2L);
    }*/

    public void spawnDisplayPlatform() {
        World world = center.getWorld();
        if (world == null) return;

        double density = 0.5;

        for (double x = -radius; x <= radius; x += density) {
            for (double z = -radius; z <= radius; z += density) {
                double distanceSquared = x * x + z * z;

                if (distanceSquared <= radius * radius) {
                    Location blockLocation = center.clone().add(x, 0, z);

                    Material blockType = (distanceSquared > (radius - 1) * (radius - 1))
                            ? Material.NETHER_BRICK_SLAB
                            : Material.RED_CONCRETE;

                    BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(blockLocation, EntityType.BLOCK_DISPLAY);
                    blockDisplay.setBlock(blockType.createBlockData());
                    platformDisplays.add(blockDisplay);
                }
            }
        }
    }

    public void startRotation() {
        rotationTask = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
            private float angle = 0;

            @Override
            public void run() {
                angle += 5;
                if (angle >= 360) angle = 0;

                for (BlockDisplay blockDisplay : platformDisplays) {
                    Location relative = blockDisplay.getLocation().clone().subtract(center);
                    double x = relative.getX();
                    double z = relative.getZ();

                    double rotatedX = x * Math.cos(Math.toRadians(5)) - z * Math.sin(Math.toRadians(5));
                    double rotatedZ = x * Math.sin(Math.toRadians(5)) + z * Math.cos(Math.toRadians(5));

                    Location newLocation = center.clone().add(rotatedX, 0, rotatedZ);
                    blockDisplay.teleport(newLocation);
                }
            }
        }, 0L, 2L);
    }


    private void stopRotationTask() {
        if (rotationTask != null) {
            rotationTask.cancel();
        }
    }

    private void removeDisplayPlatform() {
        for (BlockDisplay blockDisplay : platformDisplays) {
            blockDisplay.remove();
        }
        platformDisplays.clear();
    }
}
