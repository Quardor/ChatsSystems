package ru.mrhide.pluginza1k;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ru.mrhide.pluginza1k.data.ChatHashmap;
import ru.mrhide.pluginza1k.data.Config;
import ru.mrhide.pluginza1k.data.Cooldown;
import ru.mrhide.pluginza1k.data.MuteChatPlayers;
import ru.mrhide.pluginza1k.data.commands.ChatCommands;
import ru.mrhide.pluginza1k.data.events.ChatEvents;
import wf.utils.bukkit.config.BukkitConfig;
import wf.utils.bukkit.config.utils.BukkitConfigBuilder;
import wf.utils.bukkit.data.PersistDataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DarkAgeChatSystem extends JavaPlugin {

    @Getter
    private static Config configuration;
    @Getter
    private static DarkAgeChatSystem instance;
    private static BukkitConfig bukkitConfig;
    @Getter
    private static BukkitConfig activePlayers;
    @Getter
    private static BukkitConfig mutedPlayersChats;
    @Getter
    private static ChatHashmap chatsHashMap;
    @Getter
    private static HashMap<String, List<Inventory>> playerMutesInventories;
    @Getter
    private static Cooldown cooldown;
    @Getter
    private static MuteChatPlayers muteChatPlayers;
    @Getter
    public static List<Inventory> mutesInventories;

    @Override
    public void onEnable() {
        instance = this;
        chatsHashMap = new ChatHashmap();
        playerMutesInventories = new HashMap<>();
        muteChatPlayers = new MuteChatPlayers();

        getServer().getPluginManager().registerEvents(new ChatEvents(), this);

        bukkitConfig = new BukkitConfigBuilder()
                .setPlugin(this)
                .setConfigName("config")
                .build();
        configuration = new Config(bukkitConfig);

        cooldown = new Cooldown(configuration.getChatsCooldown());

        activePlayers = new BukkitConfigBuilder()
                .setPlugin(this)
                .setConfigName("activeplayers")
                .build();

        mutedPlayersChats = new BukkitConfigBuilder()
                .setPlugin(this)
                .setConfigName("muted_players")
                .build();

        ChatCommands.init();

        muteChatPlayers.loadMutes(mutedPlayersChats);
    }

    @Override
    public void onDisable() {
        muteChatPlayers.saveMutes(mutedPlayersChats);
    }

    public static void loadActivePlayers() {
        DarkAgeChatSystem.getChatsHashMap().keySet().forEach(chatTag ->{
            DarkAgeChatSystem.getActivePlayers().getList("chats." + chatTag).forEach(playerName -> {
                DarkAgeChatSystem.getChatsHashMap().get(chatTag).addActivePlayer(Bukkit.getPlayer((String) playerName));
            });
        });
    }

    public static void saveActivePlayers(){
        DarkAgeChatSystem.getChatsHashMap().keySet().forEach(chatTag ->{
            DarkAgeChatSystem.getActivePlayers().set("chats." + chatTag, DarkAgeChatSystem.getChatsHashMap().get(chatTag).playersListToString());
        });
        DarkAgeChatSystem.getActivePlayers().save();
    }

    public static String convertMillis(long millis) {
        long seconds = millis / 1000;
        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        return String.format("%d дней, %d часов, %d минут, %d секунд", days, hours, minutes, seconds);
    }

    public static List<Inventory> createInventories(List<ItemStack> items) {
        if(items.isEmpty()) return null;
        List<Inventory> inventories = new ArrayList<>();

        ItemStack button = new ItemStack(Material.ARROW);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Назад");
        meta.setCustomModelData(132432);
        button.setItemMeta(meta);
        ItemStack button1 = new ItemStack(Material.ARROW);
        ItemMeta meta1 = button1.getItemMeta();
        meta1.setDisplayName(ChatColor.WHITE + "Вперед");
        meta1.setCustomModelData(657234);
        button1.setItemMeta(meta1);

        Inventory inventory = Bukkit.createInventory(null, 54, "Замьюченые игроки: ");
        inventory.setItem(53, button1);
        inventory.setItem(45, button);

        for (int i = 0; i < items.size(); i++) {
            if (i % 45 == 0 && i != 0) {
                inventories.add(inventory);
                inventory = Bukkit.createInventory(null, 54, "Замьюченые игроки: ");
                inventory.setItem(53, button1);
                inventory.setItem(45, button);
            }
            if (inventory.getItem(44) == null) {
                inventory.addItem(items.get(i));
            }
        }

        inventories.add(inventory);

        return inventories;
    }

    public static List<ItemStack> mutedPlayersToItemStacks(Player player){
        List<ItemStack> mutedIcons = new ArrayList<>();

        ItemStack itemStack = DarkAgeChatSystem.getConfiguration().getGuiMutePlayer();
        Material material = itemStack.getType();
        DarkAgeChatSystem.getMuteChatPlayers().get(player.getName()).keySet().forEach(targetName -> {
            List<String> lore = new ArrayList<>();
            ItemStack itemStack1 = new ItemStack(material);
            ItemMeta meta = itemStack1.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + targetName);
            lore.add((ChatColor.WHITE + DarkAgeChatSystem.getMuteChatPlayers().HowMuchIsLeft(player.getName(), targetName)));
            meta.setLore(lore);
            PersistDataUtils utils = new PersistDataUtils(DarkAgeChatSystem.getInstance());
            meta.setCustomModelData(13371337);
            itemStack1.setItemMeta(meta);
            utils.set("player", itemStack1, targetName);
            mutedIcons.add(itemStack1);
        });
        return mutedIcons;
    }
}
