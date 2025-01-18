package org.mateh.mingle.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mateh.mingle.Main;

import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class GameManager implements CommandExecutor {
    private final Main main;
    private final RoomManager roomManager;
    private final PlayerManager playerManager;
    private final PlatformManager platformManager;
    private boolean gameRunning = false;
    private int randomGroupSize;

    public GameManager(Main main) {
        this.main = main;
        this.roomManager = new RoomManager();
        this.playerManager = new PlayerManager();
        this.platformManager = new PlatformManager(main);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mingle")) {
            if (gameRunning) {
                sender.sendMessage("A game is already running!");
                return true;
            }

            startGame();
            return true;
        }
        return false;
    }

    public void startGame() {
        gameRunning = true;
        playerManager.resetPlayers(Bukkit.getOnlinePlayers());
        platformManager.createPlatform();

        Bukkit.broadcastMessage("Game starts in 10 seconds!");

        Bukkit.getScheduler().runTaskLater(main, platformManager::replaceWithBarrierAndStartRotation, 200L);

        Bukkit.getScheduler().runTaskLater(main, this::handleRotationEnd, 400L);
    }

    private void handleRotationEnd() {
        platformManager.stopRotation();
        platformManager.restorePlatform();

        startRoomPhase();
    }

    private void startRoomPhase() {
        randomGroupSize = new Random().nextInt(1) + 1;
        Bukkit.broadcastMessage("The required group size is: " + randomGroupSize);
        Bukkit.broadcastMessage("You have 30 seconds to enter a room with the correct group size!");

        Bukkit.getScheduler().runTaskLater(main, () -> {
            roomManager.updateRoomOccupants(Bukkit.getOnlinePlayers());
            roomManager.checkRooms(randomGroupSize, playerManager);

            if (playerManager.getAlivePlayers().size() <= randomGroupSize) {
                endGame();
            } else {
                startGame();
            }
        }, 600L);
    }

    private void endGame() {
        gameRunning = false;

        Player winner = playerManager.getAlivePlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (winner != null) {
            Bukkit.broadcastMessage(winner.getName() + " is the winner!");
        } else {
            Bukkit.broadcastMessage("No winners this round!");
        }

        resetGame();
    }

    private void resetGame() {
        playerManager.resetPlayers(Collections.emptySet());
    }
}
