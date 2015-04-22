package riskyken.armourersWorkshop.api.common.skin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public interface ISkinDataHandler {
    
    /**
     * Sets the equipment stack for a player.
     * @param player Entity to add the custom equipment to.
     * @param stack Armour type to add.
     */
    public boolean setSkinOnPlayer(EntityPlayer player, ItemStack stack);
    
    public ItemStack getSkinForPlayer(EntityPlayer player, ISkinType skinType);
    
    public void removeSkinTypeFromPlayer(EntityPlayer player, ISkinType skinType);
    
    public ISkinType getSkinTypeFromStack(ItemStack stack);
    
    public boolean stackHasSkinPointer(ItemStack stack);
    
    public ISkinPointer getSkinPointerFromStack(ItemStack stack);
    
    /**
     * Checks if the armour render has been overridden for this slot.
     * @param player
     * @param slotId
     * @return
     */
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId);
}
