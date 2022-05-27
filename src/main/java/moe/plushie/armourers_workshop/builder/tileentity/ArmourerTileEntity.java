package moe.plushie.armourers_workshop.builder.tileentity;

import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.builder.world.WorldBlockUpdateTask;
import moe.plushie.armourers_workshop.builder.world.WorldUpdater;
import moe.plushie.armourers_workshop.builder.world.WorldUtils;
import moe.plushie.armourers_workshop.core.model.PlayerTextureModel;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.tileentity.AbstractTileEntity;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import moe.plushie.armourers_workshop.utils.*;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ArmourerTileEntity extends AbstractTileEntity {

    protected int flags = 0;
    protected int version = 0;

    protected ISkinType skinType = SkinTypes.ARMOR_HEAD;
    protected SkinProperties skinProperties = new SkinProperties();
    protected PlayerTextureDescriptor textureDescriptor = PlayerTextureDescriptor.EMPTY;

    protected SkinPaintData paintData;

    protected Object renderData;
    protected AxisAlignedBB b;

    public ArmourerTileEntity() {
        super(ModTileEntities.ARMOURER);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt) {
        this.skinType = SkinTypes.byName(AWDataSerializers.getString(nbt, AWConstants.NBT.SKIN_TYPE, SkinTypes.ARMOR_HEAD.getRegistryName().toString()));
        this.skinProperties = AWDataSerializers.getSkinProperties(nbt, AWConstants.NBT.SKIN_PROPERTIES);
        this.textureDescriptor = AWDataSerializers.getTextureDescriptor(nbt, AWConstants.NBT.ENTITY_TEXTURE, PlayerTextureDescriptor.EMPTY);
        this.flags = AWDataSerializers.getInt(nbt, AWConstants.NBT.FLAGS, 0);
        this.paintData = AWDataSerializers.getPaintData(nbt, AWConstants.NBT.PAINT_DATA);
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        AWDataSerializers.putString(nbt, AWConstants.NBT.SKIN_TYPE, skinType.getRegistryName().toString(), null);
        AWDataSerializers.putSkinProperties(nbt, AWConstants.NBT.SKIN_PROPERTIES, skinProperties);
        AWDataSerializers.putTextureDescriptor(nbt, AWConstants.NBT.ENTITY_TEXTURE, textureDescriptor, PlayerTextureDescriptor.EMPTY);
        AWDataSerializers.putInt(nbt, AWConstants.NBT.FLAGS, flags, 0);
        AWDataSerializers.putPaintData(nbt, AWConstants.NBT.PAINT_DATA, paintData);
    }

    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.writeToNBT(nbt);
        nbt.putInt(AWConstants.NBT.DATA_VERSION, version);
        return new SUpdateTileEntityPacket(this.worldPosition, 3, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundNBT nbt = pkt.getTag();
        this.version = nbt.getInt(AWConstants.NBT.DATA_VERSION);
    }

    public void onPlace(World world, BlockPos pos, BlockState state) {
        remakeBoundingBoxes(null, getBoundingBoxes(), true);
    }

    public void onRemove(World world, BlockPos pos, BlockState state) {
        remakeBoundingBoxes(getBoundingBoxes(), null, true);
    }

    public ISkinType getSkinType() {
        return skinType;
    }

    public void setSkinType(ISkinType skinType) {
        if (this.skinType == skinType) {
            return;
        }
        Collection<BoundingBox> boxes = getBoundingBoxes();
        this.skinType = skinType;
        this.setPaintData(null);
        this.remakeSkinProperties();
        this.remakeBoundingBoxes(boxes, getBoundingBoxes(), true);
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    public SkinProperties getSkinProperties() {
        return skinProperties;
    }

    public void setSkinProperties(SkinProperties skinProperties) {
        Collection<BoundingBox> boxes = getBoundingBoxes();
        this.skinProperties = skinProperties;
        this.remakeBoundingBoxes(boxes, getBoundingBoxes(), false);
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        Collection<BoundingBox> boxes = getBoundingBoxes();
        this.flags = flags;
        this.remakeBoundingBoxes(boxes, getBoundingBoxes(), false);
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    public PlayerTextureDescriptor getTextureDescriptor() {
        return textureDescriptor;
    }

    public void setTextureDescriptor(PlayerTextureDescriptor textureDescriptor) {
        this.textureDescriptor = textureDescriptor;
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    public void setPaintData(SkinPaintData paintData) {
        if (this.paintData == paintData) {
            return;
        }
        if (paintData != null) {
            this.paintData = paintData.clone();
        } else {
            this.paintData = null;
        }
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    public SkinPaintData getPaintData() {
        return paintData;
    }

    public IPaintColor getPaintColor(Point pos) {
        if (paintData != null) {
            return PaintColor.of(paintData.getColor(pos));
        }
        return null;
    }

    public void setPaintColor(Point pos, IPaintColor paintColor) {
        if (this.paintData == null) {
            this.paintData = new SkinPaintData(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT);
        }
        this.paintData.setColor(pos.x, pos.y, paintColor.getRawValue());
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.version += 1;
    }

    public boolean isShowGuides() {
        return (flags & 0x01) == 0;
    }

    public void setShowGuides(boolean value) {
        if (value) {
            this.flags &= ~0x01; // -
        } else {
            this.flags |= 0x01; // +
        }
        this.setChanged();
    }

    public boolean isShowHelper() {
        return (flags & 0x02) == 0;
    }

    public void setShowHelper(boolean value) {
        if (value) {
            this.flags &= ~0x02; // -
        } else {
            this.flags |= 0x02; // +
        }
        this.setChanged();
    }

    public boolean usesHelper() {
        if (skinType == SkinTypes.ARMOR_WINGS) {
            return true;
        }
        return skinType instanceof ISkinToolType;
    }

    public void copyPaintData(ISkinPartType srcPart, ISkinPartType destPart, boolean mirror) {
        if (paintData == null) {
            return;
        }
        PlayerTextureModel textureModel = BoundingBox.MODEL;
        SkyBox srcBox = textureModel.get(srcPart);
        SkyBox destBox = textureModel.get(destPart);
        if (srcBox != null && destBox != null) {
            WorldUtils.copyPaintData(paintData, srcBox, destBox, mirror);
            TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
        }
    }

    public void clearPaintData(ISkinPartType partType) {
        if (paintData == null) {
            return;
        }
        // we think the unknown part type is the signal for the clear all.
        if (partType == SkinPartTypes.UNKNOWN) {
            setPaintData(null);
            return;
        }
        // we just need to clear the paint data for the current part type.
        PlayerTextureModel textureModel = BoundingBox.MODEL;
        SkyBox srcBox = textureModel.get(partType);
        if (srcBox != null) {
            WorldUtils.clearPaintData(paintData, srcBox);
            TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
        }
    }

    public void clearCubes(ISkinPartType partType) {
        // remove all part
        World world = getLevel();
        BlockPos pos = getBlockPos().offset(0, 1, 0);
        Direction direction = Direction.NORTH;
        WorldUtils.clearCubes(world, pos, getSkinType(), getSkinProperties(), direction, partType);
        // when just clear a part, we don't reset skin properties.
        if (partType != SkinPartTypes.UNKNOWN) {
            return;
        }
        // remake all properties.
        boolean isMultiBlock = skinProperties.get(SkinProperty.BLOCK_MULTIBLOCK);
        skinProperties = new SkinProperties();
        skinProperties.put(SkinProperty.BLOCK_MULTIBLOCK, isMultiBlock);
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    public void copyCubes(ISkinPartType srcPart, ISkinPartType destPart, boolean mirror) throws Exception {
        World world = getLevel();
        BlockPos pos = getBlockPos().offset(0, 1, 0);
        Direction direction = Direction.NORTH;
        WorldUtils.copyCubes(world, pos, getSkinType(), getSkinProperties(), direction, srcPart, destPart, mirror);
    }

    public void clearMarkers(ISkinPartType partType) {
        World world = getLevel();
        BlockPos pos = getBlockPos().offset(0, 1, 0);
        Direction direction = Direction.NORTH;
        WorldUtils.clearMarkers(world, pos, getSkinType(), getSkinProperties(), direction, partType);
        setChanged();
    }

    public int getVersion() {
        return version;
    }

    public Object getRenderData() {
        return renderData;
    }

    public void setRenderData(Object renderData) {
        this.renderData = renderData;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (b == null) {
            b = new AxisAlignedBB(-32, -32, -44, 64, 64, 64);
            b = b.move(getBlockPos());
        }
        return b;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getViewDistance() {
        return 128;
    }

    private void remakeSkinProperties() {
        String name = skinProperties.get(SkinProperty.ALL_CUSTOM_NAME);
        String flavour = skinProperties.get(SkinProperty.ALL_FLAVOUR_TEXT);
        this.skinProperties = new SkinProperties();
        this.skinProperties.put(SkinProperty.ALL_CUSTOM_NAME, name);
        this.skinProperties.put(SkinProperty.ALL_FLAVOUR_TEXT, flavour);
    }

    private boolean shouldAddBoundingBoxes(ISkinPartType partType) {
        if (usesHelper()) {
            return isShowHelper();
        }
        return !partType.isModelOverridden(getSkinProperties());
    }

    private void remakeBoundingBoxes(Collection<BoundingBox> oldBoxes, Collection<BoundingBox> newBoxes, boolean forced) {
        // we only remake bounding box on the server side.
        World world = getLevel();
        if (world == null || world.isClientSide()) {
            return;
        }
        // we only remake bounding box when data is changed.
        if (!forced && Objects.equals(oldBoxes, newBoxes)) {
            return;
        }
        // we need to remove the old bounding box before add.
        applyBoundingBoxes(oldBoxes, (partType, pos, offset) -> {
            WorldBlockUpdateTask task = new WorldBlockUpdateTask(world, pos, Blocks.AIR.defaultBlockState());
            task.setValidator(state -> state.is(ModBlocks.BOUNDING_BOX));
            return task;
        });
        applyBoundingBoxes(newBoxes, (partType, pos, offset) -> {
            WorldBlockUpdateTask task = new WorldBlockUpdateTask(world, pos, ModBlocks.BOUNDING_BOX.defaultBlockState());
            task.setValidator(state -> state.getMaterial().isReplaceable());
            task.setModifier(state -> setupBoundingBox(world, pos, offset, partType));
            return task;
        });
    }

    private void setupBoundingBox(World world, BlockPos pos, Vector3i offset, ISkinPartType partType) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof BoundingBoxTileEntity) {
            BoundingBoxTileEntity box = (BoundingBoxTileEntity)tileEntity;
            box.setPartType(partType);
            box.setGuide(offset);
            box.setParent(pos.subtract(getBlockPos()));
            TileEntityUpdateCombiner.combine(box, box::sendBlockUpdates);
        }
    }

    private void applyBoundingBoxes(@Nullable Collection<BoundingBox> boxes, IUpdateTaskBuilder builder) {
        if (boxes == null || boxes.isEmpty()) {
            return;
        }
        BlockPos pos = getBlockPos();
        boxes.forEach(box -> box.forEach((ix, iy, iz) -> {
            int tx = ix + box.getX() + pos.getX();
            int ty = iy + box.getY() + pos.getY() + 1;
            int tz = iz + box.getZ() + pos.getZ();
            ix = box.getWidth() - ix - 1;
            iy = box.getHeight() - iy - 1;
            ISkinPartType partType = box.getPartType();
            IWorldUpdateTask task = builder.build(partType, new BlockPos(tx, ty, tz), new Vector3i(ix, iy, iz));
            if (task != null) {
                WorldUpdater.getInstance().submit(task);
            }
        }));
    }

    private Collection<BoundingBox> getBoundingBoxes() {
        ArrayList<BoundingBox> boxes = new ArrayList<>();
        for (ISkinPartType partType : skinType.getParts()) {
            if (shouldAddBoundingBoxes(partType)) {
                Vector3i offset = partType.getOffset();
                Rectangle3i rect = partType.getGuideSpace();
                Rectangle3i bounds = partType.getBuildingSpace();
                rect = rect.offset(-offset.getX(), -offset.getY() - bounds.getMinY(), offset.getZ());
                boxes.add(new BoundingBox(partType, rect));
            }
        }
        return boxes;
    }

    public interface IUpdateTaskBuilder {
        IWorldUpdateTask build(ISkinPartType partType, BlockPos pos, Vector3i offset);
    }
}
