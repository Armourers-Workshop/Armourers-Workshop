package riskyken.armourersWorkshop.client.model.equipmet;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;

@SideOnly(Side.CLIENT)
public class ModelCustomEquipmetBow extends AbstractModelCustomEquipment {
    
    public int frame = 0;
    
    @Override
    public void render(Entity entity, Skin armourData, float limb1, float limb2, float limb3, float headY, float headX) {
        setRotationAngles(limb1, limb2, limb3, headY, headX, SCALE, entity);
        render(entity, armourData, false, null, null);
    }
    
    @Override
    public void render(Entity entity, ModelBiped modelBiped, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour) {
        setRotationFromModelBiped(modelBiped);
        render(entity, armourData, showSkinPaint, skinDye, extraColour);
    }
    
    @Override
    public void render(Entity entity, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour) {
        if (armourData == null) { return; }
        
        ArrayList<SkinPart> parts = armourData.getParts();
        
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            this.isChild = player.isChild();
            this.heldItemRight = 0;
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }
        }
        
        ApiRegistrar.INSTANCE.onRenderEquipment(entity, SkinTypeRegistry.skinBow);
        armourData.onUsed();
        
        if (frame > parts.size() - 1) {
            frame = parts.size() - 1;
        }
        
        if (frame < 0 | frame > parts.size() - 1) {
            ModLogger.log("wow");
            return;
        }
        
        SkinPart part = parts.get(frame);
            
        GL11.glPushMatrix();
        if (isChild) {
            float f6 = 2.0F;
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
        }

        ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, part.getPartType());
        renderRightArm(part, SCALE, skinDye, extraColour);
        
        GL11.glPopMatrix();
        
        
        GL11.glColor3f(1F, 1F, 1F);
        frame = 0;
    }
    
    private void renderRightArm(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour) {
        GL11.glPushMatrix();
        
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        
        //GL11.glTranslatef(-5.0F * scale, 0F, 0F);
        //GL11.glTranslatef(0F, 2.0F * scale, 0F);
        
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);
        
        renderPart(part, scale, skinDye, extraColour);
        GL11.glPopMatrix();
    }
}
