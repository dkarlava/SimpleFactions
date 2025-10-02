package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import types.Faction;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;
import util.other.GetTotalLandData;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public record Show (PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }

        // TODO: allow show for other factions
        if (args.length != 0) {
            sender.sendMessage("Usage: /f show");
            return;
        }

        UUID playerId = player.getUniqueId();

        Faction factionDetails = connection.selectFactionByPlayerId(playerId);

        if (factionDetails == null) {
            sender.sendMessage(Component.text("That faction does not exist!",  NamedTextColor.RED));
            return;
        }

        List<FactionPlayer> factionPlayers = connection.selectAllFactionMembersUsingFactionId(factionDetails.id);
        if (factionPlayers == null) {
            sender.sendMessage(Component.text(String.format("Faction %s does not have any members", factionDetails.name),  NamedTextColor.RED));
            return;
        }

        FactionPlayer factionOwner = factionPlayers.stream()
            .filter(fP -> fP.rank == PlayerRank.Owner)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Faction owner found"));

        List<FactionPlayer> factionCoOwners = factionPlayers.stream()
            .filter(fP -> fP.rank == PlayerRank.CoOwner)
            .toList();

        String factionCoOwnersAsString = factionCoOwners.stream()
            .map(fP -> Objects.requireNonNull(Bukkit.getOfflinePlayer(fP.playerId)).getName()) // extract the names
            .collect(Collectors.joining(", "));

        List<FactionPlayer> factionElders = factionPlayers.stream()
            .filter(fP -> fP.rank == PlayerRank.Elder)
            .toList();

        String factionEldersAsString = factionElders.stream()
            .map(fP -> Objects.requireNonNull(Bukkit.getOfflinePlayer(fP.playerId)).getName()) // extract the names
            .collect(Collectors.joining(", "));

        List<FactionPlayer> factionMembers = factionPlayers.stream()
            .filter(fP -> fP.rank == PlayerRank.Member)
            .toList();

        String factionMembersAsString = factionMembers.stream()
            .map(fP -> Objects.requireNonNull(Bukkit.getOfflinePlayer(fP.playerId)).getName()) // extract the names
            .collect(Collectors.joining(", "));

        int maxPossiblePower = factionPlayers.size() * config.factionMaxPowerPerPlayer;

        GetTotalLandData.GetTotalLandDataReturn claimData = GetTotalLandData.run(connection, factionDetails.id);

        // TODO: format this make it centered if possible, add some nice headers
        player.sendMessage(Component.text("-----------------\n", NamedTextColor.GOLD)
            .append(Component.text(String.format("Name: %s\n", factionDetails.name), NamedTextColor.GOLD))
            .append(Component.text(String.format("Owner: %s\n", Objects.requireNonNull(Bukkit.getOfflinePlayer(factionOwner.playerId)).getName()), NamedTextColor.GOLD))
            .append(Component.text(String.format("Co-Owners: %s\n", factionCoOwnersAsString), NamedTextColor.GOLD))
            .append(Component.text(String.format("Elders: %s\n", factionEldersAsString), NamedTextColor.GOLD))
            .append(Component.text(String.format("Members: %s\n", factionMembersAsString), NamedTextColor.GOLD))
            .append(Component.text(String.format("Power / Land / Max Power: %d / %d / %d\n", claimData.totalPower, claimData.totalLand, maxPossiblePower), NamedTextColor.GOLD))
        );
    }
}
