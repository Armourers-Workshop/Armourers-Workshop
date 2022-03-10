package moe.plushie.armourers_workshop.core.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.base.AWTileEntities;
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

    private Vector3f modelOffset = new Vector3f();
    private Vector3f modelAngle = new Vector3f();

    private Vector3f rotationSpeed = new Vector3f();
    private Vector3f rotationOffset = new Vector3f();

    public HologramProjectorTileEntity() {
        super(AWTileEntities.HOLOGRAM_PROJECTOR);
    }

    protected void readFromNBT(CompoundNBT nbt) {
        ItemStackHelper.loadAllItems(nbt, this.items);
        this.modelAngle = AWDataSerializers.getVector3f(nbt, AWConstants.NBT.HOLOGRAM_PROJECTOR_ANGLE);
        this.modelOffset = AWDataSerializers.getVector3f(nbt, AWConstants.NBT.HOLOGRAM_PROJECTOR_OFFSET);
        this.rotationSpeed = AWDataSerializers.getVector3f(nbt, AWConstants.NBT.HOLOGRAM_PROJECTOR_ROTATION_SPEED);
        this.rotationOffset = AWDataSerializers.getVector3f(nbt, AWConstants.NBT.HOLOGRAM_PROJECTOR_ROTATION_OFFSET);
        this.modelScale = AWDataSerializers.getFloat(nbt, AWConstants.NBT.ENTITY_SCALE, 1.0f);
    }

    protected void writeToNBT(CompoundNBT nbt) {
        ItemStackHelper.saveAllItems(nbt, this.items);
        AWDataSerializers.putVector3f(nbt, AWConstants.NBT.HOLOGRAM_PROJECTOR_ANGLE, this.modelAngle);
        AWDataSerializers.putVector3f(nbt, AWConstants.NBT.HOLOGRAM_PROJECTOR_OFFSET, this.modelOffset);
        AWDataSerializers.putVector3f(nbt, AWConstants.NBT.HOLOGRAM_PROJECTOR_ROTATION_SPEED, this.rotationSpeed);
        AWDataSerializers.putVector3f(nbt, AWConstants.NBT.HOLOGRAM_PROJECTOR_ROTATION_OFFSET, this.rotationOffset);
        AWDataSerializers.putFloat(nbt, AWConstants.NBT.ENTITY_SCALE, this.modelScale, 1.0f);
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

    @Override
    public void setChanged() {
        super.setChanged();
        // tile entity config has changed must recalculate render box
        this.renderBoundingBox = null;
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

    public Vector3f getRotationSpeed() {
        return rotationSpeed;
    }

    public Vector3f getRotationOffset() {
        return rotationOffset;
    }

    public Vector3f getModelOffset() {
        return modelOffset;
    }

    public Vector3f getModelAngle() {
        return modelAngle;
    }

    public float getModelScale() {
        return modelScale;
    }


    public boolean isPowered() {
        return true;
    }

    public boolean isOverrideLight() {
        return true;
    }

    public boolean isOverrideOrigin() {
        return true;
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
        if (!isPowered()) {
            return IGNORED_RENDER_BOX;
        }
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        return super.getRenderBoundingBox();
    }
}
