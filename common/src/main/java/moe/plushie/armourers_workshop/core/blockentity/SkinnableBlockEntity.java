package moe.plushie.armourers_workshop.core.blockentity;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.client.IBlockEntityExtendedRenderer;
import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3d;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SkinnableBlockEntity extends RotableContainerBlockEntity implements IBlockEntityExtendedRenderer {

    private static final DataSerializerKey<BlockPos> REFERENCE_KEY = DataSerializerKey.create("Refer", DataTypeCodecs.BLOCK_POS, BlockPos.ZERO);
    private static final DataSerializerKey<Rectangle3i> SHAPE_KEY = DataSerializerKey.create("Shape", DataTypeCodecs.RECTANGLE_3I, Rectangle3i.ZERO);
    private static final DataSerializerKey<BlockPos> LINKED_POS_KEY = DataSerializerKey.create("LinkedPos", DataTypeCodecs.BLOCK_POS, null);
    private static final DataSerializerKey<SkinDescriptor> SKIN_KEY = DataSerializerKey.create("Skin", DataTypeCodecs.SKIN_DESCRIPTOR, SkinDescriptor.EMPTY);
    private static final DataSerializerKey<SkinProperties> SKIN_PROPERTIES_KEY = DataSerializerKey.create("SkinProperties", DataTypeCodecs.SKIN_PROPERTIES, SkinProperties.EMPTY, SkinProperties::new);
    private static final DataSerializerKey<List<BlockPos>> REFERS_KEY = DataSerializerKey.create("Refers", DataTypeCodecs.BLOCK_POS.listOf(), Collections.emptyList());
    private static final DataSerializerKey<List<SkinMarker>> MARKERS_KEY = DataSerializerKey.create("Markers", DataTypeCodecs.SKIN_MARKER.listOf(), Collections.emptyList());

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

    private BlockPos reference = BlockPos.ZERO;
    private Rectangle3i collisionShape = Rectangle3i.ZERO;

    private NonNullList<ItemStack> items;
    private List<BlockPos> refers;
    private List<SkinMarker> markers;

    private BlockPos linkedBlockPos = null;

    private SkinProperties properties;
    private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

    private OpenQuaternionf renderRotations;
    private AABB renderBoundingBox;
    private VoxelShape renderVoxelShape = null;
    private ItemStack droppedStack = null;

    private boolean isParent = false;

    public SkinnableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public static Vector3f getRotations(BlockState state) {
        AttachFace face = state.getOptionalValue(SkinnableBlock.FACE).orElse(AttachFace.FLOOR);
        Direction facing = state.getOptionalValue(SkinnableBlock.FACING).orElse(Direction.NORTH);
        return FACING_TO_ROT.getOrDefault(Pair.of(face, facing), Vector3f.ZERO);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        reference = serializer.read(REFERENCE_KEY);
        collisionShape = serializer.read(SHAPE_KEY);
        renderVoxelShape = null;
        isParent = BlockPos.ZERO.equals(reference);
        if (!isParent()) {
            return;
        }
        SkinProperties oldProperties = properties;
        refers = serializer.read(REFERS_KEY);
        markers = serializer.read(MARKERS_KEY);
        descriptor = serializer.read(SKIN_KEY);
        properties = serializer.read(SKIN_PROPERTIES_KEY);
        linkedBlockPos = serializer.read(LINKED_POS_KEY);
        if (oldProperties != null) {
            oldProperties.clear();
            oldProperties.putAll(properties);
            properties = oldProperties;
        }
        serializer.readItemList(getOrCreateItems());
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        serializer.write(REFERENCE_KEY, reference);
        serializer.write(SHAPE_KEY, collisionShape);
        if (!isParent()) {
            return;
        }
        serializer.write(REFERS_KEY, refers);
        serializer.write(MARKERS_KEY, markers);
        serializer.write(SKIN_KEY, descriptor);
        serializer.write(SKIN_PROPERTIES_KEY, properties);
        serializer.write(LINKED_POS_KEY, linkedBlockPos);

        serializer.writeItemList(getOrCreateItems());
    }

    public void updateBlockStates() {
        setChanged();
        Level level = getLevel();
        if (level != null && !level.isClientSide()) {
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
        if (renderVoxelShape != null) {
            return renderVoxelShape;
        }
        if (collisionShape.equals(Rectangle3i.ZERO)) {
            renderVoxelShape = Shapes.block();
            return renderVoxelShape;
        }
        float minX = collisionShape.getMinX() / 16f + 0.5f;
        float minY = collisionShape.getMinY() / 16f + 0.5f;
        float minZ = collisionShape.getMinZ() / 16f + 0.5f;
        float maxX = collisionShape.getMaxX() / 16f + 0.5f;
        float maxY = collisionShape.getMaxY() / 16f + 0.5f;
        float maxZ = collisionShape.getMaxZ() / 16f + 0.5f;
        renderVoxelShape = Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        return renderVoxelShape;
    }

    public void setShape(Rectangle3i shape) {
        this.collisionShape = shape;
        this.renderVoxelShape = null;
    }

    public BlockPos getLinkedBlockPos() {
        return getValueFromParent(te -> te.linkedBlockPos);
    }

    public void setLinkedBlockPos(BlockPos linkedBlockPos) {
        SkinnableBlockEntity blockEntity = getParent();
        if (blockEntity != null) {
            blockEntity.linkedBlockPos = linkedBlockPos;
            blockEntity.updateBlockStates();
        }
    }

    public void kill() {
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return getOrCreateItems();
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
        return getBlockPos().subtract(reference);
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
            Direction facing = getBlockState().getOptionalValue(SkinnableBlock.FACING).orElse(Direction.NORTH);
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
        if (getLevel() != null) {
            return ObjectUtils.safeCast(getLevel().getBlockEntity(getParentPos()), SkinnableBlockEntity.class);
        }
        return null;
    }

    public void setDropped(ItemStack itemStack) {
        this.droppedStack = itemStack;
    }

    public ItemStack getDropped() {
        return droppedStack;
    }

    public boolean isDropped() {
        return droppedStack != null;
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

    @Override
    @Environment(EnvType.CLIENT)
    public OpenQuaternionf getRenderRotations(BlockState blockState) {
        if (renderRotations != null) {
            return renderRotations;
        }
        Vector3f r = getRotations(blockState);
        renderRotations = new OpenQuaternionf(r.getX(), r.getY(), r.getZ(), true);
        return renderRotations;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Rectangle3f getRenderShape(BlockState blockState) {
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(getDescriptor(), Tickets.TEST);
        if (bakedSkin == null) {
            return null;
        }
        float f = 1 / 16f;
        Rectangle3f box = bakedSkin.getRenderBounds(SkinItemSource.EMPTY).copy();
        box.mul(OpenMatrix4f.createScaleMatrix(-f, -f, f));
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
        SkinnableBlockEntity blockEntity = getParent();
        if (blockEntity != null) {
            return getter.apply(blockEntity);
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
