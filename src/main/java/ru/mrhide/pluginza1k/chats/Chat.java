package ru.mrhide.pluginza1k.chats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;

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

    public void addAll(List<Player> players){
        activePlayers.addAll(players);
    }

    public void unmuteAll(){
        mutedPlayers.clear();
    }
    public void unmute(String player){
        mutedPlayers.remove(player);
    }

    public void addActivePlayer(Player player){
        if(activePlayers.contains(player)){
            player.sendMessage("Вы уже активировали этот чат!");
            return;
        }
        activePlayers.add(player);
        saveActivePlayers();
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
        HashMap<Player, List<Player>> mutedByPlayer = DarkAgeChatSystem.getMutedByPlayer();
        switch (type) {
            case "global":
                activePlayers.forEach(player -> {
                    if (player!=null && player.isOnline()) {
                        if(mutedByPlayer.containsKey(player) && mutedByPlayer.get(player).contains(sender)) return;
                        player.sendMessage(String.valueOf(message));
                    }
                });
                break;
            case "local":
                for (Player target : sender.getLocation().getNearbyPlayers(DarkAgeChatSystem.getConfiguration().getLocalChatRadius())) {
                    if(mutedByPlayer.containsKey(target) && mutedByPlayer.get(target).contains(sender)) return;
                    target.sendMessage(String.valueOf(message));
                }
                break;
            case "world":
                World world = sender.getWorld();
                activePlayers.forEach(player -> {
                    if (player!=null && player.isOnline() && player.getWorld() == world) {
                        if(mutedByPlayer.containsKey(player) && mutedByPlayer.get(player).contains(sender)) return;
                        player.sendMessage(String.valueOf(message));
                    }
                });
                break;
        }
    }

    public void printChatRulesPage(Player player, int pageNumber){
        rules.get(pageNumber-1).forEach(player::sendMessage);
    }
}
