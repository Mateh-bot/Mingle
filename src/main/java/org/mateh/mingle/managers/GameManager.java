package org.mateh.mingle.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.mingle.Main;

import java.util.*;

public class GameManager {

    private final Main main;
    private final World world;
    private final Location platformCenter;
    private final double platformRadius;
    private final int minPlayersToEnd;
    private final RoomManager roomManager;
    private final PlayerRotationManager rotationManager;
    private boolean gameActive = false;

    public GameManager(Main main, World world, Location platformCenter, double platformRadius) {
        this.main = main;
        this.world = world;
        this.platformCenter = platformCenter;
        this.platformRadius = platformRadius;
        this.minPlayersToEnd = main.getConfig().getInt("game.minPlayersToEnd");
        this.roomManager = new RoomManager(main);
        this.rotationManager = new PlayerRotationManager(main, world,
                main.getConfig().getDouble("platform.centerX"),
                main.getConfig().getDouble("platform.centerY"),
                main.getConfig().getDouble("platform.centerZ"),
                main.getConfig().getDouble("platform.radius"));
    }

    public void startGame() {
        if (gameActive) {
            main.getLogger().info("The game is already active.");
            return;
        }

        gameActive = true;
        Bukkit.broadcastMessage("Game starting in 10 seconds...");

        new BukkitRunnable() {
            private int countdown = 10;

            @Override
            public void run() {
                if (countdown > 0) {
                    Bukkit.broadcastMessage("Starting in " + countdown + " seconds...");
                    countdown--;
                } else {
                    this.cancel();
                    startRound();
                }
            }
        }.runTaskTimer(main, 0L, 20L);
    }

    private void startRound() {
        rotationManager.startPlayerRotation();

        new BukkitRunnable() {
            @Override
            public void run() {
                rotationManager.stopPlayerRotation();
                assignGroups();
            }
        }.runTaskLater(main, 10 * 20L); // 10 seconds
    }

    private void assignGroups() {
        int requiredGroupSize = new Random().nextInt(1) + 1;
        Bukkit.broadcastMessage("Form groups of " + requiredGroupSize + " players and enter the rooms! You have 30 seconds.");

        new BukkitRunnable() {
            @Override
            public void run() {
                evaluateGroups(requiredGroupSize);
            }
        }.runTaskLater(main, 30 * 20L); // 30 seconds
    }

    private void evaluateGroups(int requiredGroupSize) {
        roomManager.lockAllDoors();
        Set<Player> playersToEliminate = new HashSet<>();

        for (Room room : roomManager.getRooms().values()) {
            List<Player> playersInRoom = new ArrayList<>();
            for (Player player : world.getPlayers()) {
                if (room.isPlayerInside(player.getLocation())) {
                    playersInRoom.add(player);
                }
            }

            if (playersInRoom.size() != requiredGroupSize) {
                playersToEliminate.addAll(playersInRoom);
            }
        }

        for (Player player : world.getPlayers()) {
            boolean inAnyRoom = roomManager.getRooms().values().stream().anyMatch(room -> room.isPlayerInside(player.getLocation()));
            if (!inAnyRoom) {
                playersToEliminate.add(player);
            }
        }

        for (Player player : playersToEliminate) {
            player.setHealth(0.0);
            Bukkit.broadcastMessage(player.getName() + " has been eliminated!");
        }

        int alivePlayers = (int) world.getPlayers().stream().filter(player -> player.getHealth() > 0.0).count();

        if (alivePlayers <= minPlayersToEnd) {
            Bukkit.broadcastMessage("Game over! " + alivePlayers + " player(s) remain.");
            gameActive = false;
        } else {
            Bukkit.broadcastMessage("Starting the next round...");
            startRound();
        }
        roomManager.unlockAllDoors();
    }
}