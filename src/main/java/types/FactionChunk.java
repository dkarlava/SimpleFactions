package types;

public class FactionChunk {
    public String factionId;
    public String factionName;
    public int x;
    public int z;
    public FactionChunk (String factionId, int x, int z, String factionName) {
        this.factionId = factionId;
        this.x = x;
        this.z = z;
        this.factionName = factionName;
    }
}
