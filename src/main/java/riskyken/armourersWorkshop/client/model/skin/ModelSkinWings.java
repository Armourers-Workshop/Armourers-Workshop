package riskyken.armourersWorkshop.client.model.skin;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.SkinUtils;

public class ModelSkinWings extends AbstractModelSkin  {

    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance) {
        if (skin == null) {
            return;
        }
        
        ArrayList<SkinPart> parts = skin.getParts();
        
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            this.heldItemRight = 0;
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }
        }
        
        ApiRegistrar.INSTANCE.onRenderEquipment(entity, SkinTypeRegistry.skinSword);
        skin.onUsed();
        for (int i = 0; i < parts.size(); i++) {
            SkinPart part = parts.get(i);
            
            GL11.glPushMatrix();
            GL11.glTranslated(0, 0, SCALE * 2);
            if (isChild) {
                float f6 = 2.0F;
                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
            }
            ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, part.getPartType());
            
            double angle = 45D;
            
            angle = SkinUtils.getFlapAngleForWings(entity, skin);
            
            if (part.getPartType().getPartName().equals("leftWing")) {
                renderLeftWing(part, SCALE, skinDye, extraColour, distance, angle);
            }
            if (part.getPartType().getPartName().equals("rightWing")) {
                renderRightWing(part, SCALE, skinDye, extraColour, distance, angle);
            }
            GL11.glPopMatrix();
        }
        
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderLeftWing(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, double distance, double angle) {
        GL11.glPushMatrix();
        
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        

        GL11.glTranslated(0, 0, SCALE * 0.5F);
        GL11.glRotated(angle, 0, 1, 0);
        GL11.glTranslated(0, 0, SCALE * -0.5F);
        //ModLogger.log(Math.sin(System.currentTimeMillis() / 25 % Math.PI * 2));
        
        renderPart(part, scale, skinDye, extraColour, distance);
        GL11.glPopMatrix();
    }
    
    private void renderRightWing(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, double distance, double angle) {
        GL11.glPushMatrix();
        
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        
        GL11.glTranslated(0, 0, SCALE * 0.5F);
        GL11.glRotated(-angle, 0, 1, 0);
        GL11.glTranslated(0, 0, SCALE * -0.5F);
        renderPart(part, scale, skinDye, extraColour, distance);
        GL11.glPopMatrix();
    }
}
