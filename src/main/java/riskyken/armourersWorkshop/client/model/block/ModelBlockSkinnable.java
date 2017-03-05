package riskyken.armourersWorkshop.client.model.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;

public class ModelBlockSkinnable extends ModelBase {
    
    private static final ResourceLocation modelImage = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/models/ModelBlockSkinnable.png");
    
    public ModelRenderer spinningCube;
    
    public ModelBlockSkinnable() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.spinningCube = new ModelRenderer(this, 0, 0);
        this.spinningCube.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spinningCube.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5);
    }
    
    public void render(TileEntitySkinnable tileEntity, float partialTickTime, float scale) {
        Minecraft.getMinecraft().renderEngine.bindTexture(modelImage);
        GL11.glColor3f(1F, 1F, 1F);
        if (tileEntity != null) {
            float angle = (((tileEntity.getWorldObj().getTotalWorldTime() + tileEntity.hashCode()) % 360) + partialTickTime);
            setRotateAngle(this.spinningCube, (float)Math.toRadians(angle * 4), (float)Math.toRadians(angle), (float)Math.toRadians(angle * 2));
        } else {
            setRotateAngle(this.spinningCube, 0F, 0F, 0F);
        }
        this.spinningCube.render(scale);
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
