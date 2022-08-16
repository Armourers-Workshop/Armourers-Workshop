package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.client.IBlockEntityExtendedRenderer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.block.ArmourerBlock;
import moe.plushie.armourers_workshop.core.blockentity.AbstractBlockEntity;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Map;

public class SkinCubeBlockEntity extends AbstractBlockEntity implements IPaintable, IBlockEntityExtendedRenderer {

    protected BlockPaintColor colors = new BlockPaintColor(PaintColor.WHITE);
    protected boolean customRenderer = false;

    public SkinCubeBlockEntity(BlockEntityType<?> entityType) {
        super(entityType);
    }

    public void readFromNBT(CompoundTag nbt) {
        colors.deserializeNBT(nbt.getCompound(Constants.Key.COLOR));
        customRenderer = checkRendererFromColors();
    }

    public void writeToNBT(CompoundTag nbt) {
        nbt.put(Constants.Key.COLOR, colors.serializeNBT());
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

    private Direction getResolvedDirection(Direction dir) {
        switch (getDirection()) {
            case SOUTH: {
                // when block facing to south, we need to rotate 180° get facing north direction.
                return Rotation.CLOCKWISE_180.rotate(dir);
            }
            case WEST: {
                // when block facing to west, we need to rotate 90° get facing north direction.
                return Rotation.CLOCKWISE_90.rotate(dir);
            }
            case EAST: {
                // when block facing to east, we need to rotate -90° get facing north direction.
                return Rotation.COUNTERCLOCKWISE_90.rotate(dir);
            }
            default: {
                return dir;
            }
        }
    }

    @Override
    public IPaintColor getColor(Direction direction) {
        return colors.getOrDefault(getResolvedDirection(direction), PaintColor.WHITE);
    }

    @Override
    public void setColor(Direction direction, IPaintColor color) {
        this.colors.put(getResolvedDirection(direction), color);
        this.customRenderer = checkRendererFromColors();
        BlockUtils.combine(this, this::sendBlockUpdates);
    }

    @Override
    public void setColors(Map<Direction, IPaintColor> colors) {
        colors.forEach((direction, color) -> this.colors.put(getResolvedDirection(direction), color));
        this.customRenderer = checkRendererFromColors();
        BlockUtils.combine(this, this::sendBlockUpdates);
    }

    public Direction getDirection() {
        return getBlockState().getOptionalValue(ArmourerBlock.FACING).orElse(Direction.NORTH);
    }

    @Override
    public boolean shouldUseExtendedRenderer() {
        return customRenderer;
    }
}
