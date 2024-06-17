package moe.plushie.armourers_workshop.core.blockentity;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.core.block.HologramProjectorBlock;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.apache.commons.lang3.tuple.Pair;

public class HologramProjectorBlockEntity extends RotableContainerBlockEntity {

    private static final DataSerializerKey<Vector3f> ANGLE_KEY = DataSerializerKey.create("Angle", DataTypeCodecs.VECTOR_3F, Vector3f.ZERO);
    private static final DataSerializerKey<Vector3f> OFFSET_KEY = DataSerializerKey.create("Offset", DataTypeCodecs.VECTOR_3F, Vector3f.ZERO);
    private static final DataSerializerKey<Vector3f> ROTATION_SPEED_KEY = DataSerializerKey.create("RotSpeed", DataTypeCodecs.VECTOR_3F, Vector3f.ZERO);
    private static final DataSerializerKey<Vector3f> ROTATION_OFFSET_KEY = DataSerializerKey.create("RotOffset", DataTypeCodecs.VECTOR_3F, Vector3f.ZERO);
    private static final DataSerializerKey<Boolean> IS_GLOWING_KEY = DataSerializerKey.create("Glowing", DataTypeCodecs.BOOL, true);
    private static final DataSerializerKey<Boolean> IS_POWERED_KEY = DataSerializerKey.create("Powered", DataTypeCodecs.BOOL, false);
    private static final DataSerializerKey<Float> SCALE_KEY = DataSerializerKey.create("Scale", DataTypeCodecs.FLOAT, 1.0f);
    private static final DataSerializerKey<Integer> POWER_MODE_KEY = DataSerializerKey.create("PowerMode", DataTypeCodecs.INT, 0);

    private static final ImmutableMap<?, Vector3f> FACING_TO_ROT = new ImmutableMap.Builder<Object, Vector3f>()
            .put(Pair.of(AttachFace.CEILING, Direction.EAST), new Vector3f(180, 270, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.NORTH), new Vector3f(180, 180, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.WEST), new Vector3f(180, 90, 0))
            .put(Pair.of(AttachFace.CEILING, Direction.SOUTH), new Vector3f(180, 0, 0))
            .put(Pair.of(AttachFace.WALL, Direction.EAST), new Vector3f(270, 0, 270))
            .put(Pair.of(AttachFace.WALL, Direction.SOUTH), new Vector3f(270, 0, 180))
            .put(Pair.of(AttachFace.WALL, Direction.WEST), new Vector3f(270, 0, 90))
            .put(Pair.of(AttachFace.WALL, Direction.NORTH), new Vector3f(270, 0, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.EAST), new Vector3f(0, 270, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.SOUTH), new Vector3f(0, 180, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.WEST), new Vector3f(0, 90, 0))
            .put(Pair.of(AttachFace.FLOOR, Direction.NORTH), new Vector3f(0, 0, 0))
            .build();

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private OpenQuaternionf renderRotations;

    private int powerMode = 0;
    private float modelScale = 1.0f;

    private boolean isGlowing = true;
    private boolean isPowered = false;
    private boolean showRotationPoint = false;

    private Vector3f modelAngle = Vector3f.ZERO;
    private Vector3f modelOffset = Vector3f.ZERO;

    private Vector3f rotationSpeed = Vector3f.ZERO;
    private Vector3f rotationOffset = Vector3f.ZERO;

    public HologramProjectorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        serializer.readItemList(items);
        modelAngle = serializer.read(ANGLE_KEY);
        modelOffset = serializer.read(OFFSET_KEY);
        rotationSpeed = serializer.read(ROTATION_SPEED_KEY);
        rotationOffset = serializer.read(ROTATION_OFFSET_KEY);
        isGlowing = serializer.read(IS_GLOWING_KEY);
        isPowered = serializer.read(IS_POWERED_KEY);
        modelScale = serializer.read(SCALE_KEY);
        powerMode = serializer.read(POWER_MODE_KEY);
        setRenderChanged();
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        serializer.writeItemList(items);
        serializer.write(ANGLE_KEY, modelAngle);
        serializer.write(OFFSET_KEY, modelOffset);
        serializer.write(ROTATION_SPEED_KEY, rotationSpeed);
        serializer.write(ROTATION_OFFSET_KEY, rotationOffset);
        serializer.write(IS_GLOWING_KEY, isGlowing);
        serializer.write(IS_POWERED_KEY, isPowered);
        serializer.write(SCALE_KEY, modelScale);
        serializer.write(POWER_MODE_KEY, powerMode);
    }

    public void updatePowerStats() {
        boolean newValue = isRunningForState(getBlockState());
        if (newValue != isPowered) {
            updateBlockStates();
        }
    }

    public void updateBlockStates() {
        var state = getBlockState();
        isPowered = isRunningForState(state);
        setChanged();
        setRenderChanged();
        var growing = isPowered && isGlowing;
        var level = getLevel();
        if (level != null && !level.isClientSide()) {
            if (state.getValue(HologramProjectorBlock.LIT) != growing) {
                var newState = state.setValue(HologramProjectorBlock.LIT, growing);
                level.setBlock(getBlockPos(), newState, Constants.BlockFlags.BLOCK_UPDATE);
            } else {
                level.sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.BLOCK_UPDATE);
            }
        }
    }

    public int getPowerMode() {
        return powerMode;
    }

    public void setPowerMode(int powerMode) {
        this.powerMode = powerMode;
        this.updateBlockStates();
    }

    public boolean isPowered() {
        return isPowered;
    }

    protected boolean isRunningForState(BlockState state) {
        var level = getLevel();
        if (level != null && !SkinDescriptor.of(items.get(0)).isEmpty()) {
            return switch (powerMode) {
                case 1 -> level.hasNeighborSignal(getBlockPos());
                case 2 -> !level.hasNeighborSignal(getBlockPos());
                default -> true;
            };
        }
        return false;
    }

    public boolean isOverrideLight() {
        return isGlowing;
    }

    public boolean isOverrideOrigin() {
        return true;
    }

    public boolean isGlowing() {
        return isGlowing;
    }

    public void setGlowing(boolean glowing) {
        this.isGlowing = glowing;
        this.updateBlockStates();
    }

    public void setShowRotationPoint(boolean showRotationPoint) {
        this.showRotationPoint = showRotationPoint;
    }

    public boolean shouldShowRotationPoint() {
        return showRotationPoint;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setContainerChanged() {
        super.setContainerChanged();
        this.updateBlockStates();
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return !SkinDescriptor.of(itemStack).isEmpty();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public Vector3f getRotationSpeed() {
        return this.rotationSpeed;
    }

    public void setRotationSpeed(Vector3f rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
        this.updateBlockStates();
    }

    public Vector3f getRotationOffset() {
        return this.rotationOffset;
    }

    public void setRotationOffset(Vector3f rotationOffset) {
        this.rotationOffset = rotationOffset;
        this.updateBlockStates();
    }

    public Vector3f getModelOffset() {
        return this.modelOffset;
    }

    public void setModelOffset(Vector3f modelOffset) {
        this.modelOffset = modelOffset;
        this.updateBlockStates();
    }

    public Vector3f getModelAngle() {
        return this.modelAngle;
    }

    public void setModelAngle(Vector3f modelAngle) {
        this.modelAngle = modelAngle;
        this.updateBlockStates();
    }

    public float getModelScale() {
        return modelScale;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public OpenQuaternionf getRenderRotations(BlockState blockState) {
        if (renderRotations != null) {
            return renderRotations;
        }
        var face = blockState.getOptionalValue(HologramProjectorBlock.FACE).orElse(AttachFace.FLOOR);
        var facing = blockState.getOptionalValue(HologramProjectorBlock.FACING).orElse(Direction.NORTH);
        var rot = FACING_TO_ROT.getOrDefault(Pair.of(face, facing), Vector3f.ZERO);
        renderRotations = new OpenQuaternionf(rot.getX(), rot.getY(), rot.getZ(), true);
        return renderRotations;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Rectangle3f getRenderShape(BlockState blockState) {
        if (!isPowered()) {
            return null;
        }
        var descriptor = SkinDescriptor.of(getItem(0));
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.TEST);
        if (bakedSkin == null) {
            return null;
        }
        var rect = bakedSkin.getRenderBounds(SkinItemSource.EMPTY);
        var f = 1 / 16f;
        var scale = getModelScale() * f;
        var modelRadius = 0.0f;
        var rotationRadius = 0.0f;

        if (!rect.equals(Rectangle3f.ZERO)) {
            var x = MathUtils.absMax(rect.getMinX(), rect.getMaxX());
            var y = MathUtils.absMax(rect.getMinY(), rect.getMaxY());
            var z = MathUtils.absMax(rect.getMinZ(), rect.getMaxZ());
            modelRadius = MathUtils.sqrt(x * x + y * y + z * z);
        }

        if (!rotationOffset.equals(Vector3f.ZERO)) {
            var x = Math.abs(rotationOffset.getX());
            var y = Math.abs(rotationOffset.getY());
            var z = Math.abs(rotationOffset.getZ());
            rotationRadius = MathUtils.sqrt(x * x + y * y + z * z);
        }

        var tr = (rotationRadius + modelRadius) * scale;
        var tx = (modelOffset.getX()) * scale;
        var ty = (modelOffset.getY()) * scale + 0.5f;
        var tz = (modelOffset.getZ()) * scale;

        if (isOverrideOrigin()) {
            ty += rect.getMaxY() * scale;
        }

        return new Rectangle3f(tx - tr, ty - tr, tz - tr, tr * 2, tr * 2, tr * 2);
    }
}
