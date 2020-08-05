package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public class SkinLayerRendererBibed extends SkinLayerRenderer<EntityLivingBase, RenderLivingBase> {

    public SkinLayerRendererBibed(RenderLivingBase renderer) {
        super(renderer);
    }
    
    @Override
    protected void renderSkinType(EntityLivingBase entity, ISkinType skinType, IEntitySkinCapability skinCapability, IWardrobeCap wardrobeCap) {
        float distance = entity.getDistance(Minecraft.getMinecraft().player);
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

            SkinModelRenderHelper modelRenderer = SkinModelRenderHelper.INSTANCE;
            GL11.glEnable(GL11.GL_NORMALIZE);
            modelRenderer.renderEquipmentPart(entity, (ModelBiped) renderer.getMainModel(), skin, dye, extraColours, distance, true);
            GL11.glDisable(GL11.GL_NORMALIZE);
        }
    }

    @Override
    protected void setRotTranForPartType(EntityLivingBase entitylivingbaseIn, ISkinType skinType, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        // TODO Auto-generated method stub
        
    }
}
