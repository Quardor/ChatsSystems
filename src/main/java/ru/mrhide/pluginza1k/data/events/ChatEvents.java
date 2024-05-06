package ru.mrhide.pluginza1k.data.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import ru.mrhide.pluginza1k.chats.Chat;
import wf.utils.bukkit.data.PersistDataUtils;

import java.util.HashMap;
import java.util.List;

public class ChatEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                DarkAgeChatSystem.getChatsHashMap().values().forEach(value -> {
                    value.addActivePlayer(player);
                });
            }
        };
        runnable.runTaskAsynchronously(DarkAgeChatSystem.getInstance());
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!event.getCurrentItem().getItemMeta().hasCustomModelData()) return;
        if(event.getCurrentItem().getItemMeta().getCustomModelData() == DarkAgeChatSystem.getConfiguration().getGui().getNullItem().getItemMeta().getCustomModelData()){
            event.setCancelled(true);
        }
        Bukkit.getLogger().info(event.getCurrentItem().getItemMeta().getCustomModelData() + " ");
        if (DarkAgeChatSystem.getChatsHashMap().getChatByGuiItem(event.getCurrentItem()) == null) return;
        Chat chat = DarkAgeChatSystem.getChatsHashMap().getChatByGuiItem(event.getCurrentItem());
        String chatTag = chat.getChatTag();
        if(chat.getActivePlayers().contains(player)){
            player.performCommand("ch off " + chatTag);
        } else {
            player.performCommand("ch on " + chatTag);
            player.performCommand("ch rules " + chatTag + " " + 1);
        }
        event.setCancelled(true);
        player.closeInventory();
    }

    @EventHandler
    public void onClickMutePlayerList(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!player.isOp()) return;
        HashMap<String, List<Inventory>> inventories = DarkAgeChatSystem.getPlayerMutesInventories();
        if(event.getCurrentItem().getItemMeta().hasCustomModelData()) {
            if (event.getCurrentItem().getItemMeta().getCustomModelData() == DarkAgeChatSystem.getConfiguration().getGuiMutePlayersList().getItemMeta().getCustomModelData()) {
                player.closeInventory();
                if(!inventories.containsKey(player.getName())){
                    inventories.put(player.getName(), DarkAgeChatSystem.createInventories(DarkAgeChatSystem.mutedPlayersToItemStacks(player)));
                }
                if(inventories.get(player.getName()) == null){
                    inventories.put(player.getName(), DarkAgeChatSystem.createInventories(DarkAgeChatSystem.mutedPlayersToItemStacks(player)));
                }
                BukkitRunnable bukkitRunnable = new BukkitRunnable() {

                    @Override
                    public void run() {
                        player.openInventory(inventories.get(player.getName()).get(0));
                    }
                };
                bukkitRunnable.runTaskLater(DarkAgeChatSystem.getInstance(), 1);
                event.setCancelled(true);
            }

            if (event.getCurrentItem().getItemMeta().getCustomModelData() == 132432) {
                for (int i = 1; i < DarkAgeChatSystem.getMutesInventories().size(); i++) {
                    if (player.getInventory() == DarkAgeChatSystem.getMutesInventories().get(i)) {
                        player.openInventory(inventories.get(player.getName()).get(0));
                        player.openInventory(DarkAgeChatSystem.getMutesInventories().get(i - 1));
                    }
                }
            }
            if (event.getCurrentItem().getItemMeta().getCustomModelData() == 657234) {
                for (int i = 0; i < DarkAgeChatSystem.getMutesInventories().size() - 1; i++) {
                    if (player.getInventory() == DarkAgeChatSystem.getMutesInventories().get(i)) {
                        player.openInventory(DarkAgeChatSystem.getMutesInventories().get(i + 1));
                    }
                }
            }
        }
        if (event.getCurrentItem().getItemMeta().getCustomModelData() == 13371337) {
            ItemStack itemStack = event.getCurrentItem();
            PersistDataUtils utils = new PersistDataUtils(DarkAgeChatSystem.getInstance());
            player.closeInventory();
        }

    }
}

