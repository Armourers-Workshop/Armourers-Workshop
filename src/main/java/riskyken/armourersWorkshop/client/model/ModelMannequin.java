package riskyken.armourersWorkshop.client.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.data.BipedRotations;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMannequin extends ModelBiped {

    private boolean compiled;
    
    public ModelMannequin() {
        super();
        this.isChild = false;
        this.compiled = false;
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
    
    public void compile(float scale) {
        if (!compiled) {
            compilePart(bipedHead, scale);
            compilePart(bipedHeadwear, scale);
            compilePart(bipedBody, scale);
            compilePart(bipedRightArm, scale);
            compilePart(bipedLeftArm, scale);
            compilePart(bipedRightLeg, scale);
            compilePart(bipedLeftLeg, scale);
            compiled = true;
        }
        
    }
    
    private void compilePart(ModelRenderer modelRenderer, float scale) {
        Method m = ReflectionHelper.findMethod(ModelRenderer.class, modelRenderer, new String[] {"func_78788_d", "compileDisplayList"}, float.class);
        m.setAccessible(true);
        try {
            m.invoke(modelRenderer, scale);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
