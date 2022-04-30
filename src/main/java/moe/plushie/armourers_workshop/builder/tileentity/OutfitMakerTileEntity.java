package moe.plushie.armourers_workshop.builder.tileentity;

import moe.plushie.armourers_workshop.core.tileentity.AbstractContainerTileEntity;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class OutfitMakerTileEntity extends AbstractContainerTileEntity {

    private String itemName;
    private String itemFlavour;

    private NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);

    public OutfitMakerTileEntity() {
        super(ModTileEntities.OUTFIT_MAKER);
    }

    public void readFromNBT(CompoundNBT nbt) {
        ItemStackHelper.loadAllItems(nbt, items);
        this.itemName = nbt.getString(AWConstants.NBT.TILE_ENTITY_NAME);
        this.itemFlavour = nbt.getString(AWConstants.NBT.TILE_ENTITY_FLAVOUR);
    }

    public void writeToNBT(CompoundNBT nbt) {
        ItemStackHelper.saveAllItems(nbt, items);
        if (Strings.isNotEmpty(itemName)) {
            nbt.putString(AWConstants.NBT.TILE_ENTITY_NAME, itemName);
        }
        if (Strings.isNotEmpty(itemFlavour)) {
            nbt.putString(AWConstants.NBT.TILE_ENTITY_FLAVOUR, itemFlavour);
        }
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName = name;
        this.setChanged();
        this.sendBlockUpdates();
    }

    public String getItemFlavour() {
        return itemFlavour;
    }

    public void setItemFlavour(String flavour) {
        this.itemFlavour = flavour;
        this.setChanged();
        this.sendBlockUpdates();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 21;
    }

    private void sendBlockUpdates() {
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.BLOCK_UPDATE);
        }
    }
}


