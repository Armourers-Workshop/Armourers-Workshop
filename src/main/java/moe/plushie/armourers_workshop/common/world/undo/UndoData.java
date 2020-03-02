package moe.plushie.armourers_workshop.common.world.undo;

import java.awt.Color;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    
    public void undo(World world) {
        if (world.provider.getDimension() != dimensionId) {
            return;
        }
        
        IBlockState state = world.getBlockState(new BlockPos(blockX, blockY, blockZ));
        Block block = state.getBlock();
        if (block instanceof IPantableBlock) {
            Color c = new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF);
            int rgb = c.getRGB();
            
            IPantableBlock worldColourable = (IPantableBlock) block;
            BlockPos pos = new BlockPos(blockX, blockY, blockZ);
            worldColourable.setColour(world, pos, rgb, facing);
            worldColourable.setPaintType(world, pos, PaintTypeRegistry.getInstance().getPaintTypeFormByte(paintType), facing);
        }
    }
}
