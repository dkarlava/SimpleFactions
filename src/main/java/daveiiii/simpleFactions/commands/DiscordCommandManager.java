package daveiiii.simpleFactions.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import types.PluginConfig;

public record DiscordCommandManager(PluginConfig config) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(Component.text("Usage: /discord", NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!", NamedTextColor.RED));
            return true;
        }

        if (config.discordLink == null) {
            player.sendMessage(Component.text("This server does not have a discord.", NamedTextColor.RED));
            return true;
        }
        Component message = Component.text("Click here to join the discord!", NamedTextColor.GREEN)
            .hoverEvent(HoverEvent.showText(Component.text(config.discordLink)))
            .clickEvent(ClickEvent.openUrl(config.discordLink));
        player.sendMessage(message);
        return true;
    }
}
