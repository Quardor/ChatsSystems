package ru.mrhide.pluginza1k.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import wf.utils.bukkit.config.BukkitConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MuteChatPlayers extends HashMap<String, HashMap<String, Long>> {

    public void setMuteForPlayerByPlayer(String playerTarget, String player, Long muteTime){
        if(get(player) == null){
            put(player, new HashMap<>());
            get(player).put(playerTarget, muteTime + System.currentTimeMillis());
            saveMutes(DarkAgeChatSystem.getMutedPlayersChats());
            return;
        }
        if(get(player).containsKey(playerTarget)){
            get(player).remove(playerTarget);
            get(player).put(playerTarget, muteTime + System.currentTimeMillis());
            saveMutes(DarkAgeChatSystem.getMutedPlayersChats());
            return;
        }
        get(player).put(playerTarget, muteTime + System.currentTimeMillis());
        saveMutes(DarkAgeChatSystem.getMutedPlayersChats());
    }

    public void setMuteForAll(String playerTarget, Long muteTime){
        Bukkit.getOnlinePlayers().forEach(player -> {
            setMuteForPlayerByPlayer(playerTarget, player.getName(), muteTime);
        });
    }

    public boolean isMuted(String player, String target){
        if(containsKey(player) && get(player).containsKey(target)){
            return System.currentTimeMillis()<get(player).get(target);
        }
        unmute(player, target);
        return false;
    }

    public List<String> getMutedByPlayer(String senderName){
        if(get(senderName) == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(get(senderName).keySet());
    }

    public void unmute(String sender, String target) {
        if (get(sender) == null || !containsKey(sender)) return;
        if (!get(sender).containsKey(target)) return;
        get(sender).remove(target);
        saveMutes(DarkAgeChatSystem.getMutedPlayersChats());
    }

    public void unmuteAll(String target){
        Bukkit.getOnlinePlayers().forEach(player -> {
           unmute(player.getName(), target);
        });
    }

    public String HowMuchIsLeft(String playerName, String targetName){
        long millis = get(playerName).get(targetName) - System.currentTimeMillis();
        return DarkAgeChatSystem.convertMillis(millis);
    }

    public void loadMutes(BukkitConfig config){
        if (config.getConfigurationSection("players") == null) return;
        config.getConfigurationSection("players").getKeys(true).forEach(playerName -> {
            if (config.getConfigurationSection("players." + playerName) == null) return;
            config.getConfigurationSection("players." + playerName).getKeys(true).forEach(playerNames -> {
                if (DarkAgeChatSystem.getChatsHashMap().get(playerName) == null) return;
                DarkAgeChatSystem.getChatsHashMap().get(playerName).getMutedPlayers().put(playerNames, config.getLong("players." + playerName + "." + playerNames) + System.currentTimeMillis());

                put(playerName, DarkAgeChatSystem.getChatsHashMap().get(playerName).getMutedPlayers());

            });
        });
    }

    public void saveMutes(BukkitConfig config){
        if(isEmpty()) return;
        config.remove("players");
        keySet().forEach(playerName -> {
            ConfigurationSection parentSection = config.getConfigurationSection("players." + playerName);
            if (parentSection == null) {
                parentSection = config.createSection("players." + playerName);
            }
            ConfigurationSection finalParentSection = parentSection;
            get(playerName).keySet().forEach(player -> {
                ConfigurationSection playerSection = finalParentSection.getConfigurationSection(player);
                if(playerSection == null) {
                    finalParentSection.createSection(player);
                }
                finalParentSection.set(player, Math.abs(get(playerName).get(player) - System.currentTimeMillis()));
            });
        });
        config.save();
    }
}
