package types;

import java.util.UUID;

public class FactionDisband {
    public UUID playerId;
    public long timestamp;
    public String factionDisbandId;
    public FactionDisband (String playerId, long timestamp, String factionDisbandId) {
        this.playerId = UUID.fromString(playerId);
        this.timestamp = timestamp;
        this.factionDisbandId = factionDisbandId;
    }
}
