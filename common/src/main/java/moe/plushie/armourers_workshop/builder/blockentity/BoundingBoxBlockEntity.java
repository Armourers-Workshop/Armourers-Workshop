package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.client.IExtendedBlockEntityRenderer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.builder.block.ArmourerBlock;
import moe.plushie.armourers_workshop.builder.data.BoundingBox;
import moe.plushie.armourers_workshop.core.blockentity.AbstractBlockEntity;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;
import java.util.Map;

public class BoundingBoxBlockEntity extends AbstractBlockEntity implements IPaintable, IExtendedBlockEntityRenderer {

    protected static final BlockPos INVALID = BlockPos.of(-1);

    protected Vector3i guide = Vector3i.ZERO;
    protected BlockPos parent = INVALID;

    protected ISkinPartType partType = SkinPartTypes.UNKNOWN;

    private ArmourerBlockEntity cachedParentBlockEntity;
    private boolean customRenderer = false;

    public BoundingBoxBlockEntity(BlockEntityType<?> entityType) {
        super(entityType);
    }

    public void readFromNBT(CompoundTag nbt) {
        parent = DataSerializers.getBlockPos(nbt, Constants.Key.TILE_ENTITY_REFER, INVALID);
        guide = DataSerializers.getVector3i(nbt, Constants.Key.TILE_ENTITY_OFFSET);
        partType = SkinPartTypes.byName(DataSerializers.getString(nbt, Constants.Key.SKIN_PART_TYPE, SkinTypes.UNKNOWN.getRegistryName().toString()));
        customRenderer = Arrays.stream(Direction.values()).anyMatch(this::shouldChangeColor);
        cachedParentBlockEntity = null;
    }

    public void writeToNBT(CompoundTag nbt) {
        DataSerializers.putBlockPos(nbt, Constants.Key.TILE_ENTITY_REFER, parent, INVALID);
        DataSerializers.putVector3i(nbt, Constants.Key.TILE_ENTITY_OFFSET, guide);
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
        ArmourerBlockEntity blockEntity = getParentTileEntity();
        if (blockEntity != null && blockEntity.getSkinType() != null) {
            return blockEntity.getSkinType().getParts().contains(partType);
        }
        return false;
    }

    public boolean hasColors() {
        ArmourerBlockEntity tileEntity = getParentTileEntity();
        if (tileEntity == null) {
            return false;
        }
        for (Direction dir : Direction.values()) {
            IPaintColor paintColor = getArmourerTextureColor(tileEntity, getTexturePos(tileEntity, dir));
            if (paintColor != PaintColor.CLEAR) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldChangeColor(Direction direction) {
        // we can't change the side color of the face without finding the texture.
        return getTexturePos(getParentTileEntity(), direction) != null;
    }

    @Override
    public IPaintColor getColor(Direction direction) {
        ArmourerBlockEntity tileEntity = getParentTileEntity();
        TexturePos texturePos = getTexturePos(tileEntity, direction);
        IPaintColor color = getArmourerTextureColor(tileEntity, texturePos);
        if (color != null && color.getPaintType() != SkinPaintTypes.NONE) {
            return color;
        }
        // when work in the client side, we try to get the texture color from the loaded texture.
        if (level != null && level.isClientSide()) {
            return getTextureColor(tileEntity, texturePos);
        }
        return PaintColor.CLEAR;
    }

    @Override
    public void setColor(Direction direction, IPaintColor color) {
        // ?
    }

    @Override
    public void setColors(Map<Direction, IPaintColor> colors) {
        ArmourerBlockEntity tileEntity = getParentTileEntity();
        colors.forEach((dir, color) -> setArmourerTextureColor(tileEntity, getTexturePos(tileEntity, dir), color));
    }

    public void clearArmourerTextureColors() {
        ArmourerBlockEntity tileEntity = getParentTileEntity();
        if (tileEntity == null || level == null) {
            return;
        }
        for (Direction dir : Direction.values()) {
            this.setArmourerTextureColor(tileEntity, getTexturePos(tileEntity, dir), PaintColor.CLEAR);
        }
    }

    public IPaintColor getArmourerTextureColor(ArmourerBlockEntity tileEntity, TexturePos texturePos) {
        if (texturePos != null && tileEntity != null) {
            IPaintColor color = tileEntity.getPaintColor(texturePos);
            if (color != null) {
                return color;
            }
        }
        return PaintColor.CLEAR;
    }

    public void setArmourerTextureColor(ArmourerBlockEntity tileEntity, TexturePos texturePos, IPaintColor color) {
        if (texturePos != null && tileEntity != null) {
            tileEntity.setPaintColor(texturePos, color);
            BlockUtils.combine(tileEntity, tileEntity::sendBlockUpdates);
        }
    }

    @Environment(value = EnvType.CLIENT)
    private IPaintColor getTextureColor(ArmourerBlockEntity tileEntity, TexturePos texturePos) {
        if (texturePos != null && tileEntity != null) {
            IPaintColor color = TextureUtils.getPlayerTextureModelColor(tileEntity.getTextureDescriptor(), texturePos);
            if (color != null) {
                return color;
            }
        }
        return PaintColor.CLEAR;
    }

    private TexturePos getTexturePos(ArmourerBlockEntity tileEntity, Direction direction) {
        return BoundingBox.getTexturePos(partType, guide, getResolvedDirection(tileEntity, direction));
    }

    private Direction getResolvedDirection(ArmourerBlockEntity tileEntity, Direction dir) {
        if (tileEntity == null) {
            return dir;
        }
        switch (tileEntity.getBlockState().getValue(ArmourerBlock.FACING)) {
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

    private ArmourerBlockEntity getParentTileEntity() {
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
        BlockEntity tileEntity = level.getBlockEntity(getBlockPos().subtract(parent));
        if (tileEntity instanceof ArmourerBlockEntity) {
            cachedParentBlockEntity = (ArmourerBlockEntity) tileEntity;
            return cachedParentBlockEntity;
        }
        return null;
    }

    @Override
    public boolean shouldUseExtendedRenderer() {
        // if the parent entity is missing, do not render it.
        if (!isValid()) {
            return false;
        }
        return customRenderer;
    }
}
