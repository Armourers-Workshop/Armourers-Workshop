package moe.plushie.armourers_workshop.client.model;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMannequin extends ModelBiped {

    private boolean slim = false;
    
    public ModelMannequin(boolean slim) {
        super(0F, 0F, 64, 64);
        this.slim = slim;
        this.isChild = false;
        if (slim) {
            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-2.0F, -1.5F, -2F, 3, 12, 4, 0F);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
            this.bipedLeftArm = new ModelRenderer(this, 40, 16);
            this.bipedLeftArm.mirror = true;
            this.bipedLeftArm.addBox(-1.0F, -1.5F, -2F, 3, 12, 4, 0F);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        }
    }
    
    public boolean isSlim() {
        return slim;
    }
    
    private void resetRotationsOnPart(ModelRenderer mr) {
        mr.rotateAngleX = 0;
        mr.rotateAngleY = 0;
        mr.rotateAngleZ = 0;
    }
    
    private void resetRotations() {
        resetRotationsOnPart(this.bipedHead);
        resetRotationsOnPart(this.bipedHeadwear);
        resetRotationsOnPart(this.bipedBody);
        resetRotationsOnPart(this.bipedLeftArm);
        resetRotationsOnPart(this.bipedRightArm);
        resetRotationsOnPart(this.bipedLeftLeg);
        resetRotationsOnPart(this.bipedRightLeg);
    }
    
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_,
            float p_78088_4_, float p_78088_5_, float p_78088_6_,
            float scale, boolean headOverlay) {
        resetRotations();
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, p_78088_1_);
        GL11.glColor3f(1F, 1F, 1F);
        this.bipedHead.render(scale);
        this.bipedBody.render(scale);
        this.bipedRightArm.render(scale);
        this.bipedLeftArm.render(scale);
        this.bipedRightLeg.render(scale);
        this.bipedLeftLeg.render(scale);
        if (headOverlay) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            this.bipedHeadwear.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    public void render(BipedRotations bipedRotations,  boolean headOverlay, float scale) {
        GL11.glColor3f(1F, 1F, 1F);
        bipedRotations.applyRotationsToBiped(this);
        if (isChild) {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
            GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            this.bipedHead.render(scale);
            if (headOverlay) {
                GL11.glDisable(GL11.GL_CULL_FACE);
                this.bipedHeadwear.render(scale);
                GL11.glEnable(GL11.GL_CULL_FACE);
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            GL11.glPopMatrix();
        } else {
            this.bipedHead.render(scale);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            if (headOverlay) {
                GL11.glDisable(GL11.GL_CULL_FACE);
                this.bipedHeadwear.render(scale);
                GL11.glEnable(GL11.GL_CULL_FACE);
            }
        }
    }
}
