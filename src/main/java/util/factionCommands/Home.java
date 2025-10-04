package util.factionCommands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.api.IAsyncTeleport;
import daveiiii.simpleFactions.data.DataBaseHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import types.FactionHome;
import types.FactionPlayer;
import types.PlayerRank;
import types.PluginConfig;
import util.BaseFactionCommand;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public record Home(PluginConfig config, DataBaseHelper connection) implements BaseFactionCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            return;
        }
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");
        if (!(plugin instanceof Essentials essentials)) {
            player.sendMessage(Component.text("EssentialsX is not installed!",  NamedTextColor.RED));
            return;
        }

        if (args.length != 0) {
            sender.sendMessage(Component.text("Usage: /f home", NamedTextColor.RED));
            return;
        }

        FactionPlayer factionPlayer =  connection.selectFactionPlayerMember(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.factionId == null) {
            player.sendMessage(Component.text("You must be in a faction to run this command.", NamedTextColor.RED));
            return;
        }

        FactionHome factionHome = connection.selectFactionHome(factionPlayer.factionId);
        // if the faction home is not set then the nulls get treated as zeros. Checking that here
        if (factionHome == null || (factionHome.x == 0 && factionHome.z == 0)) {
            player.sendMessage(Component.text("Your faction does not have a faction home.",  NamedTextColor.RED));
            return;
        }

        if (factionPlayer.rank == PlayerRank.Member) {
            player.sendMessage(Component.text("Members cannot visit the faction home.", NamedTextColor.RED));
            return;
        }

        User user = essentials.getUser(player.getUniqueId());
        IAsyncTeleport asyncTeleport = user.getAsyncTeleport();
        Location loc = new Location(player.getWorld(), factionHome.x, factionHome.y, factionHome.z, factionHome.yaw, factionHome.pitch);
        asyncTeleport.teleport(loc, null, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());
    }
}