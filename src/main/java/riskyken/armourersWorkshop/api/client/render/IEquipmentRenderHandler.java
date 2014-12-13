package riskyken.armourersWorkshop.api.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;

public interface IEquipmentRenderHandler {

    public void renderCustomEquipmentFromStack(ItemStack stack);
    
    public void renderCustomEquipmentFromStack(ItemStack stack, ModelBiped modelBiped);
    
    public void renderCustomEquipmentFromStack(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX);
    
    public void renderCustomEquipmentFromStack(ItemStack[] stacks);
    
    public void renderCustomEquipmentFromStack(ItemStack[] stacks, ModelBiped modelBiped);
    
    public void renderCustomEquipmentFromStack(ItemStack[] stacks, float limb1, float limb2, float limb3, float headY, float headX);
    
    
    /**
     * Get the number of items in the cache for entity models.
     * @return Number of items in the cache.
     */
    public int getEntityModelRenderCacheSize();
}
