package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.client.IBlockEntityExtendedRenderer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.builder.data.BoundingBox;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TextureUtils;
import moe.plushie.armourers_workshop.utils.math.TexturePos;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.Map;

public class BoundingBoxBlockEntity extends UpdatableBlockEntity implements IPaintable, IBlockEntityExtendedRenderer {

    protected static final BlockPos INVALID = BlockPos.of(-1);

    protected Vector3i guide = Vector3i.ZERO;
    protected BlockPos parent = INVALID;

    protected ISkinPartType partType = SkinPartTypes.UNKNOWN;

    private ArmourerBlockEntity cachedParentBlockEntity;
    private boolean customRenderer = false;

    public BoundingBoxBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void readFromNBT(CompoundTag nbt) {
        parent = DataSerializers.getBlockPos(nbt, Constants.Key.BLOCK_ENTITY_REFER, INVALID);
        guide = DataSerializers.getVector3i(nbt, Constants.Key.BLOCK_ENTITY_OFFSET);
        partType = SkinPartTypes.byName(DataSerializers.getString(nbt, Constants.Key.SKIN_PART_TYPE, SkinTypes.UNKNOWN.getRegistryName().toString()));
        customRenderer = Arrays.stream(Direction.values()).anyMatch(this::shouldChangeColor);
        cachedParentBlockEntity = null;
    }

    public void writeToNBT(CompoundTag nbt) {
        DataSerializers.putBlockPos(nbt, Constants.Key.BLOCK_ENTITY_REFER, parent, INVALID);
        DataSerializers.putVector3i(nbt, Constants.Key.BLOCK_ENTITY_OFFSET, guide);
        DataSerializers.putString(nbt, Constants.Key.SKIN_PART_TYPE, partType.getRegistryName().toString(), SkinTypes.UNKNOWN.getRegistryName().toString());
    }

    public ISkinPartType getPartType() {
        return partType;
    }

    public void setPartType(ISkinPartType partType) {
        this.partType = partType;
    }

    public BlockPos getParent() {
        return parent;
    }

    public void setParent(BlockPos parent) {
        this.cachedParentBlockEntity = null;
        this.parent = parent;
    }

    public Vector3i getGuide() {
        return guide;
    }

    public void setGuide(Vector3i guide) {
        this.guide = guide;
    }

    public boolean isValid() {
        ArmourerBlockEntity blockEntity = getParentBlockEntity();
        if (blockEntity != null && blockEntity.getSkinType() != null) {
            return blockEntity.getSkinType().getParts().contains(partType);
        }
        return false;
    }

    public boolean hasColors() {
        ArmourerBlockEntity blockEntity = getParentBlockEntity();
        if (blockEntity == null) {
            return false;
        }
        for (Direction dir : Direction.values()) {
            IPaintColor paintColor = getArmourerTextureColor(blockEntity, getTexturePos(blockEntity, dir));
            if (paintColor != PaintColor.CLEAR) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldChangeColor(Direction direction) {
        // we can't change the side color of the face without finding the texture.
        return getTexturePos(getParentBlockEntity(), direction) != null;
    }

    @Override
    public IPaintColor getColor(Direction direction) {
        ArmourerBlockEntity blockEntity = getParentBlockEntity();
        TexturePos texturePos = getTexturePos(blockEntity, direction);
        IPaintColor color = getArmourerTextureColor(blockEntity, texturePos);
        if (color != null && color.getPaintType() != SkinPaintTypes.NONE) {
            return color;
        }
        // when work in the client side, we try to get the texture color from the loaded texture.
        Level level = getLevel();
        if (level != null && level.isClientSide()) {
            return getTextureColor(blockEntity, texturePos);
        }
        return PaintColor.CLEAR;
    }

    @Override
    public void setColor(Direction direction, IPaintColor color) {
        // ?
    }

    @Override
    public void setColors(Map<Direction, IPaintColor> colors) {
        ArmourerBlockEntity blockEntity = getParentBlockEntity();
        colors.forEach((dir, color) -> setArmourerTextureColor(blockEntity, getTexturePos(blockEntity, dir), color));
    }

    @Override
    public boolean hasColor(Direction direction) {
        // bounding box can't support none paint type.
        return getColor(direction) != PaintColor.CLEAR;
    }

    public void clearArmourerTextureColors() {
        ArmourerBlockEntity blockEntity = getParentBlockEntity();
        if (blockEntity == null || getLevel() == null) {
            return;
        }
        for (Direction dir : Direction.values()) {
            this.setArmourerTextureColor(blockEntity, getTexturePos(blockEntity, dir), PaintColor.CLEAR);
        }
    }

    public IPaintColor getArmourerTextureColor(ArmourerBlockEntity blockEntity, TexturePos texturePos) {
        if (texturePos != null && blockEntity != null) {
            IPaintColor color = blockEntity.getPaintColor(texturePos);
            if (color != null) {
                return color;
            }
        }
        return PaintColor.CLEAR;
    }

    public void setArmourerTextureColor(ArmourerBlockEntity blockEntity, TexturePos texturePos, IPaintColor color) {
        if (texturePos != null && blockEntity != null) {
            blockEntity.setPaintColor(texturePos, color);
            BlockUtils.combine(blockEntity, blockEntity::sendBlockUpdates);
        }
    }

    @Environment(EnvType.CLIENT)
    private IPaintColor getTextureColor(ArmourerBlockEntity blockEntity, TexturePos texturePos) {
        if (texturePos != null && blockEntity != null) {
            IPaintColor color = TextureUtils.getPlayerTextureModelColor(blockEntity.getTextureDescriptor(), texturePos);
            if (color != null) {
                return color;
            }
        }
        return PaintColor.CLEAR;
    }

    private TexturePos getTexturePos(ArmourerBlockEntity blockEntity, Direction direction) {
        return BoundingBox.getTexturePos(partType, guide, getResolvedDirection(blockEntity, direction));
    }

    private Direction getResolvedDirection(ArmourerBlockEntity blockEntity, Direction dir) {
        if (blockEntity == null) {
            return dir;
        }
        switch (blockEntity.getFacing()) {
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

    private ArmourerBlockEntity getParentBlockEntity() {
        // quickly query the parent block.
        if (cachedParentBlockEntity != null) {
            if (cachedParentBlockEntity.isRemoved()) {
                return null;
            }
            return cachedParentBlockEntity;
        }
        Level level = getLevel();
        if (level == null || parent.equals(INVALID)) {
            return null;
        }
        BlockPos target = getBlockPos().subtract(parent);
        ArmourerBlockEntity blockEntity = ObjectUtils.safeCast(level.getBlockEntity(target), ArmourerBlockEntity.class);
        if (blockEntity != null) {
            cachedParentBlockEntity = blockEntity;
            return cachedParentBlockEntity;
        }
        return null;
    }

    @Override
    public boolean shouldUseExtendedRenderer() {
        // if the parent entity is missing, do not render it.
        if (customRenderer) {
            return isValid();
        }
        return false;
    }
}
