package ru.mrhide.pluginza1k.data.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import wf.utils.bukkit.data.PersistDataUtils;

public class ChatEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        DarkAgeChatSystem.getChatsHashMap().loadPlayers(DarkAgeChatSystem.getActivePlayers());
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
        if (DarkAgeChatSystem.getChatsHashMap().getChatByGuiItem(event.getCurrentItem()) == null) return;
        if (event.getCurrentItem().getItemMeta().getCustomModelData() == DarkAgeChatSystem.getChatsHashMap().getChatByGuiItem(event.getCurrentItem()).getItem().getItemMeta().getCustomModelData()) {
            event.setCancelled(true);
            player.closeInventory();
            DarkAgeChatSystem.getChatsHashMap().getChatByGuiItem(event.getCurrentItem()).addActivePlayer(player);
            player.performCommand("chat rules " + DarkAgeChatSystem.getChatsHashMap().getTagByChat(DarkAgeChatSystem.getChatsHashMap().getChatByGuiItem(event.getCurrentItem())) + " " + 1);
        }
    }

    @EventHandler
    public void onClickMutePlayerList(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().hasItemMeta()) return;
        if(!player.isOp()) return;
        if(event.getCurrentItem().getItemMeta().hasCustomModelData()) {
            if (event.getCurrentItem().getItemMeta().getCustomModelData() == DarkAgeChatSystem.getConfiguration().getGuiMutePlayersList().getItemMeta().getCustomModelData()) {
                player.closeInventory();
                BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (DarkAgeChatSystem.getMutesInventories().get(0) == null) {
                            return;
                        }
                        player.openInventory(DarkAgeChatSystem.getMutesInventories().get(0));
                    }
                };
                bukkitRunnable.runTaskLater(DarkAgeChatSystem.getInstance(), 1);
                event.setCancelled(true);
            }

            if (event.getCurrentItem().getItemMeta().getCustomModelData() == 132432) {
                for (int i = 1; i < DarkAgeChatSystem.getMutesInventories().size(); i++) {
                    if (player.getInventory() == DarkAgeChatSystem.getMutesInventories().get(i)) {
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
            DarkAgeChatSystem.getMuteChatPlayers().unmute(DarkAgeChatSystem.getChatsHashMap().get(utils.getString("chatTag", itemStack)), utils.getString("player", itemStack));
            player.closeInventory();
        }

    }
}

