package types;

public class FactionChunk {
    public String factionId;
    public int x;
    public int z;
    public FactionChunk (String factionId, int x, int z) {
        this.factionId = factionId;
        this.x = x;
        this.z = z;
    }
}
