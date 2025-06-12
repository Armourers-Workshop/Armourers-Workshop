package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.block.HologramProjectorBlock;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.SimpleContainer;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class HologramProjectorBlockEntity extends RotableContainerBlockEntity {

    private static final Map<?, OpenVector3f> FACING_TO_ROT = Collections.immutableMap(it -> {
        it.put(Pair.of(AttachFace.CEILING, Direction.EAST), new OpenVector3f(180, 270, 0));
        it.put(Pair.of(AttachFace.CEILING, Direction.NORTH), new OpenVector3f(180, 180, 0));
        it.put(Pair.of(AttachFace.CEILING, Direction.WEST), new OpenVector3f(180, 90, 0));
        it.put(Pair.of(AttachFace.CEILING, Direction.SOUTH), new OpenVector3f(180, 0, 0));
        it.put(Pair.of(AttachFace.WALL, Direction.EAST), new OpenVector3f(270, 0, 270));
        it.put(Pair.of(AttachFace.WALL, Direction.SOUTH), new OpenVector3f(270, 0, 180));
        it.put(Pair.of(AttachFace.WALL, Direction.WEST), new OpenVector3f(270, 0, 90));
        it.put(Pair.of(AttachFace.WALL, Direction.NORTH), new OpenVector3f(270, 0, 0));
        it.put(Pair.of(AttachFace.FLOOR, Direction.EAST), new OpenVector3f(0, 270, 0));
        it.put(Pair.of(AttachFace.FLOOR, Direction.SOUTH), new OpenVector3f(0, 180, 0));
        it.put(Pair.of(AttachFace.FLOOR, Direction.WEST), new OpenVector3f(0, 90, 0));
        it.put(Pair.of(AttachFace.FLOOR, Direction.NORTH), new OpenVector3f(0, 0, 0));
    });

    private final SimpleContainer container = new SimpleContainer(1);

    private OpenQuaternionf renderRotations;

    private int powerMode = 0;
    private float modelScale = 1.0f;

    private boolean isGlowing = true;
    private boolean isPowered = false;
    private boolean showRotationPoint = false;

    private OpenVector3f modelAngle = OpenVector3f.ZERO;
    private OpenVector3f modelOffset = OpenVector3f.ZERO;

    private OpenVector3f rotationSpeed = OpenVector3f.ZERO;
    private OpenVector3f rotationOffset = OpenVector3f.ZERO;

    public HologramProjectorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        container.deserialize(serializer);
        modelAngle = serializer.read(CodingKeys.ANGLE);
        modelOffset = serializer.read(CodingKeys.OFFSET);
        rotationSpeed = serializer.read(CodingKeys.ROTATION_SPEED);
        rotationOffset = serializer.read(CodingKeys.ROTATION_OFFSET);
        isGlowing = serializer.read(CodingKeys.IS_GLOWING);
        isPowered = serializer.read(CodingKeys.IS_POWERED);
        modelScale = serializer.read(CodingKeys.SCALE);
        powerMode = serializer.read(CodingKeys.POWER_MODE);
        setRenderChanged();
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        container.serialize(serializer);
        serializer.write(CodingKeys.ANGLE, modelAngle);
        serializer.write(CodingKeys.OFFSET, modelOffset);
        serializer.write(CodingKeys.ROTATION_SPEED, rotationSpeed);
        serializer.write(CodingKeys.ROTATION_OFFSET, rotationOffset);
        serializer.write(CodingKeys.IS_GLOWING, isGlowing);
        serializer.write(CodingKeys.IS_POWERED, isPowered);
        serializer.write(CodingKeys.SCALE, modelScale);
        serializer.write(CodingKeys.POWER_MODE, powerMode);
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

    public int powerMode() {
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
        if (level != null && !SkinDescriptor.of(container.getItem(0)).isEmpty()) {
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
    protected SimpleContainer getContainer() {
        return container;
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

    public OpenVector3f getRotationSpeed() {
        return this.rotationSpeed;
    }

    public void setRotationSpeed(OpenVector3f rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
        this.updateBlockStates();
    }

    public OpenVector3f getRotationOffset() {
        return this.rotationOffset;
    }

    public void setRotationOffset(OpenVector3f rotationOffset) {
        this.rotationOffset = rotationOffset;
        this.updateBlockStates();
    }

    public OpenVector3f getModelOffset() {
        return this.modelOffset;
    }

    public void setModelOffset(OpenVector3f modelOffset) {
        this.modelOffset = modelOffset;
        this.updateBlockStates();
    }

    public OpenVector3f getModelAngle() {
        return this.modelAngle;
    }

    public void setModelAngle(OpenVector3f modelAngle) {
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
        var rot = FACING_TO_ROT.getOrDefault(Pair.of(face, facing), OpenVector3f.ZERO);
        renderRotations = new OpenQuaternionf(rot.x(), rot.y(), rot.z(), true);
        return renderRotations;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public OpenRectangle3f getRenderShape(BlockState blockState) {
        if (!isPowered()) {
            return null;
        }
        var descriptor = SkinDescriptor.of(getItem(0));
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.TEST);
        if (bakedSkin == null) {
            return null;
        }
        var rect = bakedSkin.renderBounds();
        var f = 1 / 16f;
        var scale = getModelScale() * f;
        var modelRadius = 0.0f;
        var rotationRadius = 0.0f;

        if (!rect.equals(OpenRectangle3f.ZERO)) {
            float x = Math.max(Math.abs(rect.minX()), Math.abs(rect.maxX()));
            float y = Math.max(Math.abs(rect.minY()), Math.abs(rect.maxY()));
            float z = Math.max(Math.abs(rect.minZ()), Math.abs(rect.maxZ()));
            modelRadius = OpenMath.sqrt(x * x + y * y + z * z);
        }

        if (!rotationOffset.equals(OpenVector3f.ZERO)) {
            var x = Math.abs(rotationOffset.x());
            var y = Math.abs(rotationOffset.y());
            var z = Math.abs(rotationOffset.z());
            rotationRadius = OpenMath.sqrt(x * x + y * y + z * z);
        }

        var tr = (rotationRadius + modelRadius) * scale;
        var tx = (modelOffset.x()) * scale;
        var ty = (modelOffset.y()) * scale + 0.5f;
        var tz = (modelOffset.z()) * scale;

        if (isOverrideOrigin()) {
            ty += rect.maxY() * scale;
        }

        return new OpenRectangle3f(tx - tr, ty - tr, tz - tr, tr * 2, tr * 2, tr * 2);
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<OpenVector3f> ANGLE = IDataSerializerKey.create("Angle", OpenVector3f.CODEC, OpenVector3f.ZERO);
        public static final IDataSerializerKey<OpenVector3f> OFFSET = IDataSerializerKey.create("Offset", OpenVector3f.CODEC, OpenVector3f.ZERO);
        public static final IDataSerializerKey<OpenVector3f> ROTATION_SPEED = IDataSerializerKey.create("RotSpeed", OpenVector3f.CODEC, OpenVector3f.ZERO);
        public static final IDataSerializerKey<OpenVector3f> ROTATION_OFFSET = IDataSerializerKey.create("RotOffset", OpenVector3f.CODEC, OpenVector3f.ZERO);
        public static final IDataSerializerKey<Boolean> IS_GLOWING = IDataSerializerKey.create("Glowing", IDataCodec.BOOL, true);
        public static final IDataSerializerKey<Boolean> IS_POWERED = IDataSerializerKey.create("Powered", IDataCodec.BOOL, false);
        public static final IDataSerializerKey<Float> SCALE = IDataSerializerKey.create("Scale", IDataCodec.FLOAT, 1.0f);
        public static final IDataSerializerKey<Integer> POWER_MODE = IDataSerializerKey.create("PowerMode", IDataCodec.INT, 0);
    }
}
