package util.factionCommands;

import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

public record Show (PluginConfig config, DataBaseHelper connection, Economy economy) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!"));
            return;
        }

        // TODO: allow show for other factions
        if (args.length > 1) {
            sender.sendMessage("Usage: /f show <faction_name>");
            return;
        }

        Faction factionDetails = null;
        if (args.length == 1) {
            factionDetails = connection.selectFactionByName(args[0]);
        } else {
            UUID playerId = player.getUniqueId();
            factionDetails = connection.selectFactionByPlayerId(playerId);
        }

        if (factionDetails == null) {
            sender.sendMessage(Component.text("__________[", NamedTextColor.GOLD).append(Component.text(" Wilderness ", NamedTextColor.DARK_GREEN)).append(Component.text("]__________", NamedTextColor.GOLD)));
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
            .orElseThrow(() -> new RuntimeException("Faction owner found not found"));

        List<FactionPlayer> factionCoOwners = factionPlayers.stream()
            .filter(fP -> fP.rank == PlayerRank.CoOwner)
            .toList();

        String factionCoOwnersAsString = factionCoOwners.stream()
            .map(fP -> Bukkit.getPlayer(fP.playerId))
            .filter(Objects::nonNull)
            .map(Player::getName)
            .collect(Collectors.joining(", "));

        List<FactionPlayer> factionElders = factionPlayers.stream()
            .filter(fP -> fP.rank == PlayerRank.Elder)
            .toList();

        String factionEldersAsString = factionElders.stream()
            .map(fP -> Bukkit.getPlayer(fP.playerId))
            .filter(Objects::nonNull)
            .map(Player::getName)
            .collect(Collectors.joining(", "));

        List<FactionPlayer> factionMembers = factionPlayers.stream()
            .filter(fP -> fP.rank == PlayerRank.Member)
            .toList();

        String factionMembersAsString = factionMembers.stream()
            .map(fP -> Bukkit.getPlayer(fP.playerId))
            .filter(Objects::nonNull)
            .map(Player::getName)
            .collect(Collectors.joining(", "));

        List<FactionPlayer> offlinePlayers = factionPlayers.stream()
            .filter(fP -> Bukkit.getPlayer(fP.playerId) == null)
            .toList();

        String offlinePlayersAsString = offlinePlayers.stream()
            .map(fP -> Bukkit.getOfflinePlayer(fP.playerId))
            .map(OfflinePlayer::getName)
            .collect(Collectors.joining(", "));

        int maxPossiblePower = factionPlayers.size() * config.factionMaxPowerPerPlayer;

        GetTotalLandData.GetTotalLandDataReturn claimData = GetTotalLandData.run(connection, factionDetails.id);

        OfflinePlayer factionOwnerPlayer = Bukkit.getOfflinePlayer(factionOwner.playerId);
        String factionOwnerPlayerName = factionOwnerPlayer.getName();
        if (factionOwnerPlayerName == null) {
            factionOwnerPlayerName = "Unknown Faction Owner Name";
        }

        double factionBalance = factionPlayers.stream()
            .mapToDouble(fP -> economy.getBalance(Bukkit.getOfflinePlayer(fP.playerId)))
            .sum();
        String factionBalanceFormatted = economy.format(factionBalance);

        player.sendMessage(Component.text("__________[", NamedTextColor.GRAY).append(Component.text(String.format(" %s ", factionDetails.name), NamedTextColor.GREEN)).append(Component.text("]__________", NamedTextColor.GRAY)).append(Component.newline())
            .append(Component.text("Faction Balance: ", NamedTextColor.RED)).append(Component.text(factionBalanceFormatted, NamedTextColor.WHITE)).append(Component.newline())
            .append(Component.text("Owner: ", NamedTextColor.RED)).append(Component.text(factionOwnerPlayerName, NamedTextColor.WHITE)).append(Component.newline())
            .append(Component.text("Land / Power / Max Power: ", NamedTextColor.RED)).append(Component.text(String.format("%d / %d / %d", claimData.totalLand, claimData.totalPower, maxPossiblePower), NamedTextColor.WHITE)).append(Component.newline())
            .append(Component.text("Co-Owners: ", NamedTextColor.RED)).append(Component.text(factionCoOwnersAsString, NamedTextColor.WHITE)).append(Component.newline())
            .append(Component.text("Elders: ", NamedTextColor.RED)).append(Component.text(factionEldersAsString, NamedTextColor.WHITE)).append(Component.newline())
            .append(Component.text("Members: ", NamedTextColor.RED)).append(Component.text(factionMembersAsString, NamedTextColor.WHITE)).append(Component.newline())
            .append(Component.text("Offline: ", NamedTextColor.RED)).append(Component.text(offlinePlayersAsString, NamedTextColor.WHITE)).append(Component.newline())
            .append(Component.text("------------", NamedTextColor.GRAY)).append(Component.text("-".repeat(factionDetails.name.length()), NamedTextColor.GRAY)).append(Component.text("------------", NamedTextColor.GRAY))
        );
    }
}
