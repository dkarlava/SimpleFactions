package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.PluginConfig;
import util.BaseFactionCommand;
import java.sql.SQLException;

public record SetPower(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            return;
        }

        if (!player.isOp()) {
            sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
        }

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /f setpower <player> <amount>", NamedTextColor.RED));
            return;
        }

        String playerName = args[0];
        Player selectedPlayer = Bukkit.getPlayer(playerName);
        if (selectedPlayer == null) {
            sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(Component.text("Usage: /f setpower <player> <amount>", NamedTextColor.RED));
            return;
        }

        if (amount < config.factionMinPowerPerPlayer ||  amount > config.factionMaxPowerPerPlayer) {
            sender.sendMessage(Component.text("Usage: /f setpower <player> <amount>", NamedTextColor.RED));
            return;
        }
        connection.updatePlayerPower(selectedPlayer.getUniqueId(), amount);
    }
}