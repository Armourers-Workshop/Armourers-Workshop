package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.IEntityEquipment;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinPointer;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSkinnedArrow extends RenderArrow {
    
    private final SkinModelRenderer equipmentModelRenderer;
    
    public RenderSkinnedArrow(RenderManager renderManager) {
        super(renderManager);
        this.equipmentModelRenderer = SkinModelRenderer.INSTANCE;
    }
    
    @Override
    public void doRender(EntityArrow entityArrow, double x, double y, double z, float yaw, float partialTickTime) {
        if (entityArrow.shootingEntity != null && entityArrow.shootingEntity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entityArrow.shootingEntity;
            IEntityEquipment entityEquipment = equipmentModelRenderer.getPlayerCustomEquipmentData(player);
            if (entityEquipment != null && entityEquipment.haveEquipment(SkinTypeRegistry.skinBow, 0)) {
                ISkinPointer skinPointer = entityEquipment.getSkinPointer(SkinTypeRegistry.skinBow, 0);
                if (ClientSkinCache.INSTANCE.isSkinInCache(skinPointer)) {
                    Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
                    if (skin != null) {
                        SkinPart skinPart = skin.getPart("armourers:bow.arrow");
                        if (skinPart != null) {
                            ModRenderHelper.enableAlphaBlend();
                            renderArrowSkin(entityArrow, x, y, z, partialTickTime, skinPart, skinPointer.getSkinDye());
                            ModRenderHelper.disableAlphaBlend();
                            return;
                        }
                    }
                } else {
                    ClientSkinCache.INSTANCE.requestSkinFromServer(skinPointer);
                }
            }
        }
        
        super.doRender(entityArrow, x, y, z, yaw, partialTickTime);
    }
    
    private void renderArrowSkin(EntityArrow entityArrow, double x, double y, double z, float partialTickTime, SkinPart skinPart, ISkinDye skinDye) {
        float scale = 0.0625F;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        
        GL11.glRotatef(entityArrow.prevRotationYaw + (entityArrow.rotationYaw - entityArrow.prevRotationYaw) * partialTickTime - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entityArrow.prevRotationPitch + (entityArrow.rotationPitch - entityArrow.prevRotationPitch) * partialTickTime, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(2.5F * scale, 0.5F * scale, 0.5F * scale);
        float f10 = 0.05625F;
        float f11 = (float)entityArrow.arrowShake - partialTickTime;

        if (f11 > 0.0F) {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }
        GL11.glRotatef(-90, 0, 1, 0);
        GL11.glScalef(-1, -1, 1);
        SkinPartRenderer.INSTANCE.renderPart(skinPart, 0.0625F, skinDye, null, true);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }
}
