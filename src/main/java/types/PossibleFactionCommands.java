package types;

public enum PossibleFactionCommands {
    Create("create"),
    Join("join");
    private final String command;
    PossibleFactionCommands (String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static PossibleFactionCommands getValue(String command) {
        for (PossibleFactionCommands factionCommand : PossibleFactionCommands.values()) {
            if (factionCommand.getCommand().equalsIgnoreCase(command)) {
                return factionCommand;
            }
        }
        throw new IllegalArgumentException("Invalid command: " + command);
    }

    public static String[] getAllPossibleCommand() {
        return java.util.Arrays.stream(PossibleFactionCommands.values())
                .map(PossibleFactionCommands::getCommand)
                .toArray(String[]::new);
    }
}
