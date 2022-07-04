package moe.plushie.armourers_workshop.builder.tileentity;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.block.ArmourerBlock;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.tileentity.AbstractTileEntity;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import moe.plushie.armourers_workshop.utils.color.BlockPaintColor;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

import java.util.Map;

@SuppressWarnings("NullableProblems")
public class SkinCubeTileEntity extends AbstractTileEntity implements IPaintable {

    protected BlockPaintColor colors = new BlockPaintColor();
    protected boolean customRenderer = false;

    public SkinCubeTileEntity() {
        super(ModTileEntities.SKIN_CUBE);
    }

    public void readFromNBT(CompoundNBT nbt) {
        colors.deserializeNBT(nbt.getCompound(AWConstants.NBT.COLOR));
        customRenderer = checkRendererFromColors();
    }

    public void writeToNBT(CompoundNBT nbt) {
        nbt.put(AWConstants.NBT.COLOR, colors.serializeNBT());
//        // we must need to tracking the facing at the save it,
//        // because we need to get the colors based facing from copied NBT.
//        // we can know the direction has been changed when the load copied NBT.
//        nbt.putString(AWConstants.NBT.FACING, getDirection().name());
    }

    private boolean checkRendererFromColors() {
        for (IPaintColor color : colors.values()) {
            if (color.getPaintType() != SkinPaintTypes.NORMAL) {
                return true;
            }
        }
        return false;
    }

    private BlockPaintColor.Side getSide(Direction dir) {
        switch (getDirection()) {
            case SOUTH: {
                // when block facing to south, we need to rotate 180° get facing north direction.
                return BlockPaintColor.Side.of(Rotation.CLOCKWISE_180.rotate(dir));
            }
            case WEST: {
                // when block facing to west, we need to rotate 90° get facing north direction.
                return BlockPaintColor.Side.of(Rotation.CLOCKWISE_90.rotate(dir));
            }
            case EAST: {
                // when block facing to east, we need to rotate -90° get facing north direction.
                return BlockPaintColor.Side.of(Rotation.COUNTERCLOCKWISE_90.rotate(dir));
            }
            default: {
                return BlockPaintColor.Side.of(dir);
            }
        }
    }

    @Override
    public IPaintColor getColor(Direction direction) {
        return colors.getOrDefault(getSide(direction), PaintColor.WHITE);
    }

    @Override
    public void setColor(Direction direction, IPaintColor color) {
        this.colors.put(getSide(direction), color);
        this.customRenderer = checkRendererFromColors();
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    @Override
    public void setColors(Map<Direction, IPaintColor> colors) {
        colors.forEach((direction, color) -> this.colors.put(getSide(direction), color));
        this.customRenderer = checkRendererFromColors();
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    public Direction getDirection() {
        return getBlockState().getValue(ArmourerBlock.FACING);
    }

    @Override
    public TileEntityType<?> getType() {
        if (customRenderer) {
            return ModTileEntities.SKIN_CUBE_SR;
        }
        return super.getType();
    }
}