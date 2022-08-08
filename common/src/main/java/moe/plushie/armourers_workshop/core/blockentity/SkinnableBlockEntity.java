package moe.plushie.armourers_workshop.core.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.api.client.IBlockEntityExtendedRenderer;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3d;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public class SkinnableBlockEntity extends RotableContainerBlockEntity implements IBlockEntityExtendedRenderer {

    private static final BlockPos INVALID = BlockPos.of(-1);

    private static final ImmutableMap<?, Vector3f> FACING_TO_ROT = new ImmutableMap.Builder<Object, Vector3f>()
            .put(Pair.of(AttachFace.CEILING, Direction.EAST), new Vector3f(180, 270, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.NORTH), new Vector3f(180, 180, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.WEST), new Vector3f(180, 90, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.SOUTH), new Vector3f(180, 0, 0))
            .put(Pair.of(AttachFace.WALL, Direction.EAST), new Vector3f(0, 270, 0))
            .put(Pair.of(AttachFace.WALL, Direction.SOUTH), new Vector3f(0, 180, 0))
            .put(Pair.of(AttachFace.WALL, Direction.WEST), new Vector3f(0, 90, 0))
            .put(Pair.of(AttachFace.WALL, Direction.NORTH), new Vector3f(0, 0, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.EAST), new Vector3f(0, 270, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.SOUTH), new Vector3f(0, 180, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.WEST), new Vector3f(0, 90, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.NORTH), new Vector3f(0, 0, 0))
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
    private AABB renderBoundingBox;

    private boolean isDropped = false;
    private boolean isParent = false;

    public SkinnableBlockEntity(BlockEntityType<?> entityType) {
        super(entityType);
    }

    public static Vector3f getRotations(BlockState state) {
        AttachFace face = state.getValue(SkinnableBlock.FACE);
        Direction facing = state.getValue(SkinnableBlock.FACING);
        return FACING_TO_ROT.getOrDefault(Pair.of(face, facing), new Vector3f());
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        refer = DataSerializers.getBlockPos(nbt, Constants.Key.TILE_ENTITY_REFER, INVALID);
        shape = DataSerializers.getRectangle3i(nbt, Constants.Key.TILE_ENTITY_SHAPE, Rectangle3i.ZERO);
        isParent = BlockPos.ZERO.equals(refer);
        if (!isParent()) {
            return;
        }
        SkinProperties oldProperties = properties;
        refers = DataSerializers.getBlockPosList(nbt, Constants.Key.TILE_ENTITY_REFERS);
        markers = DataSerializers.getMarkerList(nbt, Constants.Key.TILE_ENTITY_MARKERS);
        descriptor = DataSerializers.getSkinDescriptor(nbt, Constants.Key.TILE_ENTITY_SKIN, SkinDescriptor.EMPTY);
        properties = DataSerializers.getSkinProperties(nbt, Constants.Key.TILE_ENTITY_SKIN_PROPERTIES);
        linkedBlockPos = DataSerializers.getBlockPos(nbt, Constants.Key.TILE_ENTITY_LINKED_POS, null);
        if (oldProperties != null) {
            oldProperties.copyFrom(properties);
            properties = oldProperties;
        }
        ContainerHelper.loadAllItems(nbt, getOrCreateItems());
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        DataSerializers.putBlockPos(nbt, Constants.Key.TILE_ENTITY_REFER, refer, INVALID);
        DataSerializers.putRectangle3i(nbt, Constants.Key.TILE_ENTITY_SHAPE, shape, Rectangle3i.ZERO);
        if (!isParent()) {
            return;
        }
        DataSerializers.putBlockPosList(nbt, Constants.Key.TILE_ENTITY_REFERS, refers);
        DataSerializers.putMarkerList(nbt, Constants.Key.TILE_ENTITY_MARKERS, markers);
        DataSerializers.putSkinDescriptor(nbt, Constants.Key.TILE_ENTITY_SKIN, descriptor, SkinDescriptor.EMPTY);
        DataSerializers.putSkinProperties(nbt, Constants.Key.TILE_ENTITY_SKIN_PROPERTIES, properties);
        DataSerializers.putBlockPos(nbt, Constants.Key.TILE_ENTITY_LINKED_POS, linkedBlockPos, null);

        ContainerHelper.saveAllItems(nbt, getOrCreateItems());
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
            return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return Shapes.block();
    }

    public void setShape(Rectangle3i shape) {
        this.shape = shape;
    }

    public BlockPos getLinkedBlockPos() {
        return getValueFromParent(te -> te.linkedBlockPos);
    }

    public void setLinkedBlockPos(BlockPos linkedBlockPos) {
        SkinnableBlockEntity tileEntity = getParent();
        if (tileEntity != null) {
            tileEntity.linkedBlockPos = linkedBlockPos;
            tileEntity.updateBlockStates();
        }
    }

    public void kill() {
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public Quaternion getRenderRotations() {
        if (renderRotations != null) {
            return renderRotations;
        }
        Vector3f r = getRotations(getBlockState());
        renderRotations = TrigUtils.rotate(r.getX(), r.getY(), r.getZ(), true);
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
    public Container getInventory() {
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
            Direction facing = getBlockState().getValue(SkinnableBlock.FACING);
            return parentPos.relative(Rotation.CLOCKWISE_180.rotate(facing));
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
    public SkinnableBlockEntity getParent() {
        if (isParent()) {
            return this;
        }
        if (getLevel() != null && refer != INVALID) {
            BlockEntity tileEntity = getLevel().getBlockEntity(getParentPos());
            if (tileEntity instanceof SkinnableBlockEntity) {
                return (SkinnableBlockEntity) tileEntity;
            }
        }
        return null;
    }

    public void setDropped(boolean flag) {
        isDropped = flag;
    }

    public boolean isDropped() {
        return isDropped;
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
        return isParent;
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

    @Override
    public boolean shouldUseExtendedRenderer() {
        return isParent;
    }

    @Environment(value = EnvType.CLIENT)
    @Override
    public Rectangle3f getRenderBoundingBox(BlockState state) {
        BakedSkin bakedSkin = BakedSkin.of(getDescriptor());
        if (bakedSkin == null) {
            return null;
        }
        float f = 1 / 16f;
        Rectangle3f box = bakedSkin.getRenderBounds(null, null, null, ItemStack.EMPTY).copy();
        box.mul(OpenMatrix4f.createScaleMatrix(f, -f, f));
        return box;
    }


    private NonNullList<ItemStack> getOrCreateItems() {
        if (items == null) {
            items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        }
        return items;
    }

    @Nullable
    private <V> V getValueFromParent(Function<SkinnableBlockEntity, V> getter) {
        SkinnableBlockEntity tileEntity = getParent();
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
