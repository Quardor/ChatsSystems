package ru.mrhide.pluginza1k.data.arguments;

import org.bukkit.command.CommandSender;
import wf.utils.bukkit.command.subcommand.executor.types.ArgumentType;

import java.util.ArrayList;
import java.util.List;

public class MuteTypeArguments implements ArgumentType {
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
        return "mute_time_type";
    }

    @Override
    public boolean isIt(CommandSender commandSender, String[] strings, String s) {

        return s.equals("seconds") || s.equals("minutes") || s.equals("hours") || s.equals("days");
    }

    @Override
    public Object get(CommandSender commandSender, String[] strings, String s) {
        return s;
    }

    @Override
    public List<String> tabulation(CommandSender commandSender, String[] strings, String s) {
        List<String> timeTypes = new ArrayList<>();
        timeTypes.add("seconds");
        timeTypes.add("minutes");
        timeTypes.add("hours");
        timeTypes.add("days");
        return timeTypes;
    }
}
