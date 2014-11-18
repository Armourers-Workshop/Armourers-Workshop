package riskyken.armourersWorkshop.client.handler;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.client.render.EquipmentItemRenderCache;
import riskyken.armourersWorkshop.client.render.EquipmentPlayerRenderCache;

public class EquipmentRenderHandler implements IEquipmentRenderHandler {

    public static final EquipmentRenderHandler INSTANCE = new EquipmentRenderHandler();
    
    @Override
    public void renderCustomEquipmentFromStack(ItemStack stack) {
        EquipmentPlayerRenderCache.INSTANCE.renderEquipmentPartFromStack(stack, null);
    }

    @Override
    public void renderCustomEquipmentFromStack(ItemStack stack, ModelBiped modelBiped) {
        EquipmentPlayerRenderCache.INSTANCE.renderEquipmentPartFromStack(stack, modelBiped);
    }
    
    @Override
    public void renderCustomEquipmentFromStack(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX) {
        EquipmentPlayerRenderCache.INSTANCE.renderEquipmentPartFromStack(stack, limb1, limb2, limb3, headY, headX);
    }
    
    @Override
    public int getItemModelRenderCacheSize() {
        return EquipmentItemRenderCache.getCacheSize();
    }

    @Override
    public int getEntityModelRenderCacheSize() {
        return EquipmentPlayerRenderCache.INSTANCE.getCacheSize();
    }
}
