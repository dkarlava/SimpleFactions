package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;
import util.other.CompareRanks;

import java.sql.SQLException;
import java.util.UUID;

public record Demote(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("Usage: /f demote <factionMember>");
            return;
        }

        UUID playerId = player.getUniqueId();
        FactionPlayer factionPlayer = connection.selectFactionPlayerMember(playerId);
        if (factionPlayer == null) {
            sender.sendMessage(Component.text("You must be in a faction to use this command.", NamedTextColor.RED));
            return;
        }

        Player otherPlayer = Bukkit.getPlayer(args[0]);
        if (otherPlayer == null) {
            sender.sendMessage(Component.text("That player is either not online or doesnt exist.", NamedTextColor.RED));
            return;
        }

        FactionPlayer otherFactionPlayer = connection.selectFactionPlayerMember(otherPlayer.getUniqueId());
        if (otherFactionPlayer == null || !otherFactionPlayer.factionId.equals(factionPlayer.factionId)) {
            sender.sendMessage(Component.text("That player is not in your faction.", NamedTextColor.RED));
            return;
        }

        if (CompareRanks.compareRanks(factionPlayer.rank, otherFactionPlayer.rank)) {
            if (otherFactionPlayer.rank == PlayerRank.CoOwner) {
                connection.updateFactionMemberRank(otherPlayer.getUniqueId(), PlayerRank.Elder);
                player.sendMessage(Component.text(String.format("%s has been demoted to Elder!", otherPlayer.getName()), NamedTextColor.RED));
                otherPlayer.sendMessage(Component.text("You have been demoted to Elder!", NamedTextColor.RED));
            } else if (otherFactionPlayer.rank == PlayerRank.Elder) {
                connection.updateFactionMemberRank(otherPlayer.getUniqueId(), PlayerRank.Member);
                player.sendMessage(Component.text(String.format("%s has been demoted to Member!", otherPlayer.getName()), NamedTextColor.RED));
                otherPlayer.sendMessage(Component.text("You have been demoted to Member!", NamedTextColor.RED));
            }
        } else {
            player.sendMessage(Component.text("You are not a high enough rank to demote that player.", NamedTextColor.RED));
        }
    }
}
