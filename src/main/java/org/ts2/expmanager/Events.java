package org.ts2.expmanager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class Events implements Listener {
    public static boolean checkAvailability(Player player) {
        int playerExpLevel = player.getLevel();
        int maxLevel = EXPManager.instance.getConfig().getInt("players." + player.getName());

        if (!EXPManager.instance.getConfig().getBoolean("enable-limitation")) {
            return true;
        }

        if (!(playerExpLevel < maxLevel)) {
            return false;
        }

        return true;
    }

    public static void fixPlayerLvl(Player player) {
        if (EXPManager.instance.getConfig().getBoolean("enable-limitation")) {
            int playerExpLevel = player.getLevel();
            int maxLevel = EXPManager.instance.getConfig().getInt("players." + player.getName());

            if (!(playerExpLevel <= maxLevel)) {
                player.setLevel(maxLevel);
            }
        }
    }

    @EventHandler
    public void onEXPChange(PlayerExpChangeEvent event) {
        if (!checkAvailability(event.getPlayer())) {
            int maxLevel = EXPManager.instance.getConfig().getInt("players." + event.getPlayer().getName());

            event.setAmount(0);
            event.getPlayer().setExp(0.0f);
            event.getPlayer().setLevel(maxLevel);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) {
            ItemStack itemStack = event.getCurrentItem();

            if (itemStack.getType().equals(Material.AIR)) {
                itemStack = event.getCursor();
            }
            if (itemStack.getType().equals(Material.EXPERIENCE_BOTTLE)) {
                if (event.getAction().equals(InventoryAction.DROP_ALL_SLOT)  || event.getAction().equals(InventoryAction.DROP_ALL_CURSOR)  || event.getAction().equals(InventoryAction.DROP_ONE_CURSOR)  || event.getAction().equals(InventoryAction.DROP_ONE_SLOT)) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getWhoClicked().getGameMode().equals(GameMode.SPECTATOR)) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getAction().equals(InventoryAction.PLACE_ALL)  || event.getAction().equals(InventoryAction.PLACE_ONE)  || event.getAction().equals(InventoryAction.PLACE_SOME)  || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getAction().equals(InventoryAction.HOTBAR_SWAP) || event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)) {
                    if ((event.getAction().equals(InventoryAction.PLACE_ALL)  || event.getAction().equals(InventoryAction.PLACE_ONE)  || event.getAction().equals(InventoryAction.PLACE_SOME)) && event.getClickedInventory().getType().equals(InventoryType.PLAYER) || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getAction().equals(InventoryAction.HOTBAR_SWAP) || event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)) {
                        Player player = (Player) event.getWhoClicked();
                        if (player != null) {
                            if (checkAvailability(player)) {
                                int result = ThreadLocalRandom.current().nextInt(2, 5);
                                player.giveExp(itemStack.getAmount()*result);
                            } else {
                                player.setExp(0.0f);
                            }
                            itemStack.setAmount(0);
                        }
                    }

                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        ItemStack itemStack = event.getOldCursor();
        if (itemStack.getType().equals(Material.EXPERIENCE_BOTTLE) && !event.getWhoClicked().getGameMode().equals(GameMode.SPECTATOR)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String maxLevel = EXPManager.instance.getConfig().getString("players." + event.getPlayer().getName());

        if (maxLevel == null) {
            EXPManager.instance.getConfig().set("players." + event.getPlayer().getName(), 0);
            EXPManager.instance.saveConfig();
        }
    }

    @EventHandler
    public void onPlayerLvlUp(PlayerLevelChangeEvent event) {
        System.out.println(event.getPlayer().getName() + " has lvl up from " + event.getOldLevel() + " to " + event.getNewLevel());
        if ((event.getNewLevel() - event.getOldLevel() > 0)) {
            fixPlayerLvl(event.getPlayer());
            Server server = Bukkit.getServer();
            String cmd = EXPManager.instance.getConfig().getString("on lvl up command");
            for (int i=0; i < event.getPlayer().getLevel() - event.getOldLevel(); i ++) {
                server.dispatchCommand(server.getConsoleSender(), cmd.replace("{player}", event.getPlayer().getName()));
                System.out.println("CMD " + cmd.replace("{player}", event.getPlayer().getName()) + " completed");
            }

            if (!checkAvailability(event.getPlayer())) {
                event.getPlayer().setExp(0.0f);
            }
        }
    }
}
