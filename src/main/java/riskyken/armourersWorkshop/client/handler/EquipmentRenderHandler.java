package riskyken.armourersWorkshop.client.handler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.client.render.ISkinRenderHandler;
import riskyken.armourersWorkshop.api.common.skin.data.ISkin;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelHand;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.render.SkinPartRenderer;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class EquipmentRenderHandler implements ISkinRenderHandler {

    public static final EquipmentRenderHandler INSTANCE = new EquipmentRenderHandler();
    
    @Override
    public boolean renderSkinWithHelper(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, null, null);
    }

    @Override
    public boolean renderSkinWithHelper(ItemStack stack, ModelBiped modelBiped) {
        if (stack == null) {
            return false;
        }
        return SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, modelBiped, null);
    }

    @Override
    public boolean renderSkinWithHelper(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX) {
        if (stack == null) {
            return false;
        }
        ISkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer == null) {
            return false;
        }
        return renderSkinWithHelper(skinPointer, limb1, limb2, limb3, headY, headX);
    }
    
    @Override
    public boolean renderSkinWithHelper(ISkinPointer skinPointer) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean renderSkinWithHelper(ISkinPointer skinPointer,
            ModelBiped modelBiped) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean renderSkinWithHelper(ISkinPointer skinPointer, float limb1, float limb2, float limb3, float headY, float headX) {
        if (skinPointer == null) {
            return false;
        }
        return SkinModelRenderer.INSTANCE.renderEquipmentPartFromSkinPointer(skinPointer, limb1, limb2, limb3, headY, headX);
    }
    
    @Override
    public boolean renderSkin(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        ISkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer != null) {
            return renderSkin(skinPointer);
        }
        return false;
    }
    
    @Override
    public boolean renderSkin(ISkinPointer skinPointer) {
        ISkinType skinType= skinPointer.getSkinType();
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            //TODO Offset each part when rendering.
            ISkinPartType skinPartType = skinType.getSkinParts().get(i);
            renderSkinPart(skinPointer, skinPartType);
        }
        return false;
    }
    
    @Override
    public boolean renderSkinPart(ISkinPointer skinPointer, ISkinPartType skinPartType) {
        if (skinPointer == null | skinPartType == null) {
            return false;
        }
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            return false;
        }
        skin.onUsed();
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            if (skinPart.getPartType() == skinPartType) {
                SkinPartRenderer.INSTANCE.renderPart(skinPart, 0.0625F, skinPointer.getSkinDye(), null);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isSkinInModelCache(ItemStack stack) {
        if (!SkinNBTHelper.stackHasSkinData(stack)) {
            return false;
        }
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        return isSkinInModelCache(skinPointer);
    }
    
    @Override
    public boolean isSkinInModelCache(ISkinPointer skinPointer) {
        if (skinPointer == null) {
            return false;
        }
        return ClientSkinCache.INSTANCE.isSkinInCache(skinPointer);
    }
    
    @Override
    public void requestSkinModelFromSever(ItemStack stack) {
        if (!SkinNBTHelper.stackHasSkinData(stack)) {
            return;
        }
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        requestSkinModelFromSever(skinPointer);
    }

    @Override
    public void requestSkinModelFromSever(ISkinPointer skinPointer) {
        if (skinPointer == null) {
            return;
        }
        ClientSkinCache.INSTANCE.requestSkinFromServer(skinPointer);
    }

    @Override
    public ModelBase getArmourerHandModel() {
        return getHandModel();
    }
    
    @SideOnly(Side.CLIENT)
    private ModelBase getHandModel() {
        return ModelHand.MODEL;
    }

    @Override
    public ISkin getSkinFromModelCache(ISkinPointer skinPointer) {
        if (skinPointer == null) {
            return null;
        }
        return ClientSkinCache.INSTANCE.getSkin(skinPointer);
    }
    
    @Override
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId) {
        if (slotId < 4 & slotId >= 0) {
            return false;
        }
        if (player == null) {
            return false;
        }
        EquipmentWardrobeHandler ewh = ClientProxy.equipmentWardrobeHandler;
        EquipmentWardrobeData ewd = ewh.getEquipmentWardrobeData(new PlayerPointer(player));
        if (ewd != null) {
            return ewd.armourOverride.get(slotId);
        }
        return false;
    }
}
