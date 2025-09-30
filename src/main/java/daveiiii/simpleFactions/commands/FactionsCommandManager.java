package daveiiii.simpleFactions.commands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import types.PluginConfig;
import util.BaseFactionCommand;
import types.PossibleFactionCommands;
import util.factionCommands.*;

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
        commandMap.put(PossibleFactionCommands.Disband, new Disband(config, connection));
        commandMap.put(PossibleFactionCommands.Invite, new Invite(config, connection));
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
                sender.sendMessage(Component.text(String.format("Internal Error: Command %s not found.", possibleFactionCommand.getCommand())).color(NamedTextColor.RED));
                return true;
            }

            commandMap.get(possibleFactionCommand).execute(sender, Arrays.copyOfRange(args, 1, args.length));

        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text(String.format("Unknown command \"%s\". Use /f <command>", command)).color(NamedTextColor.RED));
            return true;
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return true;
    }
}
