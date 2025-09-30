package util.factionCommands;

import org.bukkit.command.CommandSender;
import types.PluginConfig;
import util.BaseFactionCommand;

public record Create(PluginConfig config) implements BaseFactionCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /f create <factionName>");
            return;
        }
        String factionName = args[0];
        if (factionName.length() < config.minFactionNameLength || factionName.length() > config.maxFactionNameLength) {
            sender.sendMessage(String.format("Faction name must be between %d and %d characters.", config.minFactionNameLength, config.maxFactionNameLength));
            return;
        }
        if (!config.factionNameRegex.matcher(factionName).matches()) {
            sender.sendMessage(String.format("Faction name must match the following regex: %s.", config.factionNameRegex));
            return;
        }
        sender.sendMessage("YAY");
    }
}
