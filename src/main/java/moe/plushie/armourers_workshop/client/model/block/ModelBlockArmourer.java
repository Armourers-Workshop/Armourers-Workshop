package moe.plushie.armourers_workshop.client.model.block;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * ModelBlockArmourer - RiskyKen
 * Created using Tabula 4.0.2
 */
public class ModelBlockArmourer extends ModelBase {
    
    private static final ResourceLocation modelImage = new ResourceLocation(LibModInfo.ID, "textures/models/mini-armourer.png");
    
    public ModelRenderer FrameTop2;
    public ModelRenderer FrameBottom;
    public ModelRenderer PillerFR;
    public ModelRenderer PillerBR;
    public ModelRenderer PillerBL;
    public ModelRenderer PillerFL;
    public ModelRenderer FrameTop1;
    public ModelRenderer shape8;

    public ModelBlockArmourer() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.shape8 = new ModelRenderer(this, 0, 51);
        this.shape8.setRotationPoint(0.0F, 1.5F, 0.0F);
        this.shape8.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5);
        this.PillerBL = new ModelRenderer(this, 0, 36);
        this.PillerBL.setRotationPoint(6.0F, -3.0F, 6.0F);
        this.PillerBL.addBox(-1.0F, 0.0F, -1.0F, 2, 9, 2);
        this.FrameTop2 = new ModelRenderer(this, 8, 36);
        this.FrameTop2.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.FrameTop2.addBox(-7.0F, 0.0F, -7.0F, 14, 1, 14);
        this.FrameTop1 = new ModelRenderer(this, 0, 18);
        this.FrameTop1.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.FrameTop1.addBox(-8.0F, 0.0F, -8.0F, 16, 2, 16);
        this.PillerFL = new ModelRenderer(this, 0, 36);
        this.PillerFL.setRotationPoint(6.0F, -3.0F, -6.0F);
        this.PillerFL.addBox(-1.0F, 0.0F, -1.0F, 2, 9, 2);
        this.PillerBR = new ModelRenderer(this, 0, 36);
        this.PillerBR.setRotationPoint(-6.0F, -3.0F, 6.0F);
        this.PillerBR.addBox(-1.0F, 0.0F, -1.0F, 2, 9, 2);
        this.FrameBottom = new ModelRenderer(this, 0, 0);
        this.FrameBottom.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.FrameBottom.addBox(-8.0F, -2.0F, -8.0F, 16, 2, 16);
        this.PillerFR = new ModelRenderer(this, 0, 36);
        this.PillerFR.setRotationPoint(-6.0F, -3.0F, -6.0F);
        this.PillerFR.addBox(-1.0F, 0.0F, -1.0F, 2, 9, 2);
    }

    public void render(TileEntity tileEntity, float tickTime, float scale) {
        Minecraft.getMinecraft().renderEngine.bindTexture(modelImage);
        if (tileEntity != null) {
            float angle = (((tileEntity.getWorld().getTotalWorldTime() + tileEntity.hashCode()) % 360) + tickTime);
            setRotateAngle(this.shape8, (float)Math.toRadians(angle * 4), (float)Math.toRadians(angle), (float)Math.toRadians(angle * 2));

            /*
            Color c = new Color(tileEntity.red, tileEntity.green, tileEntity.blue);
            if (tileEntity.getWorld().getTotalWorldTime() % 2 == 1) {
                c = UtilColour.addColourNoise(c, 3);
            }
            
            tileEntity.red = c.getRed();
            tileEntity.green = c.getGreen();
            tileEntity.blue = c.getBlue();
            float r = tileEntity.red / (float)255;
            float g = tileEntity.green / (float)255;
            float b = tileEntity.blue / (float)255;
            GL11.glColor3f(r, g, b);
            */
        } else {
            setRotateAngle(this.shape8, 0F, 0F, 0F);
        }
        this.shape8.render(scale);
        GL11.glColor3f(1F, 1F, 1F);
        this.PillerBL.render(scale);
        this.FrameTop2.render(scale);
        this.FrameTop1.render(scale);
        this.PillerFL.render(scale);
        this.PillerBR.render(scale);
        this.FrameBottom.render(scale);
        this.PillerFR.render(scale);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
