package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.builder.other.BlockUtils;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.client.texture.PlayerSkinBakery;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenRotation;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class BoundingBoxBlockEntity extends UpdatableBlockEntity implements IBlockPaintable {

    protected OpenVector3i guide = OpenVector3i.ZERO;
    protected BlockPos parent = null;

    protected SkinPartType partType = SkinPartTypes.UNKNOWN;

    private ArmourerBlockEntity cachedParentBlockEntity;
    private boolean customRenderer = false;

    public BoundingBoxBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void abi$readAdditionalData(IDataSerializer serializer) {
        parent = serializer.read(CodingKeys.REFER);
        guide = serializer.read(CodingKeys.OFFSET);
        partType = serializer.read(CodingKeys.PART_TYPE);
        customRenderer = Arrays.stream(OpenDirection.values()).anyMatch(this::shouldChangeColor);
        cachedParentBlockEntity = null;
    }

    @Override
    protected void abi$writeAdditionalData(IDataSerializer serializer) {
        serializer.write(CodingKeys.REFER, parent);
        serializer.write(CodingKeys.OFFSET, guide);
        serializer.write(CodingKeys.PART_TYPE, partType);
    }

    public SkinPartType partType() {
        return partType;
    }

    public void setPartType(SkinPartType partType) {
        this.partType = partType;
    }

    public BlockPos parent() {
        return parent;
    }

    public void setParent(BlockPos parent) {
        this.cachedParentBlockEntity = null;
        this.parent = parent;
    }

    public OpenVector3i guide() {
        return guide;
    }

    public void setGuide(OpenVector3i guide) {
        this.guide = guide;
    }

    public boolean isValid() {
        var blockEntity = parentBlockEntity();
        if (blockEntity != null && blockEntity.skinType() != null) {
            return blockEntity.skinType().parts().contains(partType);
        }
        return false;
    }

    public boolean hasColors() {
        var blockEntity = parentBlockEntity();
        if (blockEntity == null) {
            return false;
        }
        for (var dir : OpenDirection.values()) {
            var paintColor = getArmourerTextureColor(blockEntity, getTexturePos(blockEntity, dir));
            if (paintColor != SkinPaintColor.CLEAR) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldChangeColor(OpenDirection direction) {
        // we can't change the side color of the face without finding the texture.
        return getTexturePos(parentBlockEntity(), direction) != null;
    }

    @Override
    public SkinPaintColor getColor(OpenDirection direction) {
        return getColor(direction, true);
    }

    @Override
    public void setColor(OpenDirection direction, SkinPaintColor color) {
        // ?
    }

    @Override
    public void setColors(Map<OpenDirection, SkinPaintColor> colors) {
        var blockEntity = parentBlockEntity();
        colors.forEach((dir, color) -> setArmourerTextureColor(blockEntity, getTexturePos(blockEntity, dir), color));
    }

    @Override
    public boolean hasColor(OpenDirection direction) {
        // bounding box can't support none paint type.
        return getColor(direction, false) != SkinPaintColor.CLEAR;
    }

    public void clearArmourerTextureColors() {
        var blockEntity = parentBlockEntity();
        if (blockEntity == null || getLevel() == null) {
            return;
        }
        for (var dir : OpenDirection.values()) {
            setArmourerTextureColor(blockEntity, getTexturePos(blockEntity, dir), SkinPaintColor.CLEAR);
        }
    }

    public SkinPaintColor getArmourerTextureColor(ArmourerBlockEntity blockEntity, OpenVector2i texturePos) {
        if (texturePos != null && blockEntity != null) {
            var color = blockEntity.getPaintColor(texturePos);
            if (color != null) {
                return color;
            }
        }
        return SkinPaintColor.CLEAR;
    }

    public void setArmourerTextureColor(ArmourerBlockEntity blockEntity, OpenVector2i texturePos, SkinPaintColor color) {
        if (texturePos != null && blockEntity != null) {
            blockEntity.setPaintColor(texturePos, color);
            BlockUtils.combine(blockEntity, blockEntity::sendBlockUpdates);
        }
    }

    private SkinPaintColor getColor(OpenDirection direction, boolean loadFromTexture) {
        var blockEntity = parentBlockEntity();
        var texturePos = getTexturePos(blockEntity, direction);
        var color = getArmourerTextureColor(blockEntity, texturePos);
        if (color != null && color.paintType() != SkinPaintTypes.NONE) {
            return color;
        }
        // when work in the client side, we try to get the texture color from the loaded texture.
        if (loadFromTexture) {
            var level = getLevel();
            if (level != null && level.isClientSide()) {
                return getTextureColor(blockEntity, texturePos).orElse(SkinPaintColor.CLEAR);
            }
        }
        return SkinPaintColor.CLEAR;
    }

    private Optional<SkinPaintColor> getTextureColor(ArmourerBlockEntity blockEntity, OpenVector2i texturePos) {
        return EnvironmentExecutor.callOnClient(() -> () -> {
            if (texturePos != null && blockEntity != null) {
                var skin = PlayerSkinBakery.getInstance().loadSkin(blockEntity.textureDescriptor());
                if (skin != null) {
                    return skin.body().getColor(texturePos);
                }
            }
            return null;
        });
    }

    private OpenVector2i getTexturePos(ArmourerBlockEntity blockEntity, OpenDirection direction) {
        if (blockEntity != null) {
            return blockEntity.getTexturePos(partType, guide, getResolvedDirection(blockEntity, direction));
        }
        return null;
    }

    private OpenDirection getResolvedDirection(ArmourerBlockEntity blockEntity, OpenDirection dir) {
        if (blockEntity == null) {
            return dir;
        }
        return switch (blockEntity.facing()) {
            case SOUTH -> OpenRotation.CLOCKWISE_180.rotate(dir); // rotate 180° get facing north direction.
            case WEST -> OpenRotation.CLOCKWISE_90.rotate(dir); // rotate 90° get facing north direction.
            case EAST -> OpenRotation.COUNTERCLOCKWISE_90.rotate(dir); // rotate -90° get facing north direction.
            default -> dir;
        };
    }

    private ArmourerBlockEntity parentBlockEntity() {
        // quickly query the parent block.
        if (cachedParentBlockEntity != null) {
            if (cachedParentBlockEntity.isRemoved()) {
                return null;
            }
            return cachedParentBlockEntity;
        }
        var level = getLevel();
        if (level == null || parent == null) {
            return null;
        }
        var target = getBlockPos().subtract(parent);
        if (level.getBlockEntity(target) instanceof ArmourerBlockEntity blockEntity) {
            cachedParentBlockEntity = blockEntity;
            return cachedParentBlockEntity;
        }
        return null;
    }

    public boolean isCustomRenderer() {
        // if the parent entity is missing, do not render it.
        if (customRenderer) {
            return isValid();
        }
        return false;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<BlockPos> REFER = IDataSerializerKey.create("Refer", ExtraCodecs.BLOCK_POS, null);
        public static final IDataSerializerKey<OpenVector3i> OFFSET = IDataSerializerKey.create("Offset", OpenVector3i.CODEC, OpenVector3i.ZERO);
        public static final IDataSerializerKey<SkinPartType> PART_TYPE = IDataSerializerKey.create("PartType", SkinPartTypes.CODEC, SkinPartTypes.UNKNOWN);
    }
}
