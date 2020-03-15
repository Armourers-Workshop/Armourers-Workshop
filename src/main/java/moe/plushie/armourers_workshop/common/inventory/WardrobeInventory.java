package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.utils.NBTHelper;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WardrobeInventory extends ModInventory {
    
    private final ISkinType skinType;
    
    public WardrobeInventory(IInventoryCallback callback, ISkinType skinType, int size) {
        super("wardrobe", size, callback);
        this.skinType = skinType;
    }
    
    public ISkinType getSkinType() {
        return skinType;
    }
    
    public void writeItemsToNBT(NBTTagCompound compound) {
        NBTHelper.writeStackArrayToNBT(compound, skinType.getRegistryName(), slots);
    }
    
    public void readItemsFromNBT(NBTTagCompound compound) {
        clear();
        NBTHelper.readStackArrayFromNBT(compound, skinType.getRegistryName(), slots);
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }
    
    public void dropItems(World world, Vec3d pos) {
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty()) {
                UtilItems.spawnItemInWorld(world, pos, stack);
                setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
    }
}
