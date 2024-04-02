package ru.mrhide.pluginza1k.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import ru.mrhide.pluginza1k.chats.Chat;
import wf.utils.bukkit.config.BukkitConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MuteChatPlayers extends HashMap<Chat, HashMap<String, Long>> {

    public void setMutedChatPlayers(Chat chat, String player, Long muteTime){
        chat.getMutedPlayers().put(player, System.currentTimeMillis() + muteTime);
        put(chat, chat.getMutedPlayers());
    }

    public boolean isMuted(Chat chat, Player player){
        if(containsKey(chat) && chat.getMutedPlayers().containsKey(player.getName())){
            return System.currentTimeMillis()<get(chat).get(player.getName());
        }
        unmute(chat, player.getName());
        return false;
    }

    public List<String> getAllChatMutes(String chatTag){
        Chat chat = DarkAgeChatSystem.getChatsHashMap().get(chatTag);
        if(chat == null) return null;
        List<String> mutedPlayers = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(isMuted(chat, player)) mutedPlayers.add(player.getName());
        });
        return mutedPlayers;
    }

    public List<String> getAllMutes(){
        List<String> playerNames = new ArrayList<>();
        values().forEach(chat -> {
            chat.keySet().forEach(player ->{
                if(!playerNames.contains(player)){
                    playerNames.add(player);
                }
            });
        });
        return playerNames;
    }

    public void unmute(Chat chat, String player){
        if (!keySet().contains(chat)) return;
        if (get(chat) == null) return;
        get(chat).remove(player);
        chat.unmute(player);
    }

    public List<Chat> getMutedChatByPlayer(String playerName){
        List<Chat> mutedChats = new ArrayList<>();
        for (Chat chat : keySet()) {
            for (String string : get(chat).keySet()) {
                if (string.equals(playerName)) mutedChats.add(chat);
            }
        }
        return mutedChats;
    }

    public String HowMuchIsLeft(Chat chat, String playerName){
        long millis = get(chat).get(playerName) - System.currentTimeMillis();
        return DarkAgeChatSystem.convertMillis(millis);
    }

    public void loadMutes(BukkitConfig config){
        if (config.getConfigurationSection("chats") == null) return;
        config.getConfigurationSection("chats").getKeys(true).forEach(chatTag -> {
            if (config.getConfigurationSection("chats." + chatTag) == null) return;
            config.getConfigurationSection("chats." + chatTag).getKeys(true).forEach(playerNames -> {
                if (DarkAgeChatSystem.getChatsHashMap().get(chatTag) == null) return;
                DarkAgeChatSystem.getChatsHashMap().get(chatTag).getMutedPlayers().put(playerNames, config.getLong("chats." + chatTag + "." + playerNames) + System.currentTimeMillis());

                put(DarkAgeChatSystem.getChatsHashMap().get(chatTag), DarkAgeChatSystem.getChatsHashMap().get(chatTag).getMutedPlayers());

            });
        });
    }

    public void saveMutes(BukkitConfig config){
        if(isEmpty()) return;
        config.remove("chats");
        keySet().forEach(chat -> {
            if(chat == null) return;

            if (DarkAgeChatSystem.getChatsHashMap().getTagByChat(chat) == null) return;
            String chatTag = DarkAgeChatSystem.getChatsHashMap().getTagByChat(chat);
            ConfigurationSection parentSection = config.getConfigurationSection("chats." + chatTag);
            if (parentSection == null) {
                parentSection = config.createSection("chats." + chatTag);
            }
            if(DarkAgeChatSystem.getChatsHashMap().get(chatTag) == null) return;
            ConfigurationSection finalParentSection = parentSection;
            DarkAgeChatSystem.getChatsHashMap().get(chatTag).getMutedPlayers().keySet().forEach(player -> {

                Bukkit.getLogger().info(player);

                ConfigurationSection playerSection = finalParentSection.getConfigurationSection(player);
                if(playerSection == null) {
                    finalParentSection.createSection(player);
                }
                finalParentSection.set(player, DarkAgeChatSystem.getChatsHashMap().get(chatTag).getMutedPlayers().get(player) - System.currentTimeMillis());
            });
        });
        config.save();
    }
}
