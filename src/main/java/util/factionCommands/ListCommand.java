package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import types.Faction;
import types.PluginConfig;
import util.BaseFactionCommand;

import java.sql.SQLException;
import java.util.List;

public record ListCommand(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (args.length > 1) {
            sender.sendMessage("Usage: /f list <pageNumber>");
            return;
        }
        try {
            int pageNumber = 1;
            if  (args.length == 1) {
                pageNumber = Integer.parseInt(args[0]);
            }
            int maxPageNumber = connection.selectTotalFactionPageNumber();
            if (pageNumber <= 0 || pageNumber > maxPageNumber) {
                sender.sendMessage(String.format("Invalid page number. Please use a value between 1 and %d", maxPageNumber));
                return;
            }
            List<Faction> factions = connection.selectFactionPage(pageNumber);

            sender.sendMessage(Component.text(String.format("=== Factions Page %d/%d ===", pageNumber, maxPageNumber)).color(NamedTextColor.GOLD));
            for (int i = 0; i < factions.size(); i++) {
                Faction f = factions.get(i);
                sender.sendMessage(Component.text(String.format("[ %d ]. %s", i + 1, f.name)).color(NamedTextColor.GREEN));
            }
            sender.sendMessage(Component.text("=======================").color(NamedTextColor.GOLD));
        } catch (NumberFormatException e) {
            sender.sendMessage("Please enter a valid number");
        }
    }
}
