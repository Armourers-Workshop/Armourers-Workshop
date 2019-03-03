package moe.plushie.armourers_workshop.client.model.skin;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSkinBow extends ModelTypeHelper {
    
    public int frame = 0;
    
    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, ExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
        render(entity, skin, new SkinRenderData(SCALE, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null));
    }
    
    @Override
    public void render(Entity entity, Skin skin, SkinRenderData renderData) {
        if (skin == null) {
            return;
        }
        
        ArrayList<SkinPart> parts = skin.getParts();
        
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            this.isChild = player.isChild();
            /*
            this.heldItemRight = 0;
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }
            */
        }
        
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

        renderRightArm(new SkinPartRenderData(part, renderData));
        
        GL11.glPopMatrix();
        
        
        GL11.glColor3f(1F, 1F, 1F);
        frame = 0;
    }
    
    private void renderRightArm(SkinPartRenderData partRenderData) {
        GL11.glPushMatrix();
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);
        
        renderPart(partRenderData);
        GL11.glPopMatrix();
    }
}
