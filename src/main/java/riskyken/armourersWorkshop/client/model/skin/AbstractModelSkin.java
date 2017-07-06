package riskyken.armourersWorkshop.client.model.skin;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.render.SkinPartRenderer;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;

@SideOnly(Side.CLIENT)
public abstract class AbstractModelSkin extends ModelBiped implements IEquipmentModel {

    public Skin npcSkinData = null;
    public ISkinDye npcDyeData = null;
    protected boolean slim;
    
    protected static float SCALE = 0.0625F;
    
    protected void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    protected void setRotation(ModelRenderer targetModel, ModelRenderer sourceModel) {
        targetModel.rotateAngleX = sourceModel.rotateAngleX;
        targetModel.rotateAngleY = sourceModel.rotateAngleY;
        targetModel.rotateAngleZ = sourceModel.rotateAngleZ;
    }
    
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
        this.isRiding = false;
        this.isSneak = false;
        this.aimedBow = false;
        this.isChild = false;
        this.slim = false;
        this.heldItemRight = 0;
        bipedLeftLeg.rotateAngleZ = 0F;
        bipedRightLeg.rotateAngleZ = 0F;
        bipedHead.rotateAngleZ = 0F;
        bipedHeadwear.rotateAngleZ = 0F;
    }
    
    @Override
    public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
        if (npcSkinData != null) {
            this.isRiding = false;
            this.isSneak = false;
            this.aimedBow = false;
            this.isChild = false;
            this.slim = false;
            this.heldItemRight = 0;
            if (entity instanceof EntityLivingBase) {
                if (((EntityLivingBase)entity).getHeldItem() != null) {
                    this.heldItemRight = 1;
                }
                if (((EntityLivingBase)entity).isRiding()) {
                    this.isRiding = true;
                }
                if (((EntityLivingBase)entity).isSneaking()) {
                    this.isSneak = true;
                }
                if (((EntityLivingBase)entity).isChild()) {
                    this.isChild = true;
                }
            }
            bipedLeftLeg.rotateAngleZ = 0F;
            bipedRightLeg.rotateAngleZ = 0F;
            bipedHead.rotateAngleZ = 0F;
            bipedHeadwear.rotateAngleZ = 0F;
            
            super.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, entity);
            
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            ModRenderHelper.enableAlphaBlend();
            render(entity, npcSkinData, false, npcDyeData, null, false, 0, true);
            ModRenderHelper.disableAlphaBlend();
            GL11.glPopAttrib();
            
            npcSkinData = null;
            npcDyeData = null;
        }
    }
    
    @Override
    public void render(Entity entity, ModelBiped modelBiped, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading) {
        setRotationFromModelBiped(modelBiped);
        render(entity, armourData, showSkinPaint, skinDye, extraColour, itemRender, distance, doLodLoading);
    }
    
    @Override
    public void render(Entity entity, Skin armourData, float limb1, float limb2, float limb3, float headY, float headX) {
        setRotationAngles(limb1, limb2, limb3, headY, headX, SCALE, entity);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        ModRenderHelper.enableAlphaBlend();
        render(entity, armourData, false, null, null, false, 0, true);
        ModRenderHelper.disableAlphaBlend();
        GL11.glPopAttrib();
    }
    
    public abstract void render(Entity entity, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading);
    
    protected void setRotationFromModelBiped(ModelBiped modelBiped) {
        this.isRiding = false;
        this.isSneak = false;
        this.aimedBow = false;
        this.heldItemRight = 0;
        this.slim = false;
        if (modelBiped == null) {
            setRotation(bipedHead, 0F, 0F, 0F);
            setRotation(bipedBody, 0F, 0F, 0F);
            setRotation(bipedLeftArm, 0F, 0F, 0F);
            setRotation(bipedRightArm, 0F, 0F, 0F);
            setRotation(bipedLeftLeg, 0F, 0F, 0F);
            setRotation(bipedRightLeg, 0F, 0F, 0F);
            isChild = false;
        } else {
            setRotation(bipedHead, modelBiped.bipedHead);
            setRotation(bipedBody, modelBiped.bipedBody);
            setRotation(bipedLeftArm, modelBiped.bipedLeftArm);
            setRotation(bipedRightArm, modelBiped.bipedRightArm);
            setRotation(bipedLeftLeg, modelBiped.bipedLeftLeg);
            setRotation(bipedRightLeg, modelBiped.bipedRightLeg);
            isChild = modelBiped.isChild;
            if (modelBiped instanceof ModelMannequin) {
                this.slim = ((ModelMannequin)modelBiped).isSlim();
            } else {
                this.slim = false;
            }
        }
    }
    
    protected void renderPart(SkinPart armourPart, float scale, ISkinDye skinDye, byte[] extraColour, double distance, boolean doLodLoading) {
        SkinPartRenderer.INSTANCE.renderPart(armourPart, scale, skinDye, extraColour, distance, doLodLoading);
    }
}
