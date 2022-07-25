package moe.plushie.armourers_workshop.core.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.core.block.HologramProjectorBlock;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModBlockEntities;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class HologramProjectorBlockEntity extends RotableContainerBlockEntity {

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

    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private Quaternion renderRotations;

    private int powerMode = 0;
    private float modelScale = 1.0f;

    private boolean isGlowing = true;
    private boolean isPowered = false;
    private boolean showRotationPoint = false;

    private Vector3f modelAngle = new Vector3f();
    private Vector3f modelOffset = new Vector3f();

    private Vector3f rotationSpeed = new Vector3f();
    private Vector3f rotationOffset = new Vector3f();

    public HologramProjectorBlockEntity() {
        super(ModBlockEntities.HOLOGRAM_PROJECTOR.get());
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        DataSerializers.loadAllItems(nbt, items);
        modelAngle = DataSerializers.getVector3f(nbt, Constants.Key.TILE_ENTITY_ANGLE);
        modelOffset = DataSerializers.getVector3f(nbt, Constants.Key.TILE_ENTITY_OFFSET);
        rotationSpeed = DataSerializers.getVector3f(nbt, Constants.Key.TILE_ENTITY_ROTATION_SPEED);
        rotationOffset = DataSerializers.getVector3f(nbt, Constants.Key.TILE_ENTITY_ROTATION_OFFSET);
        isGlowing = DataSerializers.getBoolean(nbt, Constants.Key.TILE_ENTITY_IS_GLOWING, true);
        isPowered = DataSerializers.getBoolean(nbt, Constants.Key.TILE_ENTITY_IS_POWERED, false);
        modelScale = DataSerializers.getFloat(nbt, Constants.Key.ENTITY_SCALE, 1.0f);
        powerMode = DataSerializers.getInt(nbt, Constants.Key.TILE_ENTITY_POWER_MODE, 0);
        setRenderChanged();
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        DataSerializers.saveAllItems(nbt, items);
        DataSerializers.putVector3f(nbt, Constants.Key.TILE_ENTITY_ANGLE, modelAngle);
        DataSerializers.putVector3f(nbt, Constants.Key.TILE_ENTITY_OFFSET, modelOffset);
        DataSerializers.putVector3f(nbt, Constants.Key.TILE_ENTITY_ROTATION_SPEED, rotationSpeed);
        DataSerializers.putVector3f(nbt, Constants.Key.TILE_ENTITY_ROTATION_OFFSET, rotationOffset);
        DataSerializers.putBoolean(nbt, Constants.Key.TILE_ENTITY_IS_GLOWING, isGlowing, true);
        DataSerializers.putBoolean(nbt, Constants.Key.TILE_ENTITY_IS_POWERED, isPowered, false);
        DataSerializers.putFloat(nbt, Constants.Key.ENTITY_SCALE, modelScale, 1.0f);
        DataSerializers.putInt(nbt, Constants.Key.TILE_ENTITY_POWER_MODE, powerMode, 0);
    }

    public void updatePowerStats() {
        boolean newValue = isRunningForState(getBlockState());
        if (newValue != isPowered) {
            updateBlockStates();
        }
    }

    public void updateBlockStates() {
        BlockState state = getBlockState();
        isPowered = isRunningForState(state);
        setChanged();
        setRenderChanged();
        boolean growing = isPowered && isGlowing;
        if (level != null && !level.isClientSide) {
            if (state.getValue(HologramProjectorBlock.LIT) != growing) {
                BlockState newState = state.setValue(HologramProjectorBlock.LIT, growing);
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
        if (level != null && !SkinDescriptor.of(items.get(0)).isEmpty()) {
            switch (powerMode) {
                case 1:
                    return level.hasNeighborSignal(getBlockPos());
                case 2:
                    return !level.hasNeighborSignal(getBlockPos());
                default:
                    return true;
            }
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
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
        super.setItem(p_70299_1_, p_70299_2_);
        this.updateBlockStates();
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
    @Environment(value = EnvType.CLIENT)
    public Quaternion getRenderRotations() {
        if (renderRotations != null) {
            return renderRotations;
        }
        AttachFace face = getBlockState().getValue(HologramProjectorBlock.FACE);
        Direction facing = getBlockState().getValue(HologramProjectorBlock.FACING);
        Vector3f rot = FACING_TO_ROT.getOrDefault(Pair.of(face, facing), new Vector3f());
        renderRotations = TrigUtils.rotate(rot.getX(), rot.getY(), rot.getZ(), true);
        return renderRotations;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public Rectangle3f getRenderBoundingBox(BlockState state) {
        if (!isPowered()) {
            return null;
        }
        BakedSkin bakedSkin = BakedSkin.of(getItem(0));
        if (bakedSkin == null) {
            return null;
        }
        Rectangle3f rect = bakedSkin.getRenderBounds(null, null, null, null);
        float f = 1 / 16f;
        float scale = getModelScale() * f;
        float modelRadius = 0.0f;
        float rotationRadius = 0.0f;

        if (!rect.equals(Rectangle3f.ZERO)) {
            double x = MathUtils.absMax(rect.getMinX(), rect.getMaxX());
            double y = MathUtils.absMax(rect.getMinY(), rect.getMaxY());
            double z = MathUtils.absMax(rect.getMinZ(), rect.getMaxZ());
            modelRadius = MathUtils.sqrt(x * x + y * y + z * z);
        }

        if (!rotationOffset.equals(Vector3f.ZERO)) {
            float x = Math.abs(rotationOffset.getX());
            float y = Math.abs(rotationOffset.getY());
            float z = Math.abs(rotationOffset.getZ());
            rotationRadius = MathUtils.sqrt(x * x + y * y + z * z);
        }

        float tr = (rotationRadius + modelRadius) * scale;
        float tx = (modelOffset.getX()) * scale;
        float ty = (modelOffset.getY()) * scale + 0.5f;
        float tz = (modelOffset.getZ()) * scale;

        if (isOverrideOrigin()) {
            ty += rect.getMaxY() * scale;
        }

        return new Rectangle3f(tx - tr, ty - tr, tz - tr, tr * 2, tr * 2, tr * 2);
    }
}
