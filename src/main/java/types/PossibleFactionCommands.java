package types;

public enum PossibleFactionCommands {
    Create("create"),
    Join("join"),
    List("list"),
    Leave("leave"),
    Show("show"),
    Disband("disband"),
    Invite("invite"),
    Kick("kick"),
    Power("power"),
    Map("map"),
    Claim("claim"),
    Unclaim("unclaim"),
    SetPower("setpower"),
    SetHome("sethome"),
    Home("home");
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
