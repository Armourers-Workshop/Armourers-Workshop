package moe.plushie.armourers_workshop.core.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.base.AWTileEntities;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class HologramProjectorTileEntity extends LockableLootTileEntity {

    private static final AxisAlignedBB IGNORED_RENDER_BOX = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

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
    private AxisAlignedBB renderBoundingBox;

    private float modelScale = 1.0f;

    private boolean isGlowing = true;
    private boolean isRunning = true;
    private boolean showRotationPoint = false;
    private int powerMode = 0;

    private Vector3f modelOffset = new Vector3f();
    private Vector3f modelAngle = new Vector3f();

    private Vector3f rotationSpeed = new Vector3f();
    private Vector3f rotationOffset = new Vector3f();

    public HologramProjectorTileEntity() {
        super(AWTileEntities.HOLOGRAM_PROJECTOR);
    }

    public void readFromNBT(CompoundNBT nbt) {
        ItemStackHelper.loadAllItems(nbt, items);
        modelAngle = AWDataSerializers.getVector3f(nbt, AWConstants.NBT.TILE_ENTITY_ANGLE);
        modelOffset = AWDataSerializers.getVector3f(nbt, AWConstants.NBT.TILE_ENTITY_OFFSET);
        rotationSpeed = AWDataSerializers.getVector3f(nbt, AWConstants.NBT.TILE_ENTITY_ROTATION_SPEED);
        rotationOffset = AWDataSerializers.getVector3f(nbt, AWConstants.NBT.TILE_ENTITY_ROTATION_OFFSET);
        modelScale = AWDataSerializers.getFloat(nbt, AWConstants.NBT.ENTITY_SCALE, 1.0f);
        powerMode = AWDataSerializers.getInt(nbt, AWConstants.NBT.TILE_ENTITY_POWER_MODE, 0);
        isGlowing = AWDataSerializers.getBoolean(nbt, AWConstants.NBT.TILE_ENTITY_IS_GLOWING, true);
        isRunning = AWDataSerializers.getBoolean(nbt, AWConstants.NBT.TILE_ENTITY_POWERED, false);
        renderBoundingBox = null;
    }

    public void writeToNBT(CompoundNBT nbt) {
        ItemStackHelper.saveAllItems(nbt, items);
        AWDataSerializers.putVector3f(nbt, AWConstants.NBT.TILE_ENTITY_ANGLE, modelAngle);
        AWDataSerializers.putVector3f(nbt, AWConstants.NBT.TILE_ENTITY_OFFSET, modelOffset);
        AWDataSerializers.putVector3f(nbt, AWConstants.NBT.TILE_ENTITY_ROTATION_SPEED, rotationSpeed);
        AWDataSerializers.putVector3f(nbt, AWConstants.NBT.TILE_ENTITY_ROTATION_OFFSET, rotationOffset);
        AWDataSerializers.putFloat(nbt, AWConstants.NBT.ENTITY_SCALE, modelScale, 1.0f);
        AWDataSerializers.putInt(nbt, AWConstants.NBT.TILE_ENTITY_POWER_MODE, powerMode, 0);
        AWDataSerializers.putBoolean(nbt, AWConstants.NBT.TILE_ENTITY_IS_GLOWING, isGlowing, true);
        AWDataSerializers.putBoolean(nbt, AWConstants.NBT.TILE_ENTITY_POWERED, isRunning, false);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.readFromNBT(nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        this.writeToNBT(nbt);
        return nbt;
    }

    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.writeToNBT(nbt);
        return new SUpdateTileEntityPacket(this.worldPosition, 3, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        this.readFromNBT(nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        this.writeToNBT(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        this.readFromNBT(tag);
    }

    public void updatePowerStats() {
        boolean newValue = isRunningForState(getBlockState());
        if (newValue != isRunning) {
            updateBlockStates();
        }
    }

    public void updateBlockStates() {
        BlockState state = getBlockState();
        isRunning = isRunningForState(state);
        renderBoundingBox = null;
        boolean growing = isRunning && isGlowing;
        if (level != null && !level.isClientSide) {
            if (state.getValue(HologramProjectorBlock.LIT) != growing) {
                BlockState newState = state.setValue(HologramProjectorBlock.LIT, growing);
                level.setBlock(getBlockPos(), newState, 2);
            } else {
                level.sendBlockUpdated(getBlockPos(), state, state, 2);
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

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isRunningForState(BlockState state) {
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
        return items.size();
    }

    @Override
    protected ITextComponent getDefaultName() {
        return TranslateUtils.title("block.armourers_workshop.hologram-projector");
    }

    @Override
    protected Container createMenu(int containerId, PlayerInventory inventory) {
        return null;
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

    @OnlyIn(Dist.CLIENT)
    public void setRenderBoundingBoxWithRect(Rectangle3f rect) {
        if (renderBoundingBox != null) {
            return;
        }
        float f = 1 / 16f;
        float scale = getModelScale() * f;
        float modelRadius = 0.0f;
        float rotationRadius = 0.0f;

        if (!rect.equals(Rectangle3f.ZERO)) {
            double x = MathHelper.absMax(rect.getMinX(), rect.getMaxX());
            double y = MathHelper.absMax(rect.getMinY(), rect.getMaxY());
            double z = MathHelper.absMax(rect.getMinZ(), rect.getMaxZ());
            modelRadius = MathHelper.sqrt(x * x + y * y + z * z);
        }

        if (!rotationOffset.equals(AWConstants.ZERO)) {
            float x = Math.abs(rotationOffset.x());
            float y = Math.abs(rotationOffset.y());
            float z = Math.abs(rotationOffset.z());
            rotationRadius = MathHelper.sqrt(x * x + y * y + z * z);
        }

        float tr = (rotationRadius + modelRadius) * scale;
        float tx = (modelOffset.x()) * scale;
        float ty = (modelOffset.y()) * scale + 0.5f;
        float tz = (modelOffset.z()) * scale;

        if (isOverrideOrigin()) {
            ty += rect.getMaxY() * scale;
        }

        Rectangle3f rect1 = new Rectangle3f(tx - tr, ty - tr, tz - tr, tr * 2, tr * 2, tr * 2);
        rect1.mul(getRenderRotations());
        renderBoundingBox = rect1.asAxisAlignedBB().move(Vector3d.atCenterOf(getBlockPos()));
    }

    @OnlyIn(Dist.CLIENT)
    public Quaternion getRenderRotations() {
        if (renderRotations != null) {
            return renderRotations;
        }
        AttachFace face = getBlockState().getValue(HologramProjectorBlock.FACE);
        Direction facing = getBlockState().getValue(HologramProjectorBlock.FACING);
        Vector3f rot = FACING_TO_ROT.getOrDefault(Pair.of(face, facing), new Vector3f());
        renderRotations = new Quaternion(rot.x(), rot.y(), rot.z(), true);
        return renderRotations;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (!isRunning()) {
            return IGNORED_RENDER_BOX;
        }
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        return super.getRenderBoundingBox();
    }
}
