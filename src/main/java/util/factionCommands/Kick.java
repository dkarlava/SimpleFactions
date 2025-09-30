package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionPlayer;
import types.PluginConfig;
import util.BaseFactionCommand;
import util.other.BroadcastMessageToFactionMembers;
import util.other.CompareRanks;
import java.sql.SQLException;
import java.util.UUID;

public record Kick(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("Usage: /f kick <playerName>");
            return;
        }
        String otherPlayerName = args[0];
        UUID playerId = player.getUniqueId();
        Player otherPlayer = Bukkit.getPlayer(otherPlayerName);
        if (otherPlayer == null) {
            // TODO: Error?
            return;
        }

        UUID otherPlayerId = otherPlayer.getUniqueId();
        FactionPlayer factionPlayer = connection.selectFactionPlayerMember(playerId);

        if (factionPlayer == null) {
            sender.sendMessage(Component.text("You must be in a faction to kick someone.").color(NamedTextColor.RED));
            return;
        }

        FactionPlayer otherFactionPlayer = connection.selectFactionPlayerMember(otherPlayerId);
        if (otherFactionPlayer == null) {
            sender.sendMessage(Component.text("That player is not in a faction.").color(NamedTextColor.RED));
            return;
        }

        if (!factionPlayer.factionId.equals(otherFactionPlayer.factionId)) {
            sender.sendMessage(Component.text("You and that player are not in the same faction.").color(NamedTextColor.RED));
            return;
        }

        // returns true only if the first rank given is HIGHER than the second rank
        if (CompareRanks.compareRanks(factionPlayer.rank, otherFactionPlayer.rank)) {
            connection.deleteFactionMember(otherPlayerId);
            otherPlayer.sendMessage(Component.text("You have been kicked from the faction!", NamedTextColor.RED));
            BroadcastMessageToFactionMembers.run(connection, factionPlayer.factionId, Component.text(String.format("%s has been kicked from the faction!", otherPlayerName), NamedTextColor.RED));
        } else {
            sender.sendMessage(Component.text("You do not have the power to kick that player.", NamedTextColor.RED));
        }
    }
}