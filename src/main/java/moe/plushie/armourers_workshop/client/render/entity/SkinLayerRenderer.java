package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class SkinLayerRenderer<E extends EntityLivingBase, R extends RenderLivingBase> implements LayerRenderer<E> {
    
    protected static final float SCALE = 0.0625F;
    protected final R renderer;
    
    public SkinLayerRenderer(R renderer) {
        this.renderer = renderer;
    }
    
    @Override
    public void doRenderLayer(E entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
        if (skinCapability == null) {
            return;
        }
        double distance = Minecraft.getMinecraft().player.getDistance(
                entitylivingbaseIn.posX,
                entitylivingbaseIn.posY,
                entitylivingbaseIn.posZ);
        if (distance > ConfigHandlerClient.renderDistanceSkin) {
            return;
        }
        ISkinType[] skinTypes = skinCapability.getValidSkinTypes();
        for (int i = 0; i < skinTypes.length; i++) {
            GlStateManager.pushMatrix();
            setRotTranForPartType(entitylivingbaseIn, skinTypes[i], limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            renderSkinType(entitylivingbaseIn, skinTypes[i], skinCapability, WardrobeCap.get(entitylivingbaseIn));
            GlStateManager.popMatrix();
        }
    }
    
    protected abstract void setRotTranForPartType(E entitylivingbaseIn, ISkinType skinType, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale);
    
    protected void renderSkinType(EntityLivingBase entity, ISkinType skinType, IEntitySkinCapability skinCapability, IWardrobeCap wardrobeCap) {
        double distance = entity.getDistance(Minecraft.getMinecraft().player);
        for (int i = 0; i < skinCapability.getSlotCountForSkinType(skinType); i++) {
            ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(skinType, i);
            if (skinDescriptor == null) {
                continue;
            }
            
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
            if (skin == null) {
                continue;
            }
            
            IExtraColours extraColours = ExtraColours.EMPTY_COLOUR;
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
                SkinPartRenderer.INSTANCE.renderPart(new SkinPartRenderData(skin.getParts().get(partIndex), SCALE, dye, extraColours, distance, false, false, false, null));
            }
            GL11.glDisable(GL11.GL_NORMALIZE);
        }
    }
    
    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
