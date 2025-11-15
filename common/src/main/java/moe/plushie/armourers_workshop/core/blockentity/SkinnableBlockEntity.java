package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.common.ITickable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.SimpleContainer;
import moe.plushie.armourers_workshop.core.data.ticket.TicketManager;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SkinnableBlockEntity extends RotableContainerBlockEntity implements ITickable {

    private static final Map<?, OpenVector3f> FACING_TO_ROT = Collections.immutableMap(it -> {
        it.put(Pair.of(AttachFace.CEILING, Direction.EAST), new OpenVector3f(180, 270, 0));
        it.put(Pair.of(AttachFace.CEILING, Direction.NORTH), new OpenVector3f(180, 180, 0));
        it.put(Pair.of(AttachFace.CEILING, Direction.WEST), new OpenVector3f(180, 90, 0));
        it.put(Pair.of(AttachFace.CEILING, Direction.SOUTH), new OpenVector3f(180, 0, 0));
        it.put(Pair.of(AttachFace.WALL, Direction.EAST), new OpenVector3f(0, 270, 0));
        it.put(Pair.of(AttachFace.WALL, Direction.SOUTH), new OpenVector3f(0, 180, 0));
        it.put(Pair.of(AttachFace.WALL, Direction.WEST), new OpenVector3f(0, 90, 0));
        it.put(Pair.of(AttachFace.WALL, Direction.NORTH), new OpenVector3f(0, 0, 0));
        it.put(Pair.of(AttachFace.FLOOR, Direction.EAST), new OpenVector3f(0, 270, 0));
        it.put(Pair.of(AttachFace.FLOOR, Direction.SOUTH), new OpenVector3f(0, 180, 0));
        it.put(Pair.of(AttachFace.FLOOR, Direction.WEST), new OpenVector3f(0, 90, 0));
        it.put(Pair.of(AttachFace.FLOOR, Direction.NORTH), new OpenVector3f(0, 0, 0));
    });

    private BlockPos reference = BlockPos.ZERO;
    private OpenRectangle3i collisionShape = OpenRectangle3i.ZERO;

    private SimpleContainer container;
    private List<BlockPos> refers;
    private List<SkinMarker> markers;

    private GlobalPos linkedPos = null;

    private SkinProperties properties;
    private SkinDescriptor skin = SkinDescriptor.EMPTY;

    private OpenQuaternionf renderRotations;

    private VoxelShape cachedRenderShape = null;
    private VoxelShape cachedCollisionShape = null;

    private ItemStack droppedStack = null;

    private LinkedSnapshot lastSnapshot;

    private int callDepth = 0;
    private boolean isParent = false;

    public SkinnableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public static OpenVector3f getRotations(BlockState state) {
        AttachFace face = state.getOptionalValue(SkinnableBlock.FACE).orElse(AttachFace.FLOOR);
        Direction facing = state.getOptionalValue(SkinnableBlock.FACING).orElse(Direction.NORTH);
        return FACING_TO_ROT.getOrDefault(Pair.of(face, facing), OpenVector3f.ZERO);
    }

    @Override
    protected void abi$readAdditionalData(IDataSerializer serializer) {
        reference = serializer.read(CodingKeys.REFERENCE);
        collisionShape = serializer.read(CodingKeys.SHAPE);
        cachedRenderShape = null;
        cachedCollisionShape = null;
        isParent = BlockPos.ZERO.equals(reference);
        if (!isParent()) {
            return;
        }
        var oldProperties = properties;
        refers = serializer.read(CodingKeys.REFERENCES);
        markers = serializer.read(CodingKeys.MARKERS);
        skin = serializer.read(CodingKeys.SKIN);
        properties = serializer.read(CodingKeys.SKIN_PROPERTIES);
        linkedPos = serializer.read(CodingKeys.LINKED_POS);
        if (oldProperties != null) {
            oldProperties.clear();
            oldProperties.putAll(properties);
            properties = oldProperties;
        }
        getOrCreateItems().deserialize(serializer);
    }

    @Override
    protected void abi$writeAdditionalData(IDataSerializer serializer) {
        serializer.write(CodingKeys.REFERENCE, reference);
        serializer.write(CodingKeys.SHAPE, collisionShape);
        if (!isParent()) {
            return;
        }
        serializer.write(CodingKeys.REFERENCES, refers);
        serializer.write(CodingKeys.MARKERS, markers);
        serializer.write(CodingKeys.SKIN, skin);
        serializer.write(CodingKeys.SKIN_PROPERTIES, properties);
        serializer.write(CodingKeys.LINKED_POS, linkedPos);
        getOrCreateItems().serialize(serializer);
    }

    @Override
    public void tick() {
        if (isParent()) {
            parentTick();
        } else {
            childTick();
        }
    }

    protected void parentTick() {
        // notify neighbors blocks when snapshot changed.
        var snapshot = makeLinkedSnapshot();
        if (!Objects.equals(lastSnapshot, snapshot)) {
            updateStateAndNeighbors();
            lastSnapshot = snapshot;
        }
    }

    protected void childTick() {
        // when the parent block is broken for some reason, the child will be automatically destroyed.
        var level = getLevel();
        var parent = getParent();
        if (parent == null && level != null && !level.isClientSide()) {
            ModLog.warn("found a zombie block at {}, destroy it.", getBlockPos());
            kill();
        }
    }

    public void updateBlockStates() {
        setChanged();
        var level = getLevel();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    public void updateStateAndNeighbors() {
        var level = getLevel();
        if (level != null && !level.isClientSide()) {
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    public void setSkin(SkinDescriptor skin) {
        this.skin = skin;
    }

    public SkinDescriptor getSkin() {
        if (isParent()) {
            return skin;
        }
        return SkinDescriptor.EMPTY;
    }

    public VoxelShape getShape() {
        if (cachedRenderShape != null) {
            return cachedRenderShape;
        }
        cachedRenderShape = calcCollisionShape();
        return cachedRenderShape;
    }

    public VoxelShape getCollisionShape() {
        if (noCollision()) {
            return Shapes.empty();
        }
        if (cachedCollisionShape != null) {
            return cachedCollisionShape;
        }
        cachedCollisionShape = calcCollisionShape();
        return cachedCollisionShape;
    }

    public void setLinkedPos(GlobalPos pos) {
        var blockEntity = getParent();
        if (blockEntity != null) {
            blockEntity.linkedPos = pos;
            blockEntity.updateBlockStates();
            blockEntity.updateStateAndNeighbors();
        }
    }

    public GlobalPos getLinkedPos() {
        return getValueFromParent(te -> te.linkedPos).orElse(null);
    }

    public void kill() {
        var level = getLevel();
        if (level != null && !level.isClientSide()) {
            level.setBlockAndUpdate(getBlockPos(), Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public int getContainerSize() {
        return 9 * 9;
    }

    @Override
    protected SimpleContainer getContainer() {
        return getOrCreateItems();
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

    public int getAnalogOutputSignal(Direction dir) {
        return getLinkedValueFromParent((level, pos) -> level.getBlockState(pos).getAnalogOutputSignal(level, pos, dir)).orElse(0);
    }

    public int getSignal(Direction dir) {
        return getLinkedValueFromParent((level, pos) -> level.getBlockState(pos).getSignal(level, pos, dir)).orElse(0);
    }

    public int getDirectSignal(Direction dir) {
        return getLinkedValueFromParent((level, pos) -> level.getBlockState(pos).getDirectSignal(level, pos, dir)).orElse(0);
    }

    public Collection<BlockPos> getRefers() {
        if (refers == null) {
            refers = getValueFromParent(te -> te.refers).orElse(null);
        }
        if (refers == null) {
            return Collections.emptyList();
        }
        return refers;
    }

    public BlockPos getParentPos() {
        return getBlockPos().subtract(reference);
    }

    public OpenVector3d getSeatPos() {
        float dx = 0, dy = 0, dz = 0;
        var parentPos = getParentPos();
        var markers = getMarkers();
        if (markers != null && !markers.isEmpty()) {
            var marker = markers.iterator().next();
            dx = marker.x / 16.0f;
            dy = marker.y / 16.0f;
            dz = marker.z / 16.0f;
        }
        return new OpenVector3d(parentPos.getX() + dx, parentPos.getY() + dy, parentPos.getZ() + dz);
    }

    public BlockPos getBedPos() {
        var parentPos = getParentPos();
        var markers = getMarkers();
        if (markers == null || markers.isEmpty()) {
            Direction facing = getBlockState().getOptionalValue(SkinnableBlock.FACING).orElse(Direction.NORTH);
            return parentPos.relative(Rotation.CLOCKWISE_180.rotate(facing));
        }
        SkinMarker marker = markers.iterator().next();
        return parentPos.offset(marker.x / 16, marker.y / 16, marker.z / 16);
    }

    public Collection<SkinMarker> getMarkers() {
        if (markers == null) {
            markers = getValueFromParent(te -> te.markers).orElse(null);
        }
        return markers;
    }

    @Nullable
    public SkinProperties getProperties() {
        if (properties == null) {
            properties = getValueFromParent(te -> te.properties).orElse(null);
        }
        return properties;
    }

    @Nullable
    public SkinnableBlockEntity getParent() {
        if (isParent()) {
            return this;
        }
        if (getLevel() != null) {
            return Objects.safeCast(getLevel().getBlockEntity(getParentPos()), SkinnableBlockEntity.class);
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

    public boolean isEmissive() {
        return getProperty(SkinProperty.BLOCK_GLOWING);
    }

    public boolean isSeat() {
        return getProperty(SkinProperty.BLOCK_SEAT);
    }

    public boolean isBed() {
        return getProperty(SkinProperty.BLOCK_BED);
    }

    public boolean isLinked() {
        return getLinkedPos() != null;
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

    @Nullable
    @Override
    protected <T> T abi$getCapability(IBlockEntityCapability<T> capability, @Nullable OpenDirection dir) {
        return getLinkedValueFromParent((level, pos) -> {
            var state = level.getBlockState(pos);
            var entity = level.getBlockEntity(pos);
            return capability.get(level, pos, state, entity, dir);
        }).orElse(null);
    }

    @Override
    public Optional<OpenQuaternionf> getRenderRotations(BlockState blockState) {
        return EnvironmentExecutor.callOnClient(() -> () -> {
            if (renderRotations != null) {
                return renderRotations;
            }
            var r = getRotations(blockState);
            renderRotations = new OpenQuaternionf(r.x(), r.y(), r.z(), true);
            return renderRotations;
        });
    }

    @Override
    public Optional<OpenRectangle3f> getRenderShape(BlockState blockState) {
        return EnvironmentExecutor.callOnClient(() -> () -> {
            var bakedSkin = SkinBakery.getInstance().loadSkin(TicketManager.TEST.get(getSkin()));
            if (bakedSkin == null) {
                return null;
            }
            var f = 1 / 16f;
            var box = bakedSkin.renderBounds().copy();
            box.transform(OpenMatrix4f.createScaleMatrix(-f, -f, f));
            return box;
        });
    }


    private SimpleContainer getOrCreateItems() {
        if (container == null) {
            container = new SimpleContainer(getContainerSize());
        }
        return container;
    }

    public <V> Optional<V> getValueFromParent(Function<SkinnableBlockEntity, V> getter) {
        var blockEntity = getParent();
        if (blockEntity != null) {
            return Optional.ofNullable(getter.apply(blockEntity));
        }
        return Optional.empty();
    }

    public <V> Optional<V> getLinkedValueFromParent(BiFunction<Level, BlockPos, V> getter) {
        var globalPos = getLinkedPos();
        if (globalPos == null) {
            return Optional.empty();
        }
        // the current in level?
        var level = getLevel();
        if (level == null) {
            return Optional.empty();
        }
        // the target in different dimension?
        if (!Objects.equals(level.dimension(), globalPos.dimension())) {
            var server = level.getServer();
            if (server == null) {
                return Optional.empty(); // can't find dimension.
            }
            level = server.getLevel(globalPos.dimension());
        }
        // we never load the target chunk.
        if (level == null || !level.isLoaded(globalPos.pos()) || callDepth > 10) {
            return Optional.empty();
        }
        callDepth += 1;
        var result = getter.apply(level, globalPos.pos());
        callDepth -= 1;
        return Optional.ofNullable(result);
    }

    private <V> V getProperty(SkinProperty<V> property) {
        var properties = getProperties();
        if (properties != null) {
            return properties.get(property);
        }
        return property.defaultValue();
    }

    private LinkedSnapshot makeLinkedSnapshot() {
        var result = getLinkedValueFromParent((level, pos) -> {
            var snapshot = new LinkedSnapshot();
            var state = level.getBlockState(pos);
            if (state.hasAnalogOutputSignal()) {
                for (var dir : Direction.values()) {
                    snapshot.analogOutputSignal[dir.get3DDataValue()] = state.getAnalogOutputSignal(level, pos, dir);
                }
            }
            for (var dir : Direction.values()) {
                snapshot.redstoneSignal[dir.get3DDataValue()] = state.getSignal(level, pos, dir);
                snapshot.directRedstoneSignal[dir.get3DDataValue()] = state.getDirectSignal(level, pos, dir);
            }
            return snapshot;
        });
        return result.orElse(null);
    }

    private VoxelShape calcCollisionShape() {
        if (collisionShape.equals(OpenRectangle3i.ZERO)) {
            return Shapes.block();
        }
        float minX = collisionShape.minX() / 16f + 0.5f;
        float minY = collisionShape.minY() / 16f + 0.5f;
        float minZ = collisionShape.minZ() / 16f + 0.5f;
        float maxX = collisionShape.maxX() / 16f + 0.5f;
        float maxY = collisionShape.maxY() / 16f + 0.5f;
        float maxZ = collisionShape.maxZ() / 16f + 0.5f;
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }


    private static class LinkedSnapshot {

        private final int[] analogOutputSignal = new int[]{0, 0, 0, 0, 0, 0};
        private final int[] redstoneSignal = new int[]{0, 0, 0, 0, 0, 0};
        private final int[] directRedstoneSignal = new int[]{0, 0, 0, 0, 0, 0};

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LinkedSnapshot snapshot)) return false;
            return Arrays.equals(analogOutputSignal, snapshot.analogOutputSignal) && Arrays.equals(redstoneSignal, snapshot.redstoneSignal) && Arrays.equals(directRedstoneSignal, snapshot.directRedstoneSignal);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(analogOutputSignal);
            result = 31 * result + Arrays.hashCode(redstoneSignal);
            result = 31 * result + Arrays.hashCode(directRedstoneSignal);
            return result;
        }
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<BlockPos> REFERENCE = IDataSerializerKey.create("Refer", ExtraCodecs.BLOCK_POS, BlockPos.ZERO);
        public static final IDataSerializerKey<OpenRectangle3i> SHAPE = IDataSerializerKey.create("Shape", OpenRectangle3i.CODEC, OpenRectangle3i.ZERO);
        public static final IDataSerializerKey<GlobalPos> LINKED_POS = IDataSerializerKey.create("LinkedPos", ExtraCodecs.GLOBAL_POS, null);
        public static final IDataSerializerKey<SkinDescriptor> SKIN = IDataSerializerKey.create("Skin", SkinDescriptor.CODEC, SkinDescriptor.EMPTY);
        public static final IDataSerializerKey<SkinProperties> SKIN_PROPERTIES = IDataSerializerKey.create("SkinProperties", SkinProperties.CODEC, SkinProperties.EMPTY, SkinProperties.EMPTY::copy);
        public static final IDataSerializerKey<List<BlockPos>> REFERENCES = IDataSerializerKey.create("Refers", ExtraCodecs.BLOCK_POS.listOf(), Collections.emptyList());
        public static final IDataSerializerKey<List<SkinMarker>> MARKERS = IDataSerializerKey.create("Markers", SkinMarker.CODEC.listOf(), Collections.emptyList());
    }
}
