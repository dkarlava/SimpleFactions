package types;

import java.util.UUID;

public class FactionPlayer {
    public PlayerRank rank;
    public String factionId;
    public UUID playerId;
    public FactionPlayer(PlayerRank rank, String factionId, UUID playerId) {
        this.rank = rank;
        this.factionId = factionId;
        this.playerId = playerId;
    }
}
