package types;

public class FactionPlayer {
    public PlayerRank rank;
    public String factionId;
    public FactionPlayer(PlayerRank rank, String factionId) {
        this.rank = rank;
        this.factionId = factionId;
    }
}
