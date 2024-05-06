package ru.mrhide.pluginza1k.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import ru.mrhide.pluginza1k.chats.Chat;
import wf.utils.bukkit.config.BukkitConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatHashmap extends HashMap<String, Chat> {

    public List<String> getPlayerChatsString(Player player){
        List<String> playerChats = new ArrayList<>();
        keySet().forEach(chat ->{
            if(get(chat).getActivePlayers().contains(player)){
                playerChats.add(chat);
            }
        });
        return playerChats;
    }

    public Chat getChatByGuiItem(ItemStack itemStack) {
        Config configuration = DarkAgeChatSystem.getConfiguration();

        BukkitConfig config = configuration.getConfig();

        ConfigurationSection chats = config.getConfigurationSection("chats");

        int cmd = itemStack.getItemMeta().getCustomModelData();
        return get(chats.getString (chats.getKeys(true).stream().filter(chat -> chats.getInt(chat + ".item_custom_model_data") == cmd).findAny().orElse(null) + ".tag_name"));
    }
}

