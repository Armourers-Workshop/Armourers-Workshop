package moe.plushie.armourers_workshop.builder.tileentity;

import moe.plushie.armourers_workshop.api.painting.IPaintColorProvider;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.color.PaintColor;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class ColourMixerTileEntity extends TileEntity implements IPaintColorProvider {

    private PaintColor color = PaintColor.WHITE;

    public ColourMixerTileEntity() {
        super(ModTileEntities.COLOUR_MIXER);
    }

    public void readFromNBT(CompoundNBT nbt) {
        color = AWDataSerializers.getPaintColor(nbt, AWConstants.NBT.COLOR, PaintColor.WHITE);
    }

    public void writeToNBT(CompoundNBT nbt) {
        AWDataSerializers.putPaintColor(nbt, AWConstants.NBT.COLOR, color, PaintColor.WHITE);
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
        this.sendBlockUpdates();
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

    @Override
    public PaintColor getColor() {
        return color;
    }

    public void setColor(PaintColor color) {
        this.color = color;
        this.setChanged();
        this.sendBlockUpdates();
    }

    private void sendBlockUpdates() {
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.BLOCK_UPDATE);
        }
    }
}
