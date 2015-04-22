package riskyken.armourersWorkshop.client.handler;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.client.render.ISkinRenderHandler;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

public class EquipmentRenderHandler implements ISkinRenderHandler {

    public static final EquipmentRenderHandler INSTANCE = new EquipmentRenderHandler();
    
    @Override
    public boolean renderSkinFromStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return EquipmentModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, null);
    }

    @Override
    public boolean renderSkinFromStack(ItemStack stack, ModelBiped modelBiped) {
        if (stack == null) {
            return false;
        }
        return EquipmentModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, modelBiped);
    }

    @Override
    public boolean renderSkinFromStack(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX) {
        if (stack == null) {
            return false;
        }
        return EquipmentModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, limb1, limb2, limb3, headY, headX);
    }
    
    @Override
    public boolean renderSkinFromPointer(ISkinPointer skinPointer) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isSkinInModelCache(ItemStack stack) {
        if (!EquipmentNBTHelper.stackHasSkinData(stack)) {
            return false;
        }
        SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(stack);
        return isSkinInModelCache(skinPointer);
    }
    
    @Override
    public boolean isSkinInModelCache(ISkinPointer skinPointer) {
        if (skinPointer == null) {
            return false;
        }
        return ClientEquipmentModelCache.INSTANCE.isEquipmentInCache(skinPointer.getSkinId());
    }
    
    @Override
    public void requestSkinModelFromSever(ItemStack stack) {
        if (!EquipmentNBTHelper.stackHasSkinData(stack)) {
            return;
        }
        SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(stack);
        requestSkinModelFromSever(skinPointer);
    }

    @Override
    public void requestSkinModelFromSever(ISkinPointer skinPointer) {
        if (skinPointer == null) {
            return;
        }
        ClientEquipmentModelCache.INSTANCE.requestEquipmentDataFromServer(skinPointer.getSkinId());
    }
}
