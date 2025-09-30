package daveiiii.simpleFactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import types.PluginConfig;
import util.BaseFactionCommand;
import types.PossibleFactionCommands;
import util.factionCommands.Create;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FactionsCommandManager implements CommandExecutor {

    private final Map<PossibleFactionCommands, BaseFactionCommand> commandMap = new HashMap<>();

    public FactionsCommandManager(PluginConfig config) {
        commandMap.put(PossibleFactionCommands.Create, new Create(config));
    }

    @Override
    public boolean onCommand (@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /f <command>");
            return true;
        }

        String command = args[0].toLowerCase();
        try {
            PossibleFactionCommands possibleFactionCommand = PossibleFactionCommands.getValue(command);
            if (!commandMap.containsKey(possibleFactionCommand)) {
                sender.sendMessage("Unknown command. Use /f <command>");
                return true;
            }

            commandMap.get(possibleFactionCommand).execute(sender, Arrays.copyOfRange(args, 1, args.length));

        } catch (IllegalArgumentException e) {
            sender.sendMessage("Unknown command. Use /f <command>");
            return true;
        }

        return true;
    }
}
