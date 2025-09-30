package types;

import java.util.UUID;

public class PlayerData {
    public int power;
    public UUID playerId;
    public PlayerData (UUID playerId, int power) {
        this.playerId = playerId;
        this.power = power;
    }
}
