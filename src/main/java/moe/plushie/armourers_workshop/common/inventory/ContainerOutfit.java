package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.ModInventory.IInventoryCallback;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerOutfit extends ModContainer implements IInventoryCallback {
    
    private final ItemStack stackOutfit;
    private final SkinInventoryContainer sic;
    
    public ContainerOutfit(InventoryPlayer invPlayer, ItemStack stackOutfit) {
        super(invPlayer);
        this.stackOutfit = stackOutfit;
        
        ISkinType[] skinTypes = new ISkinType[] {
                SkinTypeRegistry.skinHead,
                SkinTypeRegistry.skinChest,
                SkinTypeRegistry.skinLegs,
                SkinTypeRegistry.skinFeet,
                SkinTypeRegistry.skinWings};
        
        sic = new SkinInventoryContainer(this, skinTypes, 2);
        if (!stackOutfit.hasTagCompound()) {
            stackOutfit.setTagCompound(new NBTTagCompound());
        }
        sic.readFromNBT(stackOutfit.getTagCompound());
        
        addPlayerSlots(8, 84);
        
        for (int skinIndex = 0; skinIndex < skinTypes.length; skinIndex++) {
            ISkinType skinType = skinTypes[skinIndex];
            WardrobeInventory inventory = sic.getSkinTypeInv(skinType);
            
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                addSlotToContainer(new SlotSkin(skinType, inventory, i, 8 + 20 * skinIndex, 20 + 20 * i));
            }
        }
    }
    
    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        return super.transferStackFromPlayer(playerIn, index);
    }

    @Override
    public void setInventorySlotContents(IInventory inventory, int index, ItemStack stack) {
    }

    @Override
    public void dirty() {
        sic.writeToNBT(stackOutfit.getTagCompound());
    }
}
