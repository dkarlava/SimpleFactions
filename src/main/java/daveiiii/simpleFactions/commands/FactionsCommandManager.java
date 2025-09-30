package daveiiii.simpleFactions.commands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import types.PluginConfig;
import util.BaseFactionCommand;
import types.PossibleFactionCommands;
import util.factionCommands.Create;
import util.factionCommands.Leave;
import util.factionCommands.ListCommand;
import util.factionCommands.Show;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FactionsCommandManager implements CommandExecutor {

    private final Logger logger;
    private final Map<PossibleFactionCommands, BaseFactionCommand> commandMap = new HashMap<>();

    public FactionsCommandManager(PluginConfig config, DataBaseHelper connection, Logger logger) {
        this.logger = logger;
        commandMap.put(PossibleFactionCommands.Create, new Create(config, connection));
        commandMap.put(PossibleFactionCommands.List, new ListCommand(config, connection));
        commandMap.put(PossibleFactionCommands.Leave, new Leave(config, connection));
        commandMap.put(PossibleFactionCommands.Show, new Show(config, connection));
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
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return true;
    }
}
