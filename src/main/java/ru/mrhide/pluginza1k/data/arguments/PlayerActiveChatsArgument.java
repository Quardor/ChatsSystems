package ru.mrhide.pluginza1k.data.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mrhide.pluginza1k.DarkAgeChatSystem;
import wf.utils.bukkit.command.subcommand.executor.types.ArgumentType;

import java.util.List;

public class PlayerActiveChatsArgument implements ArgumentType {

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
        return "player_active_chats";
    }

    @Override
    public boolean isIt(CommandSender commandSender, String[] strings, String s) {
        if(DarkAgeChatSystem.getChatsHashMap().containsKey(s)){
            return DarkAgeChatSystem.getChatsHashMap().get(s).getActivePlayers().contains((Player) commandSender);
        }
        return false;
    }

    @Override
    public Object get(CommandSender commandSender, String[] strings, String s) {
        return DarkAgeChatSystem.getChatsHashMap().get(s);
    }

    @Override
    public List<String> tabulation(CommandSender commandSender, String[] strings, String s) {
        return DarkAgeChatSystem.getChatsHashMap().getPlayerChatsString((Player) commandSender);
    }
}
