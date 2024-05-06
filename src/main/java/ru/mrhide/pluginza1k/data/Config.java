package ru.mrhide.pluginza1k.data;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import ru.mrhide.pluginza1k.chats.Chat;
import wf.utils.bukkit.config.BukkitConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class Config {
    private final BukkitConfig config;

    private ItemStack itemStack;
    private ItemStack guiItemStack;
    private ItemStack guiMutePlayersList;
    private ItemStack guiMutePlayer;

    private int localChatRadius;
    private int guiMutePlayersSlot;

    private long chatsCooldown;

    @Getter
    private final Gui gui;

    public Config(BukkitConfig bukkitConfig) {
        this.config = bukkitConfig;

        bukkitConfig.getConfigurationSection("chats").getKeys(true).forEach(this::loadChat);

        localChatRadius = bukkitConfig.getInt("local_chat_radius");

        chatsCooldown = bukkitConfig.getLong("cooldown")*1000L;

        guiMutePlayersSlot = bukkitConfig.getInt("gui.muted_player_list_slot");

        if(bukkitConfig.getString("gui.muted_player_list_material") != null) {
            guiMutePlayersList = new ItemStack(Material.valueOf(bukkitConfig.getString("gui.muted_player_list_material").toUpperCase()));
            ItemMeta meta = guiMutePlayersList.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', bukkitConfig.getString("gui.muted_player_list_name")));
            meta.setCustomModelData(bukkitConfig.getInt("gui.muted_player_list_custom_model_data"));
            guiMutePlayersList.setItemMeta(meta);
        }

        if(bukkitConfig.getString("gui.muted_item_material") != null) {
            guiMutePlayer = new ItemStack(Material.valueOf(bukkitConfig.getString("gui.muted_item_material").toUpperCase()));
            guiMutePlayer.getItemMeta().setCustomModelData(bukkitConfig.getInt("gui.muted_item_custom_model_data"));
        }

        itemStack = new ItemStack(Material.valueOf(bukkitConfig.getString("gui.slots_aggregate.material").toUpperCase()));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',bukkitConfig.getString("gui.slots_aggregate.aggregate_name")));
        List<String> lore = bukkitConfig.getStringList("gui.slots_aggregate.aggregate_lore");
        lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
        meta.setLore(lore);
        meta.setCustomModelData(bukkitConfig.getInt("gui.slots_aggregate.item_custom_model_data"));
        itemStack.setItemMeta(meta);

        gui = new Gui(bukkitConfig.getString("gui.name_gui"), bukkitConfig.getIntegerList("gui.slots_aggregate.slot"), DarkAgeChatSystem.getChatsHashMap().values(), itemStack, guiMutePlayersSlot, guiMutePlayersList);
    }

    public void loadChat(String key){
        List<List<String>> rulesList = new ArrayList<>();
        ConfigurationSection chatSection = config.getConfigurationSection("chats."+key);
        ConfigurationSection chatRules = config.getConfigurationSection("chats."+key+".rules");

        if(chatSection == null) {
            return;
        }

        String itemMaterial = chatSection.getString("item_material");

        if (itemMaterial == null) return;

        String itemName = chatSection.getString("item_name");
        ItemStack item = new ItemStack(Material.valueOf(itemMaterial.toUpperCase()));
        ItemMeta meta1 = item.getItemMeta();
        meta1.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
        meta1.setCustomModelData(chatSection.getInt("item_custom_model_data"));

        item.setItemMeta(meta1);


        String logsFileName = chatSection.getString("logs_file_name");

        String chatTag = chatSection.getString("tag_name");

        String chatPrefix = chatSection.getString("prefix");

        String chatType = chatSection.getString("type");

        String onEnableMessage = chatSection.getString("on_enable_message");

        String onDisableMessage = chatSection.getString("on_disable_message");

        String chatAlreadyContainsPlayerMessage = chatSection.getString("chat_already_contains_player_message");

        if(chatRules != null) {
            chatRules.getKeys(true).forEach(list -> {
                List<String> chatRule = chatRules.getStringList(list);
                rulesList.add(chatRule);
            });
        }

        int itemSlot = chatSection.getInt("slot");

        Chat chat = new Chat(chatPrefix, chatType, itemSlot, rulesList, new ArrayList<>(), new HashMap<>(), item, null, onEnableMessage, onDisableMessage, chatAlreadyContainsPlayerMessage, chatTag);

        try {
            if(logsFileName == null) {
                return;
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter( DarkAgeChatSystem.getInstance().getDataFolder() + File.separator + logsFileName, true));
            chat.setWriter(writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DarkAgeChatSystem.getChatsHashMap().put(chatTag, chat);
    }
}
