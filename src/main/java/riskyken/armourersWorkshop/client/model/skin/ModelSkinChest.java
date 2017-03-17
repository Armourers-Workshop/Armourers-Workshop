package riskyken.armourersWorkshop.client.model.skin;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.skin.SkinModelTexture;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinPaintCache;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.proxies.ClientProxy;

@SideOnly(Side.CLIENT)
public class ModelSkinChest extends AbstractModelSkin {
    
    @Override
    public void render(Entity entity, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        if (armourData == null) { return; }
        ArrayList<SkinPart> parts = armourData.getParts();
        
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            this.heldItemRight = 0;
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }
        }
        
        if (ClientProxy.isJrbaClientLoaded()) {
            this.isChild = false;
        }
        
        ApiRegistrar.INSTANCE.onRenderEquipment(entity, SkinTypeRegistry.skinChest);
        RenderHelper.enableGUIStandardItemLighting();
        
        if (armourData.hasPaintData() & showSkinPaint) {
            if (extraColour == null) {
                extraColour = PaintingHelper.getLocalPlayerExtraColours();
            }
            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(armourData, skinDye, extraColour);
            st.bindTexture();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            bipedBody.render(SCALE);
            bipedLeftArm.render(SCALE);
            bipedRightArm.render(SCALE);
            GL11.glPopAttrib();
        }
        
        for (int i = 0; i < parts.size(); i++) {
            SkinPart part = parts.get(i);
            
            GL11.glPushMatrix();
            if (isChild) {
                float f6 = 2.0F;
                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
            }

            ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, part.getPartType());
            
            if (part.getPartType().getPartName().equals("base")) {
                renderChest(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getPartName().equals("leftArm")) {
                renderLeftArm(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getPartName().equals("rightArm")) {
                renderRightArm(part, SCALE, skinDye, extraColour, itemRender, distance, doLodLoading);
            }
            
            GL11.glPopMatrix();
            
        }
        
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderChest(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glRotated(28F, 1F, 0, 0);
        }
        GL11.glColor3f(1F, 1F, 1F);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }
    
    private void renderLeftArm(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        GL11.glTranslatef(5.0F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 2.0F * scale, 0F);
        
        
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleX), 1, 0, 0);
        
        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        
        GL11.glPopMatrix();
    }
    
    private void renderRightArm(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        GL11.glTranslatef(-5.0F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 2.0F * scale, 0F);
        
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);
        
        renderPart(part, scale, skinDye, extraColour, distance, doLodLoading);
        GL11.glPopMatrix();
    }
}
