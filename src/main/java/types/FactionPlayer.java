package types;

import java.util.UUID;

public class FactionPlayer {
    public int power;
    public UUID playerId;
    public PlayerRank rank;
    public String factionId;
    public FactionPlayer(PlayerRank rank, String factionId, UUID playerId, int power) {
        this.rank = rank;
        this.power = power;
        this.playerId = playerId;
        this.factionId = factionId;
    }
}
