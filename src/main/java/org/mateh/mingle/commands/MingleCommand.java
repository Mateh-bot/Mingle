package org.mateh.mingle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.mateh.mingle.managers.GameManager;

public class MingleCommand implements CommandExecutor {
    private final GameManager gameManager;

    public MingleCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mingle.start")) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            gameManager.startGame();
            return true;
        }
        return false;
    }
}
