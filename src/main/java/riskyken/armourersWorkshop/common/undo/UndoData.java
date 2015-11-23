package riskyken.armourersWorkshop.common.undo;

public class UndoData {

    public final int blockX;
    public final int blockY;
    public final int blockZ;
    public final int dimensionId;
    public final byte[] rgb;
    public final byte paintType;
    public final int side;

    public UndoData(int blockX, int blockY, int blockZ, int dimensionId, byte[] rgb, byte paintType, int side) {
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.dimensionId = dimensionId;
        this.rgb = rgb;
        this.paintType = paintType;
        this.side = side;
    }
}
