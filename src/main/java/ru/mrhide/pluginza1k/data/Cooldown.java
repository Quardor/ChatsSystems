package ru.mrhide.pluginza1k.data;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class Cooldown{
    private HashMap<Player, Long> cooldowns;
    private Long cooldown;

    public Cooldown(Long cooldown){
        cooldowns = new HashMap<>();
        this.cooldown = cooldown;
    }

    public void setCooldown(Player player){
        cooldowns.put(player, System.currentTimeMillis()+ cooldown);
    }

    public boolean isCooldowned(Player player){
        if(cooldowns.containsKey(player)){
            return System.currentTimeMillis()<cooldowns.get(player);
        }
        return false;
    }
}
