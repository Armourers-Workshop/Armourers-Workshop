package moe.plushie.armourers_workshop.builder.tileentity;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.builder.block.BoundingBoxBlock;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.tileentity.AbstractTileEntity;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.utils.BoundingBox;
import moe.plushie.armourers_workshop.utils.TextureUtils;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class BoundingBoxTileEntity extends AbstractTileEntity implements IPaintable {

    protected static final BlockPos INVALID = BlockPos.of(-1);

    protected Vector3i guide = Vector3i.ZERO;
    protected BlockPos parent = INVALID;

    protected ISkinPartType partType = SkinPartTypes.UNKNOWN;

    protected boolean customRenderer = false;

    public BoundingBoxTileEntity() {
        super(ModTileEntities.BOUNDING_BOX);
    }

    public void readFromNBT(CompoundNBT nbt) {
        parent = AWDataSerializers.getBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, INVALID);
        guide = AWDataSerializers.getVector3i(nbt, AWConstants.NBT.TILE_ENTITY_OFFSET);
        partType = SkinPartTypes.byName(AWDataSerializers.getString(nbt, AWConstants.NBT.SKIN_PART_TYPE, SkinTypes.UNKNOWN.getRegistryName().toString()));
        customRenderer = Arrays.stream(Direction.values()).anyMatch(this::shouldChangeColor);
    }

    public void writeToNBT(CompoundNBT nbt) {
        AWDataSerializers.putBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, parent, INVALID);
        AWDataSerializers.putVector3i(nbt, AWConstants.NBT.TILE_ENTITY_OFFSET, guide);
        AWDataSerializers.putString(nbt, AWConstants.NBT.SKIN_PART_TYPE, partType.getRegistryName().toString(), SkinTypes.UNKNOWN.getRegistryName().toString());
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
        this.parent = parent;
    }

    public Vector3i getGuide() {
        return guide;
    }

    public void setGuide(Vector3i guide) {
        this.guide = guide;
    }

    public boolean isValid() {
        return getParentTileEntity() != null;
    }

    @Override
    public boolean shouldChangeColor(Direction direction) {
        // we can't change the side color of the face without finding the texture.
        return BoundingBox.getTexturePos(partType, guide, direction) != null;
    }

    @Override
    public IPaintColor getColor(Direction direction) {
        IPaintColor color = getArmourerTextureColor(direction);
        if (color != null && color.getPaintType() != SkinPaintTypes.NONE) {
            return color;
        }
        // when work in the client side, we try to get the texture color from the loaded texture.
        if (level != null && level.isClientSide()) {
            return getTextureColor(direction);
        }
        return PaintColor.CLEAR;
    }

    @Override
    public void setColor(Direction direction, IPaintColor color) {
        // ?
    }

    @Override
    public void setColors(Map<Direction, IPaintColor> colors) {
        colors.forEach(this::setArmourerTextureColor);
    }

    public void clearArmourerTextureColors() {
        ArmourerTileEntity tileEntity = getParentTileEntity();
        if (tileEntity == null || level == null) {
            return;
        }
        for (Direction dir : Direction.values()) {
            this.setArmourerTextureColor(dir, PaintColor.CLEAR);
        }
    }

    public IPaintColor getArmourerTextureColor(Direction direction) {
        ArmourerTileEntity tileEntity = getParentTileEntity();
        Point texture = BoundingBox.getTexturePos(partType, guide, direction);
        if (texture != null && tileEntity != null) {
            IPaintColor color = tileEntity.getPaintColor(texture);
            if (color != null) {
                return color;
            }
        }
        return PaintColor.CLEAR;
    }

    public void setArmourerTextureColor(Direction direction, IPaintColor color) {
        ArmourerTileEntity tileEntity = getParentTileEntity();
        Point texture = BoundingBox.getTexturePos(partType, guide, direction);
        if (texture != null && tileEntity != null) {
            tileEntity.setPaintColor(texture, color);
            TileEntityUpdateCombiner.combine(tileEntity, tileEntity::sendBlockUpdates);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private IPaintColor getTextureColor(Direction direction) {
        ArmourerTileEntity tileEntity = getParentTileEntity();
        Point texture = BoundingBox.getTexturePos(partType, guide, direction);
        if (texture != null && tileEntity != null) {
            IPaintColor color = TextureUtils.getPlayerTextureModelColor(tileEntity.getTextureDescriptor(), texture);
            if (color != null) {
                return color;
            }
        }
        return PaintColor.CLEAR;
    }

    private ArmourerTileEntity getParentTileEntity() {
        World world = getLevel();
        if (world == null || parent.equals(INVALID)) {
            return null;
        }
        TileEntity tileEntity = world.getBlockEntity(getBlockPos().subtract(parent));
        if (tileEntity instanceof ArmourerTileEntity) {
            return (ArmourerTileEntity) tileEntity;
        }
        return null;
    }

    @Override
    public TileEntityType<?> getType() {
        if (customRenderer) {
            return ModTileEntities.BOUNDING_BOX_SR;
        }
        return super.getType();
    }
}
