package riskyken.armourersWorkshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.render.SkinPartRenderer;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

@SideOnly(Side.CLIENT)
public class RenderSkinnedArrow extends RenderArrow {
    
    private final SkinModelRenderer equipmentModelRenderer;
    
    public RenderSkinnedArrow() {
        this.equipmentModelRenderer = SkinModelRenderer.INSTANCE;
    }
    
    @Override
    public void doRender(EntityArrow entityArrow, double x, double y, double z, float yaw, float partialTickTime) {
        if (entityArrow.shootingEntity != null && entityArrow.shootingEntity instanceof EntityClientPlayerMP) {
            EntityClientPlayerMP player = (EntityClientPlayerMP) entityArrow.shootingEntity;
            IEntityEquipment entityEquipment = equipmentModelRenderer.getPlayerCustomEquipmentData(player);
            if (entityEquipment != null && entityEquipment.haveEquipment(SkinTypeRegistry.skinArrow, 0)) {
                ISkinPointer skinPointer = entityEquipment.getSkinPointer(SkinTypeRegistry.skinArrow, 0);
                if (ClientSkinCache.INSTANCE.isSkinInCache(skinPointer)) {
                    ModRenderHelper.enableAlphaBlend();
                    renderArrowSkin(entityArrow, x, y, z, partialTickTime, skinPointer);
                    ModRenderHelper.disableAlphaBlend();
                    return;
                } else {
                    ClientSkinCache.INSTANCE.requestSkinFromServer(skinPointer);
                }
            }
        }
        
        super.doRender(entityArrow, x, y, z, yaw, partialTickTime);
    }
    
    private void renderArrowSkin(EntityArrow entityArrow, double x, double y, double z, float partialTickTime, ISkinPointer skinPointer) {
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            return;
        }
        
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
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            //TODO apply dyes to arrows.
            SkinPartRenderer.INSTANCE.renderPart(skinPart, 0.0625F, null, null, true);
        }
        GL11.glPopMatrix();
    }
}
