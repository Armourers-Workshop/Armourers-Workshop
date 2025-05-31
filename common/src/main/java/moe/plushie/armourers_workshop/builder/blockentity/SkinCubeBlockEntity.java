package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.builder.block.ArmourerBlock;
import moe.plushie.armourers_workshop.builder.other.BlockUtils;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class SkinCubeBlockEntity extends UpdatableBlockEntity implements IBlockPaintable {

    protected BlockPaintColor colors = new BlockPaintColor();
    protected boolean customRenderer = false;

    public SkinCubeBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        colors = serializer.read(CodingKeys.COLORS);
        customRenderer = checkRendererFromColors();
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        serializer.write(CodingKeys.COLORS, colors);
//        // we must need to tracking the facing at the save it,
//        // because we need to get the colors based facing from copied NBT.
//        // we can know the direction has been changed when the load copied NBT.
//        nbt.putString(Constants.NBT.FACING, getDirection().name());
    }

    private boolean checkRendererFromColors() {
        for (var color : colors.values()) {
            if (color.getPaintType() != SkinPaintTypes.NORMAL) {
                return true;
            }
        }
        return false;
    }

    private OpenDirection getResolvedDirection(OpenDirection dir) {
        return switch (getDirection()) {
            case SOUTH -> OpenRotation.CLOCKWISE_180.rotate(dir); // rotate 180° get facing north direction.
            case WEST -> OpenRotation.CLOCKWISE_90.rotate(dir); // rotate 90° get facing north direction.
            case EAST -> OpenRotation.COUNTERCLOCKWISE_90.rotate(dir);// rotate -90° get facing north direction.
            default -> dir;
        };
    }

    @Override
    public SkinPaintColor getColor(OpenDirection direction) {
        return colors.getOrDefault(getResolvedDirection(direction), SkinPaintColor.WHITE);
    }

    @Override
    public void setColor(OpenDirection direction, SkinPaintColor color) {
        this.colors.put(getResolvedDirection(direction), color);
        this.customRenderer = checkRendererFromColors();
        BlockUtils.combine(this, this::sendBlockUpdates);
    }

    @Override
    public void setColors(Map<OpenDirection, SkinPaintColor> colors) {
        colors.forEach((direction, color) -> this.colors.put(getResolvedDirection(direction), color));
        this.customRenderer = checkRendererFromColors();
        BlockUtils.combine(this, this::sendBlockUpdates);
    }

    public Direction getDirection() {
        return getBlockState().getOptionalValue(ArmourerBlock.FACING).orElse(Direction.NORTH);
    }


    public boolean isCustomRenderer() {
        return customRenderer;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<BlockPaintColor> COLORS = IDataSerializerKey.create("Color", BlockPaintColor.CODEC, BlockPaintColor.WHITE, BlockPaintColor.WHITE::copy);
    }
}
