package types;

public class FactionHome {
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public FactionHome (double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
