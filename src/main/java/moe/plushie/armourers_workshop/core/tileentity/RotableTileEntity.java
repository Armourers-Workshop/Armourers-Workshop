package moe.plushie.armourers_workshop.core.tileentity;

import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public abstract class RotableTileEntity extends LockableLootTileEntity {

    public static final AxisAlignedBB ZERO_BOX = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    private Quaternion renderRotations;
    private AxisAlignedBB renderBoundingBox;

    public RotableTileEntity(TileEntityType<?> entityType) {
        super(entityType);
    }

    public abstract void readFromNBT(CompoundNBT nbt);

    public abstract void writeToNBT(CompoundNBT nbt);

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
        this.setChanged();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return null;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> p_199721_1_) {

    }

    @Override
    protected ITextComponent getDefaultName() {
        return null;
    }

    @Override
    protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
        return null;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    public void setRenderChanged() {
        renderBoundingBox = null;
    }

    public Quaternion getRenderRotations() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public Rectangle3f getRenderBoundingBox(BlockState state) {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        Rectangle3f rect = getRenderBoundingBox(getBlockState());
        if (rect == null) {
            return ZERO_BOX;
        }
        Quaternion quaternion = getRenderRotations();
        if (quaternion != null) {
            rect.mul(quaternion);
        }
        renderBoundingBox = rect.asAxisAlignedBB().move(Vector3d.atCenterOf(getBlockPos()));
        return renderBoundingBox;
    }
}
