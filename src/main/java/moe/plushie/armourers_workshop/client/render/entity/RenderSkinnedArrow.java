package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderSkinnedArrow<T extends EntityArrow> extends RenderArrow<T> {

    private final SkinModelRenderHelper equipmentModelRenderer;

    public RenderSkinnedArrow(RenderManager renderManager) {
        super(renderManager);
        this.equipmentModelRenderer = SkinModelRenderHelper.INSTANCE;
    }

    @Override
    public void doRender(T entityArrow, double x, double y, double z, float yaw, float partialTickTime) {
        boolean didRender = false;
        if (entityArrow.shootingEntity != null) {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get(entityArrow.shootingEntity);
            if (skinCapability != null) {
                for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinBow); i++) {
                    ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinBow, i);
                    if (skinDescriptor != null) {
                        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
                        if (skin != null) {
                            SkinPart skinPart = skin.getPart("armourers:bow.arrow");
                            if (skinPart != null) {
                                ModRenderHelper.enableAlphaBlend();
                                renderArrowSkin(entityArrow, x, y, z, partialTickTime, skinPart, skinDescriptor.getSkinDye());
                                ModRenderHelper.disableAlphaBlend();
                                didRender = true;
                            }
                        }
                    }
                }
            }
        }
        if (!didRender) {
            super.doRender(entityArrow, x, y, z, yaw, partialTickTime);
        }
    }

    private void renderArrowSkin(EntityArrow entityArrow, double x, double y, double z, float partialTickTime, SkinPart skinPart, ISkinDye skinDye) {
        float scale = 0.0625F;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);

        GL11.glRotatef(entityArrow.prevRotationYaw + (entityArrow.rotationYaw - entityArrow.prevRotationYaw) * partialTickTime - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entityArrow.prevRotationPitch + (entityArrow.rotationPitch - entityArrow.prevRotationPitch) * partialTickTime, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(2.5F * scale, -0.5F * scale, -0.5F * scale);
        float f10 = 0.05625F;
        float f11 = entityArrow.arrowShake - partialTickTime;

        if (f11 > 0.0F) {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }
        GL11.glRotatef(-90, 0, 1, 0);
        GL11.glScalef(-1, -1, 1);
        SkinPartRenderer.INSTANCE.renderPart(new SkinPartRenderData(skinPart, scale, skinDye, null, 0, true, false, false, null));
        GL11.glPopMatrix();
    }
}
