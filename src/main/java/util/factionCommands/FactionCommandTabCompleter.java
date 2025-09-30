package util.factionCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import types.PossibleFactionCommands;

import java.util.Arrays;
import java.util.List;

public class FactionCommandTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) {
            return Arrays.asList(PossibleFactionCommands.getAllPossibleCommand());
        }
        return List.of();
    }
}