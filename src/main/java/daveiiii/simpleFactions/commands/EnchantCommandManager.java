package daveiiii.simpleFactions.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import types.PluginConfig;

public record EnchantCommandManager(PluginConfig config) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /setenchant <CustomItemName>", NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can execute this command!", NamedTextColor.RED));
            return true;
        }

        if (!player.isOp()) {
            sender.sendMessage(Component.text("You do not have permission to run this command!", NamedTextColor.RED));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must be holding an item!", NamedTextColor.RED));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return true;
        }

        String itemName = String.join(" ", args);
        meta.displayName(Component.text(itemName));
        meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
        return true;
    }
}
