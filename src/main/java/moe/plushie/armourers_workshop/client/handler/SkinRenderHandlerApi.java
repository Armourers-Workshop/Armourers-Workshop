package moe.plushie.armourers_workshop.client.handler;

import moe.plushie.armourers_workshop.api.client.render.ISkinRenderHandler;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkin;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.client.model.armourer.ModelHand;
import moe.plushie.armourers_workshop.client.model.skin.ModelTypeHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper.ModelType;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;

public class SkinRenderHandlerApi implements ISkinRenderHandler {

    public static final SkinRenderHandlerApi INSTANCE = new SkinRenderHandlerApi();

    @Override
    public boolean renderSkinWithHelper(ItemStack stack) {

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean renderSkinWithHelper(ItemStack stack, ModelBiped modelBiped) {
        if (stack.isEmpty()) {
            return false;
        }
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (descriptor == null) {
            return false;
        }
        ModelTypeHelper modelTypeHelper = SkinModelRenderHelper.INSTANCE.getTypeHelperForModel(ModelType.MODEL_BIPED, descriptor.getIdentifier().getSkinType());
        Skin skin = ClientSkinCache.INSTANCE.getSkin(descriptor);
        if (skin == null) {
            return false;
        }
        modelTypeHelper.render(null, skin, modelBiped, true, descriptor.getSkinDye(), ExtraColours.EMPTY_COLOUR, false, 0, true);
        return true;
    }

    @Override
    public boolean renderSkinWithHelper(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX) {
        if (stack.isEmpty()) {
            return false;
        }
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (descriptor == null) {
            return false;
        }
        ModelTypeHelper modelTypeHelper = SkinModelRenderHelper.INSTANCE.getTypeHelperForModel(ModelType.MODEL_BIPED, descriptor.getIdentifier().getSkinType());
        modelTypeHelper.render(null, limb1, limb2, limb3, headX, headY, 0);
        return true;
    }

    @Override
    public boolean renderSkinWithHelper(ISkinDescriptor skinPointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean renderSkinWithHelper(ISkinDescriptor skinPointer, ModelBiped modelBiped) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean renderSkinWithHelper(ISkinDescriptor skinPointer, float limb1, float limb2, float limb3, float headY, float headX) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean renderSkin(ItemStack stack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean renderSkin(ISkinDescriptor skinPointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean renderSkinPart(ISkinDescriptor skinPointer, ISkinPartType skinPartType) {
        return false;
    }

    @Override
    public boolean isSkinInModelCache(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (descriptor != null) {
            return ClientSkinCache.INSTANCE.isSkinInCache(descriptor);
        }
        return false;
    }

    @Override
    public boolean isSkinInModelCache(ISkinDescriptor skinPointer) {
        if (skinPointer == null) {
            return false;
        }
        return false;
    }

    @Override
    public void requestSkinModelFromSever(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        requestSkinModelFromSever(SkinNBTHelper.getSkinDescriptorFromStack(stack));
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
        return ModelHand.MODEL;
    }

    @Override
    public ISkin getSkinFromModelCache(ISkinDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }
        return ClientSkinCache.INSTANCE.getSkin(descriptor);
    }

}
