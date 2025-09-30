package types;

public enum PlayerRank {
    Owner("owner"),
    CoOwner("co_owner"),
    Elder("elder"),
    Member("member");

    private final String rank;
    PlayerRank (String rank) {
        this.rank = rank;
    }

    public String getRank() {
        return rank;
    }

    public static PlayerRank getValue(String rank) {
        for (PlayerRank possibleRank : PlayerRank.values()) {
            if (possibleRank.getRank().equalsIgnoreCase(rank)) {
                return possibleRank;
            }
        }
        throw new IllegalArgumentException("Invalid command: " + rank);
    }

//    public static String[] getAllPossibleRanks() {
//        return java.util.Arrays.stream(PlayerRank.values())
//                .map(PlayerRank::getRank)
//                .toArray(String[]::new);
//    }
}
