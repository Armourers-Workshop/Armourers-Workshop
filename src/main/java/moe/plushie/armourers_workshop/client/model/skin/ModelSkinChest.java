package moe.plushie.armourers_workshop.client.model.skin;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinPaintCache;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSkinChest extends ModelTypeHelper {
    
    @Override
    public void render(Entity entity, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        if (armourData == null) { return; }
        ArrayList<SkinPart> parts = armourData.getParts();
        
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            /*this.heldItemRight = 0;
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }*/
        }
        
        RenderHelper.enableGUIStandardItemLighting();
        
        if (armourData.hasPaintData() & showSkinPaint) {
            if (extraColours == null) {
                extraColours = ExtraColours.EMPTY_COLOUR;
            }
            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(armourData, skinDye, extraColours);
            st.bindTexture();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            bipedBody.render(SCALE);
            bipedLeftArm.render(SCALE);
            bipedRightArm.render(SCALE);
            GL11.glPopAttrib();
        }
        
        boolean override = SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(armourData.getProperties());
        
        for (int i = 0; i < parts.size(); i++) {
            SkinPart part = parts.get(i);
            
            GL11.glPushMatrix();
            if (isChild) {
                float f6 = 2.0F;
                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
            }
            
            if (isSneak) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            
            if (part.getPartType().getPartName().equals("base")) {
                renderChest(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading);
            } else if (part.getPartType().getPartName().equals("leftArm")) {
                renderLeftArm(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading, override);
            } else if (part.getPartType().getPartName().equals("rightArm")) {
                renderRightArm(part, SCALE, skinDye, extraColours, itemRender, distance, doLodLoading, override);
            }
            
            GL11.glPopMatrix();
            
        }
        
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderChest(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glRotated(Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
        }
        GL11.glColor3f(1F, 1F, 1F);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }
    
    private void renderLeftArm(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, boolean override) {
        GL11.glPushMatrix();
        
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        GL11.glTranslatef(5.0F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 2.0F * scale, 0F);
        
        
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleX), 1, 0, 0);
        
        if (slim & !override) {
            GL11.glTranslatef(-0.25F * scale, 0F, 0F);
            GL11.glTranslatef(0F, 0.5F * scale, 0F);
            
            GL11.glScalef(0.75F, 1F, 1F);
        }
        
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        
        GL11.glPopMatrix();
    }
    
    private void renderRightArm(SkinPart part, float scale, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, boolean override) {
        GL11.glPushMatrix();
        
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        GL11.glTranslatef(-5.0F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 2.0F * scale, 0F);
        
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);
        
        if (slim & !override) {
            GL11.glTranslatef(0.25F * scale, 0F, 0F);
            GL11.glTranslatef(0F, 0.5F * scale, 0F);
            
            GL11.glScalef(0.75F, 1F, 1F);
        }
        
        renderPart(part, scale, skinDye, extraColours, distance, doLodLoading);
        GL11.glPopMatrix();
    }
}
