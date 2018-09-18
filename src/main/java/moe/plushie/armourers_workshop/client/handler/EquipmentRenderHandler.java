package moe.plushie.armourers_workshop.client.handler;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import moe.plushie.armourers_workshop.api.client.render.ISkinRenderHandler;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkin;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.armourer.ModelHand;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.data.PlayerPointer;
import moe.plushie.armourers_workshop.common.skin.EquipmentWardrobeData;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EquipmentRenderHandler implements ISkinRenderHandler {

    public static final EquipmentRenderHandler INSTANCE = new EquipmentRenderHandler();
    
    @Override
    public boolean renderSkinWithHelper(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, null, null, 0, true);
    }

    @Override
    public boolean renderSkinWithHelper(ItemStack stack, ModelBiped modelBiped) {
        if (stack == null) {
            return false;
        }
        return SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, modelBiped, null, 0, true);
    }

    @Override
    public boolean renderSkinWithHelper(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX) {
        if (stack == null) {
            return false;
        }
        ISkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (skinPointer == null) {
            return false;
        }
        return renderSkinWithHelper(skinPointer, limb1, limb2, limb3, headY, headX);
    }
    
    @Override
    public boolean renderSkinWithHelper(ISkinDescriptor skinPointer) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean renderSkinWithHelper(ISkinDescriptor skinPointer,
            ModelBiped modelBiped) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean renderSkinWithHelper(ISkinDescriptor skinPointer, float limb1, float limb2, float limb3, float headY, float headX) {
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
        ISkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (skinPointer != null) {
            return renderSkin(skinPointer);
        }
        return false;
    }
    
    @Override
    public boolean renderSkin(ISkinDescriptor skinPointer) {
        ISkinType skinType= skinPointer.getIdentifier().getSkinType();
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            //TODO Offset each part when rendering.
            ISkinPartType skinPartType = skinType.getSkinParts().get(i);
            renderSkinPart(skinPointer, skinPartType);
        }
        return false;
    }
    
    @Override
    public boolean renderSkinPart(ISkinDescriptor skinPointer, ISkinPartType skinPartType) {
        if (skinPointer == null | skinPartType == null) {
            return false;
        }
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            return false;
        }
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            if (skinPart.getPartType() == skinPartType) {
                SkinPartRenderer.INSTANCE.renderPart(skinPart, 0.0625F, skinPointer.getSkinDye(), null, true);
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
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        return isSkinInModelCache(skinPointer);
    }
    
    @Override
    public boolean isSkinInModelCache(ISkinDescriptor skinPointer) {
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
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        requestSkinModelFromSever(skinPointer);
    }

    @Override
    public void requestSkinModelFromSever(ISkinDescriptor skinPointer) {
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
    public ISkin getSkinFromModelCache(ISkinDescriptor skinPointer) {
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
