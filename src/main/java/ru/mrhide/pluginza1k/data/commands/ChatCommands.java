package ru.mrhide.pluginza1k.data.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import ru.mrhide.pluginza1k.chats.Chat;
import ru.mrhide.pluginza1k.data.Cooldown;
import ru.mrhide.pluginza1k.data.MuteChatPlayers;
import ru.mrhide.pluginza1k.data.arguments.ChatsListArguments;
import ru.mrhide.pluginza1k.data.arguments.MuteTypeArguments;
import ru.mrhide.pluginza1k.data.arguments.MutedPlayersArguments;
import ru.mrhide.pluginza1k.data.arguments.PlayerActiveChatsArgument;
import wf.utils.bukkit.command.handler.CommandHandler;
import wf.utils.bukkit.command.handler.CommandHandlerBuilder;
import wf.utils.bukkit.command.handler.ExecutionCommand;
import wf.utils.bukkit.command.subcommand.SubCommandBuilder;
import wf.utils.bukkit.command.subcommand.executor.Argument;
import wf.utils.bukkit.command.subcommand.executor.types.ArgumentType;
import wf.utils.bukkit.command.subcommand.executor.types.bukkit.BukkitArgumentType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatCommands {
    public static void init() {
        CommandHandler commandHandler = new CommandHandlerBuilder()
                .setPlugin(DarkAgeChatSystem.getInstance())
                .setCommands("ch")
                .build();

        commandHandler.addSubcommand(
                new SubCommandBuilder()
                        .setOnlyPlayer(true)
                        .setCommand("send")
                        .setArguments(
                                new Argument(new PlayerActiveChatsArgument()),
                                new Argument(ArgumentType.MULTI_STRING)
                        )
                        .setRunnable(ChatCommands::sendMessage)
                        .build()
        );

        commandHandler.addSubcommand(
                new SubCommandBuilder()
                        .setOnlyPlayer(true)
                        .setCommand("on")
                        .setArguments(new Argument(new ChatsListArguments()))
                        .setRunnable(ChatCommands::playerJoinChat)
                        .build()
        );

        commandHandler.addSubcommand(
                new SubCommandBuilder()
                        .setOnlyPlayer(true)
                        .setCommand("off")
                        .setArguments(new Argument(new PlayerActiveChatsArgument()))
                        .setRunnable(ChatCommands::playerLeaveChat)
                        .build()
        );

        commandHandler.addSubcommand(
                new SubCommandBuilder()
                        .setOnlyPlayer(true)
                        .setCommand("rules")
                        .setArguments(
                                new Argument(new PlayerActiveChatsArgument()),
                                new Argument(ArgumentType.INTEGER)
                        )
                        .setRunnable(ChatCommands::openChatRules)
                        .build()
        );

        commandHandler.addSubcommand(
                new SubCommandBuilder()
                        .setOnlyPlayer(true)
                        .setCommand("gui")
                        .setRunnable(ChatCommands::openChatGui)
                        .build()
        );

        commandHandler.addSubcommand(
                new SubCommandBuilder()
                        .setOnlyPlayer(true)
                        .setCommand("adminmute")
                        .setPermission("command.admin.mute")
                        .setArguments(
                                new Argument(ArgumentType.INTEGER),
                                new Argument(new MuteTypeArguments()),
                                new Argument(BukkitArgumentType.ONLINE_PLAYER)
                        )
                        .setRunnable(ChatCommands::adminMute)
                        .build()
        );

        commandHandler.addSubcommand(
                new SubCommandBuilder()
                        .setOnlyPlayer(true)
                        .setCommand("adminunmute")
                        .setPermission("command.admin.mute")
                        .setArguments(
                                new Argument(BukkitArgumentType.ONLINE_PLAYER)
                        )
                        .setRunnable(ChatCommands::adminUnmute)
                        .build()
        );

        commandHandler.addSubcommand(
                new SubCommandBuilder()
                        .setOnlyPlayer(true)
                        .setCommand("mute")
                        .setArguments(
                                new Argument(BukkitArgumentType.ONLINE_PLAYER),
                                new Argument(ArgumentType.INTEGER),
                                new Argument(new MuteTypeArguments())
                        )
                        .setRunnable(ChatCommands::mute)
                        .build()
        );

        commandHandler.addSubcommand(
                new SubCommandBuilder()
                        .setOnlyPlayer(true)
                        .setCommand("unmute")
                        .setArguments(new Argument(new MutedPlayersArguments()))
                        .setRunnable(ChatCommands::unmute)
                        .build()
        );
    }

    private static void mute(CommandSender commandSender, ExecutionCommand executionCommand, Object[] args) {
        MuteChatPlayers mute = DarkAgeChatSystem.getMuteChatPlayers();
        Player target = (Player) args[0];
        int time = (int) args[1];
        String type = (String) args[2];

        long muteTime = 0;

        if(type.equals("seconds")) muteTime = time*1000L;
        if(type.equals("minutes")) muteTime = time*60L*1000L;
        if(type.equals("hours")) muteTime = time*3600L*1000L;
        if(type.equals("days")) muteTime = time*86400L*1000L;

        mute.setMuteForPlayerByPlayer(target.getName(), commandSender.getName(), muteTime);
    }

    private static void unmute(CommandSender commandSender, ExecutionCommand executionCommand, Object[] args) {
        MuteChatPlayers mute = DarkAgeChatSystem.getMuteChatPlayers();
        String playerName = (String) args[0];
        if (mute.isMuted(commandSender.getName(), playerName)) {
            mute.unmute(commandSender.getName(), playerName);
        }
    }

    private static void sendMessage(CommandSender commandSender, ExecutionCommand executionCommand, Object[] args) {
        Cooldown cooldown = DarkAgeChatSystem.getCooldown();
        if(cooldown.isCooldowned((Player) commandSender)){
            commandSender.sendMessage("Помедленней!");
            return;
        }
        Chat chat = (Chat) args[0];

        StringBuilder message = new StringBuilder();
        message.append(ChatColor.translateAlternateColorCodes('&', chat.getPrefix())).append(" ").append(commandSender.getName()).append(": ");

        for (int i = 1; i < args.length; i++) {
            message.append(args[i]);
            if (i < args.length - 1) {
                message.append(" ");
            }
        }

        chat.sendChatMessage((Player) commandSender, message.toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            BufferedWriter writer = chat.getWriter();
            writer.write("[" + dateFormat.format(new Date()) + "] " + message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cooldown.setCooldown((Player) commandSender);
    }


    private static void playerJoinChat(CommandSender commandSender, ExecutionCommand executionCommand, Object[] args) {
        Chat chat = (Chat) args[0];
        if(chat.getActivePlayers().contains((Player) commandSender)) {
            commandSender.sendMessage(chat.getChatAlreadyContainsPlayerMessage());
            return;
        }
        chat.addActivePlayer((Player) commandSender);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',chat.getOnEnableMessage()));
    }

    private static void playerLeaveChat(CommandSender commandSender, ExecutionCommand executionCommand, Object[] args) {
        Chat chat = (Chat) args[0];
        chat.removeActivePlayer((Player) commandSender);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',chat.getOnDisableMessage()));
    }

    private static void openChatRules(CommandSender commandSender, ExecutionCommand executionCommand, Object[] args) {
        Chat chat = (Chat) args[0];
        int pageNumber = (int) args[1];
        if (pageNumber >= 1 && pageNumber <= chat.getRules().size()) {
            chat.printChatRulesPage((Player) commandSender, (Integer) args[1]);

                TextComponent message = new TextComponent(ChatColor.GREEN + "Следующая страница:");
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ch rules " + DarkAgeChatSystem.getChatsHashMap().getTagByChat(chat) + " " + (pageNumber + 1)));

                TextComponent message1 = new TextComponent(ChatColor.RED + "Предыдущая страница:");
                message1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ch rules " + DarkAgeChatSystem.getChatsHashMap().getTagByChat(chat) + " " + (pageNumber - 1)));
            if (pageNumber < chat.getRules().size()) {
                commandSender.sendMessage(message);
            }
            if (pageNumber > 1 ) {
                commandSender.sendMessage(message1);
            }

        } else commandSender.sendMessage("Такой страницы не существует!");
    }

    private static void openChatGui(CommandSender commandSender, ExecutionCommand executionCommand, Object[] args){
        Player player = (Player) commandSender;

        Inventory inventory = DarkAgeChatSystem.getConfiguration().getGui().getGui();
        player.openInventory(inventory);
    }

    private static void adminMute(CommandSender commandSender, ExecutionCommand executionCommand, Object[] args){
        int time = (int) args[1];
        String type = (String) args[2];
        Player target = (Player) args[3];
        if(type == null) return;
        MuteChatPlayers mute = DarkAgeChatSystem.getMuteChatPlayers();
        if (!commandSender.isOp()){
            commandSender.sendMessage("Данная команда предназначена для администраторов!");
            return;
        }
        long muteTime = 0;
        if(type.equals("seconds")) muteTime = time*1000L;
        if(type.equals("minutes")) muteTime = time*60L*1000L;
        if(type.equals("hours")) muteTime = time*3600L*1000L;
        if(type.equals("days")) muteTime = time*86400L*1000L;
        mute.setMuteForAll(target.getName(), muteTime);
    }

    private static void adminUnmute(CommandSender commandSender, ExecutionCommand executionCommand, Object[] args) {
        MuteChatPlayers mute = DarkAgeChatSystem.getMuteChatPlayers();
        String targetName = (String) args[1];
        mute.unmuteAll(targetName);
    }
}
