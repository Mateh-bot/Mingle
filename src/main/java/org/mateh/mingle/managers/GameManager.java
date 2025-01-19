package org.mateh.mingle.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mateh.mingle.Main;

import java.util.*;

public class GameManager{

    private final Main main;
    private final World world;
    private final Location platformCenter;
    private final double platformRadius;
    private int minPlayersToEnd;
    private final PlayerRotationManager rotationManager;
    private boolean gameActive = false;

    public GameManager(Main main, World world, Location platformCenter, double platformRadius, int minPlayersToEnd) {
        this.main = main;
        this.world = world;
        this.platformCenter = platformCenter;
        this.platformRadius = platformRadius;
        this.minPlayersToEnd = minPlayersToEnd;
        this.rotationManager = new PlayerRotationManager(main, world, platformCenter.getX(), platformCenter.getZ(), platformRadius);
    }

    public void startGame() {
        if (gameActive) {
            main.getLogger().info("The game is already active.");
            return;
        }

        gameActive = true;
        main.getLogger().info("Game starting...");

        new BukkitRunnable() {
            private int countdown = 10;

            @Override
            public void run() {
                if (countdown > 0) {
                    Bukkit.broadcastMessage("The game starts in " + countdown + " seconds!");
                    countdown--;
                } else {
                    startRound();
                    this.cancel();
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
                assignRooms();
            }
        }.runTaskLater(main, 10 * 20L); // 10 seconds of rotation
    }

    private void assignRooms() {
        int requiredGroupSize = new Random().nextInt(5) + 1;
        Bukkit.broadcastMessage("Form groups of size " + requiredGroupSize + " and enter the rooms! You have 30 seconds.");

        new BukkitRunnable() {
            @Override
            public void run() {
                evaluateRooms(requiredGroupSize);
            }
        }.runTaskLater(main, 30 * 20L); // 30 seconds to form groups
    }

    private void evaluateRooms(int requiredGroupSize) {
        Map<Location, List<Player>> roomOccupants = getRoomOccupants();
        Set<Player> playersToEliminate = new HashSet<>();

        for (Map.Entry<Location, List<Player>> entry : roomOccupants.entrySet()) {
            List<Player> playersInRoom = entry.getValue();
            if (playersInRoom.size() != requiredGroupSize) {
                playersToEliminate.addAll(playersInRoom);
            }
        }

        for (Player player : world.getPlayers()) {
            if (!isPlayerInAnyRoom(player)) {
                playersToEliminate.add(player);
            }
        }

        for (Player player : playersToEliminate) {
            player.setHealth(0.0);
        }

        int alivePlayers = (int) world.getPlayers().stream().filter(player -> player.getHealth() > 0.0).count();

        if (alivePlayers <= minPlayersToEnd) {
            Bukkit.broadcastMessage("Game over! " + alivePlayers + " player(s) remain.");
            gameActive = false;
        } else {
            Bukkit.broadcastMessage("Starting the next round...");
            startRound();
        }
    }

    private Map<Location, List<Player>> getRoomOccupants() {
        Map<Location, List<Player>> roomOccupants = new HashMap<>();

        // Example logic: Replace with actual room locations and detection
        for (Player player : world.getPlayers()) {
            Location roomLocation = detectRoom(player);
            if (roomLocation != null) {
                roomOccupants.computeIfAbsent(roomLocation, k -> new ArrayList<>()).add(player);
            }
        }

        return roomOccupants;
    }

    private boolean isPlayerInAnyRoom(Player player) {
        return detectRoom(player) != null;
    }

    private Location detectRoom(Player player) {
        // Example detection logic: Replace with actual room boundaries
        Location location = player.getLocation();
        if (location.getX() < 50 && location.getZ() < 50) {
            return new Location(world, 25, location.getY(), 25); // Example room location
        }
        return null;
    }

    public void reloadConfig(FileConfiguration config) {
        this.minPlayersToEnd = config.getInt("minPlayersToEnd", 1);
    }
}