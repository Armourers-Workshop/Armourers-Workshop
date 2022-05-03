package moe.plushie.armourers_workshop.core.tileentity;

import moe.plushie.armourers_workshop.api.common.IHasInventory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public abstract class AbstractTileEntity extends TileEntity {

    public AbstractTileEntity(TileEntityType<?> entityType) {
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
}
