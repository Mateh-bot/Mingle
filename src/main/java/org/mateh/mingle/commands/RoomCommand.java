package org.mateh.mingle.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mateh.mingle.managers.RoomManager;

public class RoomCommand implements CommandExecutor {
    private final RoomManager roomManager;

    public RoomCommand(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("set") && args.length == 7) {
                try {
                    double x1 = Double.parseDouble(args[1]);
                    double y1 = Double.parseDouble(args[2]);
                    double z1 = Double.parseDouble(args[3]);
                    double x2 = Double.parseDouble(args[4]);
                    double y2 = Double.parseDouble(args[5]);
                    double z2 = Double.parseDouble(args[6]);

                    roomManager.addRoom(new Location(player.getWorld(), x1, y1, z1), new Location(player.getWorld(), x2, y2, z2));
                    sender.sendMessage("Room has been set successfully!");
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("Coordinates must be valid numbers.");
                    return false;
                }
            }
            else if (args[0].equalsIgnoreCase("door") && args.length == 4) {
                try {
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);

                    roomManager.setLastRoomDoor(new Location(player.getWorld(), x, y, z));
                    sender.sendMessage("Door has been set successfully!");
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("Coordinates must be valid numbers.");
                    return false;
                }
            }
        }

        sender.sendMessage("Usage: /room set <x1> <y1> <z1> <x2> <y2> <z2> or /room set door <x> <y> <z>");
        return false;
    }
}
