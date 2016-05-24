package riskyken.armourersWorkshop.client.model.skin;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.skin.ClientSkinPaintCache;
import riskyken.armourersWorkshop.client.skin.SkinModelTexture;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

@SideOnly(Side.CLIENT)
public class ModelSkinFeet extends AbstractModelSkin {
    
    @Override
    public void render(Entity entity, Skin armourData, float limb1, float limb2, float limb3, float headY, float headX) {
        setRotationAngles(limb1, limb2, limb3, headY, headX, SCALE, entity);
        render(entity, armourData, false, null, null, false);
    }
    
    @Override
    public void render(Entity entity, ModelBiped modelBiped, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender) {
        setRotationFromModelBiped(modelBiped);
        render(entity, armourData, showSkinPaint, skinDye, extraColour, itemRender);
    }
    
    @Override
    public void render(Entity entity, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender) {
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

        ApiRegistrar.INSTANCE.onRenderEquipment(entity, SkinTypeRegistry.skinFeet);
        armourData.onUsed();
        RenderHelper.enableGUIStandardItemLighting();
        
        if (armourData.hasPaintData() & showSkinPaint) {
            if (extraColour == null) {
                extraColour = PaintingHelper.getLocalPlayerExtraColours();
            }
            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(armourData, skinDye, extraColour);
            st.bindTexture();
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            if (itemRender) {
                GL11.glTranslated(0, -12 * SCALE, 0);
            }
            bipedLeftLeg.render(SCALE);
            bipedRightLeg.render(SCALE);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
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
            
            if (part.getPartType().getPartName().equals("leftFoot")) {
                renderLeftFoot(part, SCALE, skinDye, extraColour, itemRender);
            } else if (part.getPartType().getPartName().equals("rightFoot")) {
                renderRightFoot(part, SCALE, skinDye, extraColour, itemRender);
            }
            
            GL11.glPopMatrix();
        }
        
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderLeftFoot(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        GL11.glColor3f(1F, 1F, 1F);
        if (!itemRender) {
            GL11.glTranslated(0, 12 * scale, 0);
        }
        GL11.glTranslated(2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);
        
        
        renderPart(part, scale, skinDye, extraColour);
        GL11.glPopMatrix();
    }
    
    private void renderRightFoot(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, boolean itemRender) {
        GL11.glPushMatrix();
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        GL11.glColor3f(1F, 1F, 1F);
        if (!itemRender) {
            GL11.glTranslated(0, 12 * scale, 0);
        }
        GL11.glTranslated(-2 * scale, 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);
        
        renderPart(part, scale, skinDye, extraColour);
        GL11.glPopMatrix();
    }
}
