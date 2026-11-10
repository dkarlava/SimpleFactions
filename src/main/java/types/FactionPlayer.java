package types;

import java.util.UUID;

public class FactionPlayer {
    public int power;
    public UUID playerId;
    public PlayerRank rank;
    public String factionId;
    public String factionName;
    public FactionPlayer(PlayerRank rank, String factionId, UUID playerId, int power, String factionName) {
        this.rank = rank;
        this.power = power;
        this.playerId = playerId;
        this.factionId = factionId;
        this.factionName = factionName;
    }
}
