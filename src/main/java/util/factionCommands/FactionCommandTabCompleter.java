package util.factionCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import types.PossibleFactionCommands;

import java.util.Arrays;
import java.util.List;

public class FactionCommandTabCompleter implements TabCompleter {

    private static final List<String> playerCommands = List.of(
            "invite",
            "kick"
    );

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList(PossibleFactionCommands.getAllPossibleCommand());
        }

        if (args.length == 2) {
            if (playerCommands.contains(args[0].toLowerCase())) {
                String input = args[1].toLowerCase();

                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
            }
        }

        return List.of();
    }
}