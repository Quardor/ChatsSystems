package ru.mrhide.pluginza1k.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mrhide.pluginza1k.chats.Chat;
import wf.utils.bukkit.config.BukkitConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatHashmap extends HashMap<String, Chat> {

    public void loadPlayers(BukkitConfig config){
        keySet().forEach(chatTag -> {
            get(chatTag).addAll(stringsToPlayers(config.getStringList("chats." + chatTag)));
        });
    }

    private List<Player> stringsToPlayers(List<String> nicknames){
        List<Player> players = new ArrayList<>();
        nicknames.forEach(nickname -> {
            players.add(Bukkit.getPlayer(nickname));
        });
        return players;
    }

    public List<String> getPlayerChatsString(Player player){
        List<String> playerChats = new ArrayList<>();
        keySet().forEach(chat ->{
            if(get(chat).getActivePlayers().contains(player)){
                playerChats.add(chat);
            }
        });
        return playerChats;
    }

    public String getTagByChat(Chat chat){
        for (String s : keySet()) {
            if (get(s) == chat) return s;
        }
        return null;
    }

    public Chat getChatByGuiItem(ItemStack itemStack){
        for (Chat value : values()) {
            if(value.getItem().getItemMeta().getCustomModelData() == itemStack.getItemMeta().getCustomModelData()) return value;
        }
        return null;
    }

}

