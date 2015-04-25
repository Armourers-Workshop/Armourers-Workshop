package riskyken.armourersWorkshop.common.undo;

public class UndoData {

    public final int blockX;
    public final int blockY;
    public final int blockZ;
    public final int dimensionId;
    public final int colour;
    public final int side;

    public UndoData(int blockX, int blockY, int blockZ, int dimensionId, int colour, int side) {
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.dimensionId = dimensionId;
        this.colour = colour;
        this.side = side;
    }
}
