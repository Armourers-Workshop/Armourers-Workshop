package riskyken.armourersWorkshop.api.common.skin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public interface ISkinDataHandler {
    
    public boolean setSkinOnPlayer(EntityPlayer player, ItemStack stack);
    
    public ItemStack getSkinFormPlayer(EntityPlayer player, ISkinType skinType);
    
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType);
    
    public boolean stackHasSkinPointer(ItemStack stack);
    
    public ISkinPointer getSkinPointerFromStack(ItemStack stack);
    
    public void saveSkinPointerOnStack(ISkinPointer skinPointer, ItemStack stack);
    
    public boolean compoundHasSkinPointer(NBTTagCompound compound);
    
    public ISkinPointer readSkinPointerFromCompound(NBTTagCompound compound);
    
    public void writeSkinPointerToCompound(ISkinPointer skinPointer, NBTTagCompound compound);
    
    /**
     * Checks if the armour render has been overridden for this slot.
     * @param player
     * @param slotId
     * @return
     */
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId);
}
