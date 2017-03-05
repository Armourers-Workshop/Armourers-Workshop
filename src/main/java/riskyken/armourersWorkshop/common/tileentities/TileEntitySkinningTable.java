package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.common.crafting.ItemSkinningRecipes;
import riskyken.armourersWorkshop.common.inventory.IInventorySlotUpdate;
import riskyken.armourersWorkshop.common.inventory.ModInventory;

public class TileEntitySkinningTable extends TileEntity implements IInventorySlotUpdate {

    private final ModInventory craftingInventory;
    private final ModInventory outputInventory;
    
    public TileEntitySkinningTable() {
        craftingInventory = new ModInventory("skinningTablecrafting", 2, this, this);
        outputInventory = new ModInventory("skinningTableOutput", 1, this, this);
    }
    
    @Override
    public boolean canUpdate() {
        return false;
    }
    
    public ModInventory getCraftingInventory() {
        return craftingInventory;
    }
    
    public ModInventory getOutputInventory() {
        return outputInventory;
    }

    @Override
    public void setInventorySlotContents(IInventory inventory, int slotId, ItemStack stack) {
        if (inventory == craftingInventory) {
            checkForValidRecipe();
        }
    }
    
    private void checkForValidRecipe() {
        ItemStack stack = ItemSkinningRecipes.getRecipeOutput(craftingInventory);
        outputInventory.setInventorySlotContents(0, stack);
        markDirty();
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        craftingInventory.saveItemsToNBT(compound);
        outputInventory.saveItemsToNBT(compound);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        craftingInventory.loadItemsFromNBT(compound);
        outputInventory.loadItemsFromNBT(compound);
    }
}
