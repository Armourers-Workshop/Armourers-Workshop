package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class SkinLayerRenderer<E extends EntityLivingBase> implements LayerRenderer<E> {
    
    protected static final float SCALE = 0.0625F;
    
    @Override
    public void doRenderLayer(E entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
        if (skinCapability == null) {
            return;
        }
        
        ISkinType[] skinTypes = skinCapability.getValidSkinTypes();
        for (int i = 0; i < skinTypes.length; i++) {
            GlStateManager.pushMatrix();
            setRotTranForPartType(entitylivingbaseIn, skinTypes[i], limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            renderSkinType(entitylivingbaseIn, SkinTypeRegistry.skinHead, skinCapability, WardrobeCap.get(entitylivingbaseIn));
            GlStateManager.popMatrix();
        }
    }
    
    protected abstract void setRotTranForPartType(E entitylivingbaseIn, ISkinType skinType, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale);
    
    protected void renderSkinType(EntityLivingBase entity, ISkinType skinType, IEntitySkinCapability skinCapability, IWardrobeCap wardrobeCap) {
        for (int i = 0; i < skinCapability.getSlotCountForSkinType(skinType); i++) {
            ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(skinType, i);
            if (skinDescriptor == null) {
                continue;
            }
            
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
            if (skin == null) {
                continue;
            }
            
            ExtraColours extraColours = ExtraColours.EMPTY_COLOUR;
            ISkinDye dye = skinDescriptor.getSkinDye();
            if (wardrobeCap != null) {
                extraColours = wardrobeCap.getExtraColours();
                dye = new SkinDye(wardrobeCap.getDye());
                for (int dyeIndex = 0; dyeIndex < 8; dyeIndex++) {
                    if (skinDescriptor.getSkinDye().haveDyeInSlot(dyeIndex)) {
                        dye.addDye(dyeIndex, skinDescriptor.getSkinDye().getDyeColour(dyeIndex));
                    }
                }
            }

            GL11.glEnable(GL11.GL_NORMALIZE);
            for (int partIndex = 0; partIndex < skin.getParts().size(); partIndex++) {
                SkinPartRenderer.INSTANCE.renderPart(skin.getParts().get(partIndex), SCALE, dye, extraColours, false);
            }
            GL11.glDisable(GL11.GL_NORMALIZE);
        }
    }
    
    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
