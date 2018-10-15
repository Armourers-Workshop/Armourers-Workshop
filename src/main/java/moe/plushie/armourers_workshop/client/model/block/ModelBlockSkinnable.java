package moe.plushie.armourers_workshop.client.model.block;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ModelBlockSkinnable extends ModelBase {
    
    public static final ResourceLocation MODEL_IMAGE = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/models/model-block-skinnable.png");
    
    public ModelRenderer spinningCube;
    
    public ModelBlockSkinnable() {
        this.textureWidth = 16;
        this.textureHeight = 16;
        this.spinningCube = new ModelRenderer(this, 0, 0).setTextureSize(textureWidth, textureHeight);
        this.spinningCube.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spinningCube.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5);
    }
    
    public void render(TileEntitySkinnable tileEntity, float partialTickTime, float scale) {
        Minecraft.getMinecraft().renderEngine.bindTexture(MODEL_IMAGE);
        GL11.glColor3f(1F, 1F, 1F);
        if (tileEntity != null) {
            float angle = (((tileEntity.getWorld().getTotalWorldTime() + tileEntity.hashCode()) % 360) + partialTickTime);
            setRotateAngle(this.spinningCube, (float)Math.toRadians(angle * 4), (float)Math.toRadians(angle), (float)Math.toRadians(angle * 2));
        } else {
            setRotateAngle(this.spinningCube, 0F, 0F, 0F);
        }
        this.spinningCube.render(scale);
    }
    
    public void render(float scale, boolean bindTexture) {
        if (bindTexture) {
            Minecraft.getMinecraft().renderEngine.bindTexture(MODEL_IMAGE);
        }
        GlStateManager.pushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glScaled(2F, 2F, 2F);
        
        double angle = (float) ((double)(System.currentTimeMillis() / 30) % 360D);
        setRotateAngle(this.spinningCube, (float)Math.toRadians(angle * 4), (float)Math.toRadians(angle), (float)Math.toRadians(angle * 2));
        this.spinningCube.render(scale);
        GlStateManager.popMatrix();
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
