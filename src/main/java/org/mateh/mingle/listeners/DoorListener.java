package org.mateh.mingle.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mateh.mingle.managers.Room;
import org.mateh.mingle.managers.RoomManager;

public class DoorListener implements Listener {
    private final RoomManager roomManager;

    public DoorListener(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @EventHandler
    public void onDoorInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock() == null ||
                event.getClickedBlock().getType() != Material.OAK_DOOR ||
                event.getClickedBlock().getType() != Material.BIRCH_DOOR ||
                event.getClickedBlock().getType() != Material.SPRUCE_DOOR ||
                event.getClickedBlock().getType() != Material.JUNGLE_DOOR ||
                event.getClickedBlock().getType() != Material.ACACIA_DOOR ||
                event.getClickedBlock().getType() != Material.DARK_OAK_DOOR ||
                event.getClickedBlock().getType() != Material.CRIMSON_DOOR ||
                event.getClickedBlock().getType() != Material.WARPED_DOOR ||
                event.getClickedBlock().getType() != Material.BAMBOO_DOOR ||
                event.getClickedBlock().getType() != Material.COPPER_DOOR ||
                event.getClickedBlock().getType() != Material.CHERRY_DOOR ||
                event.getClickedBlock().getType() != Material.MANGROVE_DOOR) return;

        Location doorLocation = event.getClickedBlock().getLocation();

        for (Room room : roomManager.getRooms().values()) {
            if (room.getDoorLocation().equals(doorLocation) && room.isDoorLocked()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("This door is locked!");
                return;
            }
        }
    }
}
