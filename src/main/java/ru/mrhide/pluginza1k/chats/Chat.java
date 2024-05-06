package ru.mrhide.pluginza1k.chats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import ru.mrhide.pluginza1k.data.MuteChatPlayers;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.mrhide.pluginza1k.DarkAgeChatSystem.saveActivePlayers;

@Getter
@AllArgsConstructor
public class Chat {
    private String prefix;
    private String type;
    private int itemSlot;
    private List<List<String>> rules;
    private List<Player> activePlayers;
    private HashMap<String, Long> mutedPlayers;
    private ItemStack item;
    @Setter
    private BufferedWriter writer;
    private String onEnableMessage;
    private String onDisableMessage;
    private String chatAlreadyContainsPlayerMessage;
    @Getter
    private String chatTag;

    public void addAll(List<Player> players){
        activePlayers.addAll(players);
    }

    public void addActivePlayer(Player player){
        if(activePlayers.contains(player))return;
        activePlayers.add(player);
    }

    public void removeActivePlayer(Player player){
        if(activePlayers.contains(player)) {
            activePlayers.remove(player);
        }
        saveActivePlayers();
    }

    public List<String> playersListToString(){
        List<String> playersNicknames = new ArrayList<>();
        activePlayers.forEach(player ->{
            if(player == null) return;
            playersNicknames.add(player.getName());
        });
        return playersNicknames;
    }

    public void sendChatMessage(Player sender, String message){
        MuteChatPlayers mute = DarkAgeChatSystem.getMuteChatPlayers();
        switch (type) {
            case "global":
                activePlayers.forEach(player -> {
                    if (player!=null && player.isOnline()) {
                        if (mute.isMuted(player.getName(), sender.getName())) return;
                        player.sendMessage(String.valueOf(message));
                    }
                });
                break;
            case "local":
                for (Player target : sender.getLocation().getNearbyPlayers(DarkAgeChatSystem.getConfiguration().getLocalChatRadius())) {
                    if (mute.isMuted(target.getName(), sender.getName())) return;
                    target.sendMessage(String.valueOf(message));
                }
                break;
            case "world":
                World world = sender.getWorld();
                activePlayers.forEach(player -> {
                    if (player!=null && player.isOnline() && player.getWorld() == world) {
                        if (mute.isMuted(player.getName(), sender.getName())) return;
                        player.sendMessage(String.valueOf(message));
                    }
                });
                break;
        }
    }

    public void printChatRulesPage(Player player, int pageNumber){
        rules.get(pageNumber-1).forEach(msg ->{
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        });
    }
}
