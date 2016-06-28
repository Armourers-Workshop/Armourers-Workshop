package riskyken.armourersWorkshop.common.undo;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class UndoData {

    public final BlockPos pos;
    public final int dimensionId;
    public final byte[] rgb;
    public final byte paintType;
    public final EnumFacing side;

    public UndoData(BlockPos pos, int dimensionId, byte[] rgb, byte paintType, EnumFacing side) {
        this.pos = pos;
        this.dimensionId = dimensionId;
        this.rgb = rgb;
        this.paintType = paintType;
        this.side = side;
    }
}
