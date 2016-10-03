package riskyken.armourersWorkshop.client.model.block;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;

public class ModelBlockGlobalSkinLibrary extends ModelBase {
    
    private static final ResourceLocation MODEL_TEXTURE = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/tileEntities/globalSkinLibrary.png");
    
    public ModelRenderer globe;
    
    public ModelBlockGlobalSkinLibrary() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.globe = new ModelRenderer(this, 0, 0);
        this.globe.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.globe.addBox(-8F, -8F, -8F, 16, 16, 16);
    }
    
    public void render(TileEntityGlobalSkinLibrary tileEntity, float partialTickTime, float scale) {
        Minecraft.getMinecraft().renderEngine.bindTexture(MODEL_TEXTURE);
        ModRenderHelper.disableLighting();
        ModRenderHelper.enableAlphaBlend();
        GL11.glColor4f(1F, 1F, 1F, 0.5F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        if (tileEntity != null) {
            float angle = (((tileEntity.getWorld().getTotalWorldTime() + tileEntity.hashCode()) % 360) + partialTickTime);
            setRotateAngle(this.globe, (float)Math.toRadians(angle * 4), (float)Math.toRadians(angle), (float)Math.toRadians(angle * 2));
        } else {
            setRotateAngle(this.globe, 0F, 0F, 0F);
        }
        //setRotateAngle(this.globe, 0F, 0F, 0F);
        GL11.glScalef(0.6F, 0.6F, 0.6F);
        this.globe.render(scale);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        ModRenderHelper.disableAlphaBlend();
        ModRenderHelper.enableLighting();
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
