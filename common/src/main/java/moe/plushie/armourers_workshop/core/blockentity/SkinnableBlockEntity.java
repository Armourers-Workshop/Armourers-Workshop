package moe.plushie.armourers_workshop.core.blockentity;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.client.IBlockEntityExtendedRenderer;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
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
    public void readFromNBT(CompoundTag tag) {
        refer = tag.getOptionalBlockPos(Constants.Key.BLOCK_ENTITY_REFER, INVALID);
        shape = tag.getOptionalRectangle3i(Constants.Key.BLOCK_ENTITY_SHAPE, Rectangle3i.ZERO);
        renderVoxelShape = null;
        isParent = BlockPos.ZERO.equals(refer);
        if (!isParent()) {
            return;
        }
        SkinProperties oldProperties = properties;
        refers = tag.getOptionalBlockPosArray(Constants.Key.BLOCK_ENTITY_REFERS);
        markers = tag.getOptionalSkinMarkerArray(Constants.Key.BLOCK_ENTITY_MARKERS);
        descriptor = tag.getOptionalSkinDescriptor(Constants.Key.BLOCK_ENTITY_SKIN);
        properties = tag.getOptionalSkinProperties(Constants.Key.BLOCK_ENTITY_SKIN_PROPERTIES);
        linkedBlockPos = tag.getOptionalBlockPos(Constants.Key.BLOCK_ENTITY_LINKED_POS, null);
        if (oldProperties != null) {
            oldProperties.clear();
            oldProperties.putAll(properties);
            properties = oldProperties;
        }
        ContainerHelper.loadAllItems(tag, getOrCreateItems());
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putOptionalBlockPos(Constants.Key.BLOCK_ENTITY_REFER, refer, INVALID);
        tag.putOptionalRectangle3i(Constants.Key.BLOCK_ENTITY_SHAPE, shape, Rectangle3i.ZERO);
        if (!isParent()) {
            return;
        }
        tag.putOptionalBlockPosArray(Constants.Key.BLOCK_ENTITY_REFERS, refers);
        tag.putOptionalSkinMarkerArray(Constants.Key.BLOCK_ENTITY_MARKERS, markers);
        tag.putOptionalSkinDescriptor(Constants.Key.BLOCK_ENTITY_SKIN, descriptor);
        tag.putOptionalSkinProperties(Constants.Key.BLOCK_ENTITY_SKIN_PROPERTIES, properties);
        tag.putOptionalBlockPos(Constants.Key.BLOCK_ENTITY_LINKED_POS, linkedBlockPos, null);

        ContainerHelper.saveAllItems(tag, getOrCreateItems());
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
        if (shape.equals(Rectangle3i.ZERO)) {
            renderVoxelShape = Shapes.block();
            return renderVoxelShape;
        }
        float minX = shape.getMinX() / 16f + 0.5f;
        float minY = shape.getMinY() / 16f + 0.5f;
        float minZ = shape.getMinZ() / 16f + 0.5f;
        float maxX = shape.getMaxX() / 16f + 0.5f;
        float maxY = shape.getMaxY() / 16f + 0.5f;
        float maxZ = shape.getMaxZ() / 16f + 0.5f;
        renderVoxelShape = Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        return renderVoxelShape;
    }

    public void setShape(Rectangle3i shape) {
        this.shape = shape;
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
    @Environment(EnvType.CLIENT)
    public OpenQuaternionf getRenderRotations(BlockState blockState) {
        if (renderRotations != null) {
            return renderRotations;
        }
        Vector3f r = getRotations(blockState);
        renderRotations = new OpenQuaternionf(r.getX(), r.getY(), r.getZ(), true);
        return renderRotations;
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
        if (getLevel() != null && refer != INVALID) {
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

    @Environment(EnvType.CLIENT)
    @Override
    public Rectangle3f getRenderBoundingBox(BlockState blockState) {
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(getDescriptor(), Tickets.TEST);
        if (bakedSkin == null) {
            return null;
        }
        float f = 1 / 16f;
        Rectangle3f box = bakedSkin.getRenderBounds(null, null, SkinItemSource.EMPTY).copy();
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
