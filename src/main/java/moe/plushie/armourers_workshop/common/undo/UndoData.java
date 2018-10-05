package moe.plushie.armourers_workshop.common.undo;

import net.minecraft.util.EnumFacing;

public class UndoData {

    public final int blockX;
    public final int blockY;
    public final int blockZ;
    public final int dimensionId;
    public final byte[] rgb;
    public final byte paintType;
    public final EnumFacing facing;

    public UndoData(int blockX, int blockY, int blockZ, int dimensionId, byte[] rgb, byte paintType, EnumFacing facing) {
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.dimensionId = dimensionId;
        this.rgb = rgb;
        this.paintType = paintType;
        this.facing = facing;
    }
}
