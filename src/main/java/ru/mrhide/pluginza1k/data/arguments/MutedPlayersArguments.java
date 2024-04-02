package ru.mrhide.pluginza1k.data.arguments;

import org.bukkit.command.CommandSender;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import wf.utils.bukkit.command.subcommand.executor.types.ArgumentType;

import java.util.List;

public class MutedPlayersArguments implements ArgumentType {
    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public String getMessageCode() {
        return "";
    }

    @Override
    public String getName() {
        return "muted_players";
    }

    @Override
    public boolean isIt(CommandSender commandSender, String[] strings, String s) {
        return true;
    }

    @Override
    public Object get(CommandSender commandSender, String[] strings, String s) {
        return s;
    }

    @Override
    public List<String> tabulation(CommandSender commandSender, String[] args, String s) {
        return DarkAgeChatSystem.getMuteChatPlayers().getAllChatMutes(args[1]);
    }
}
