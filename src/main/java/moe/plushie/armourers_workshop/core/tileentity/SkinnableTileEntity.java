package moe.plushie.armourers_workshop.core.tileentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.core.utils.TrigUtils;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
public class SkinnableTileEntity extends RotableContainerTileEntity {

    private static final BlockPos INVALID = BlockPos.of(-1);

    private static final ImmutableMap<?, Vector3f> FACING_TO_ROT = new ImmutableMap.Builder<Object, Vector3f>()
            .put(Pair.of(AttachFace.CEILING, Direction.EAST), new Vector3f(180, 90, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.NORTH), new Vector3f(180, 0, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.WEST), new Vector3f(180, 270, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.SOUTH), new Vector3f(180, 180, 0))
            .put(Pair.of(AttachFace.WALL, Direction.EAST), new Vector3f(0, 270, 0))
            .put(Pair.of(AttachFace.WALL, Direction.SOUTH), new Vector3f(0, 180, 0))
            .put(Pair.of(AttachFace.WALL, Direction.WEST), new Vector3f(0, 90, 0))
            .put(Pair.of(AttachFace.WALL, Direction.NORTH), new Vector3f(0, 0, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.EAST), new Vector3f(0, 90, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.SOUTH), new Vector3f(0, 0, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.WEST), new Vector3f(0, 270, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.NORTH), new Vector3f(0, 180, 0))
            .build();

    private BlockPos refer = INVALID;
    private Rectangle3i shape = Rectangle3i.ZERO;

    private NonNullList<ItemStack> items;
    private Collection<BlockPos> refers;
    private Collection<SkinMarker> markers;

    private BlockPos linkedBlockPos = null;

    private SkinProperties properties;
    private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

    private Quaternion renderRotations;
    private AxisAlignedBB renderBoundingBox;

    public SkinnableTileEntity() {
        super(ModTileEntities.SKINNABLE);
    }

    public static Vector3f getRotations(BlockState state) {
        AttachFace face = state.getValue(SkinnableBlock.FACE);
        Direction facing = state.getValue(SkinnableBlock.FACING);
        return FACING_TO_ROT.getOrDefault(Pair.of(face, facing), new Vector3f());
    }

    @Override
    public void readFromNBT(CompoundNBT nbt) {
        refer = AWDataSerializers.getBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, INVALID);
        shape = AWDataSerializers.getRectangle3i(nbt, AWConstants.NBT.TILE_ENTITY_SHAPE, Rectangle3i.ZERO);
        if (!isParent()) {
            return;
        }
        SkinProperties oldProperties = properties;
        refers = AWDataSerializers.getBlockPosList(nbt, AWConstants.NBT.TILE_ENTITY_REFERS);
        markers = AWDataSerializers.getMarkerList(nbt, AWConstants.NBT.TILE_ENTITY_MARKERS);
        descriptor = AWDataSerializers.getSkinDescriptor(nbt, AWConstants.NBT.TILE_ENTITY_SKIN, SkinDescriptor.EMPTY);
        properties = AWDataSerializers.getSkinProperties(nbt, AWConstants.NBT.TILE_ENTITY_SKIN_PROPERTIES);
        linkedBlockPos = AWDataSerializers.getBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_LINKED_POS, null);
        if (oldProperties != null) {
            oldProperties.copyFrom(properties);
            properties = oldProperties;
        }
        ItemStackHelper.loadAllItems(nbt, getOrCreateItems());
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        AWDataSerializers.putBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, refer, INVALID);
        AWDataSerializers.putRectangle3i(nbt, AWConstants.NBT.TILE_ENTITY_SHAPE, shape, Rectangle3i.ZERO);
        if (!isParent()) {
            return;
        }
        AWDataSerializers.putBlockPosList(nbt, AWConstants.NBT.TILE_ENTITY_REFERS, refers);
        AWDataSerializers.putMarkerList(nbt, AWConstants.NBT.TILE_ENTITY_MARKERS, markers);
        AWDataSerializers.putSkinDescriptor(nbt, AWConstants.NBT.TILE_ENTITY_SKIN, descriptor, SkinDescriptor.EMPTY);
        AWDataSerializers.putSkinProperties(nbt, AWConstants.NBT.TILE_ENTITY_SKIN_PROPERTIES, properties);
        AWDataSerializers.putBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_LINKED_POS, linkedBlockPos, null);

        ItemStackHelper.saveAllItems(nbt, getOrCreateItems());
    }

    public void updateBlockStates() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    public SkinDescriptor getDescriptor() {
        if (isParent()) {
            return descriptor;
        }
        return SkinDescriptor.EMPTY;
    }

    public void setDescriptor(SkinDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public VoxelShape getShape() {
        if (!shape.equals(Rectangle3i.ZERO)) {
            float minX = shape.getMinX() / 16f + 0.5f;
            float minY = shape.getMinY() / 16f + 0.5f;
            float minZ = shape.getMinZ() / 16f + 0.5f;
            float maxX = shape.getMaxX() / 16f + 0.5f;
            float maxY = shape.getMaxY() / 16f + 0.5f;
            float maxZ = shape.getMaxZ() / 16f + 0.5f;
            return VoxelShapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return VoxelShapes.block();
    }

    public void setShape(Rectangle3i shape) {
        this.shape = shape;
    }

    public BlockPos getLinkedBlockPos() {
        return getValueFromParent(te -> te.linkedBlockPos);
    }

    public void setLinkedBlockPos(BlockPos linkedBlockPos) {
        SkinnableTileEntity tileEntity = getParent();
        if (tileEntity != null) {
            tileEntity.linkedBlockPos = linkedBlockPos;
            tileEntity.updateBlockStates();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Quaternion getRenderRotations() {
        if (renderRotations != null) {
            return renderRotations;
        }
        Vector3f r = getRotations(getBlockState());
        renderRotations = TrigUtils.rotate(r.x(), r.y(), r.z(), true);
        return renderRotations;
    }


    @Override
    public NonNullList<ItemStack> getItems() {
        if (items != null) {
            return items;
        }
        return NonNullList.create();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 9 * 9;
    }

    @Nullable
    public String getInventoryName() {
        return getProperty(SkinProperty.ALL_CUSTOM_NAME);
    }

    @Nullable
    @Override
    public IInventory getInventory() {
        return getParent();
    }

    public Collection<BlockPos> getRefers() {
        if (refers == null) {
            refers = getValueFromParent(te -> te.refers);
        }
        return refers;
    }

    public BlockPos getParentPos() {
        return getBlockPos().subtract(refer);
    }

    public Vector3d getSeatPos() {
        float dx = 0, dy = 0, dz = 0;
        BlockPos parentPos = getParentPos();
        Collection<SkinMarker> markers = getMarkers();
        if (markers != null && !markers.isEmpty()) {
            SkinMarker marker = markers.iterator().next();
            dx = marker.x / 16.0f;
            dy = marker.y / 16.0f;
            dz = marker.z / 16.0f;
        }
        return new Vector3d(parentPos.getX() + dx, parentPos.getY() + dy, parentPos.getZ() + dz);
    }

    public BlockPos getBedPos() {
        BlockPos parentPos = getParentPos();
        Collection<SkinMarker> markers = getMarkers();
        if (markers == null || markers.isEmpty()) {
            return parentPos.relative(getBlockState().getValue(SkinnableBlock.FACING));
        }
        SkinMarker marker = markers.iterator().next();
        return parentPos.offset(marker.x / 16, marker.y / 16, marker.z / 16);
    }

    public Collection<SkinMarker> getMarkers() {
        if (markers == null) {
            markers = getValueFromParent(te -> te.markers);
        }
        return markers;
    }

    @Nullable
    public SkinProperties getProperties() {
        if (properties == null) {
            properties = getValueFromParent(te -> te.properties);
        }
        return properties;
    }

    @Nullable
    public SkinnableTileEntity getParent() {
        if (isParent()) {
            return this;
        }
        if (getLevel() != null && refer != INVALID) {
            TileEntity tileEntity = getLevel().getBlockEntity(getParentPos());
            if (tileEntity instanceof SkinnableTileEntity) {
                return (SkinnableTileEntity) tileEntity;
            }
        }
        return null;
    }

    public boolean isLadder() {
        return getProperty(SkinProperty.BLOCK_LADDER);
    }

    public boolean isGrowing() {
        return getProperty(SkinProperty.BLOCK_GLOWING);
    }

    public boolean isSeat() {
        return getProperty(SkinProperty.BLOCK_SEAT);
    }

    public boolean isBed() {
        return getProperty(SkinProperty.BLOCK_BED);
    }

    public boolean isLinked() {
        return getLinkedBlockPos() != null;
    }

    public boolean isInventory() {
        return getProperty(SkinProperty.BLOCK_INVENTORY) || isEnderInventory();
    }

    public boolean isEnderInventory() {
        return getProperty(SkinProperty.BLOCK_ENDER_INVENTORY);
    }

    public boolean isParent() {
        return BlockPos.ZERO.equals(refer);
    }

    public boolean noCollision() {
        return getProperty(SkinProperty.BLOCK_NO_COLLISION);
    }

    public int getInventoryWidth() {
        return getProperty(SkinProperty.BLOCK_INVENTORY_WIDTH);
    }

    public int getInventoryHeight() {
        return getProperty(SkinProperty.BLOCK_INVENTORY_HEIGHT);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Rectangle3f getRenderBoundingBox(BlockState state) {
        BakedSkin bakedSkin = BakedSkin.of(getDescriptor());
        if (bakedSkin == null) {
            return null;
        }
        float f = 1 / 16f;
        Rectangle3f box = bakedSkin.getRenderBounds(null, null, null).copy();
        box.mul(TrigUtils.scale(f, -f, f));
        return box;
    }


    private NonNullList<ItemStack> getOrCreateItems() {
        if (items == null) {
            items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        }
        return items;
    }

    @Nullable
    private <V> V getValueFromParent(Function<SkinnableTileEntity, V> getter) {
        SkinnableTileEntity tileEntity = getParent();
        if (tileEntity != null) {
            return getter.apply(tileEntity);
        }
        return null;
    }

    private <V> V getProperty(SkinProperty<V> property) {
        SkinProperties properties = getProperties();
        if (properties != null) {
            return properties.get(property);
        }
        return property.getDefaultValue();
    }
}
