package moe.plushie.armourers_workshop.builder.tileentity;

import moe.plushie.armourers_workshop.core.tileentity.AbstractContainerTileEntity;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName = name;
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
    }

    public String getItemFlavour() {
        return itemFlavour;
    }

    public void setItemFlavour(String flavour) {
        this.itemFlavour = flavour;
        TileEntityUpdateCombiner.combine(this, this::sendBlockUpdates);
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
}


