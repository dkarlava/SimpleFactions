package types;

import java.util.UUID;

public class FactionInvite {
    public UUID playerId;
    public long timestamp;
    public String factionInviteId;
    public FactionInvite (String playerId, long timestamp, String factionInviteId) {
        this.playerId = UUID.fromString(playerId);
        this.timestamp = timestamp;
        this.factionInviteId = factionInviteId;
    }
}
