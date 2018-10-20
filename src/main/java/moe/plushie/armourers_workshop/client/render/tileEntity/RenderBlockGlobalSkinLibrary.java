package moe.plushie.armourers_workshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.model.block.ModelBlockGlobalSkinLibrary;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityGlobalSkinLibrary;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderBlockGlobalSkinLibrary extends TileEntitySpecialRenderer<TileEntityGlobalSkinLibrary> {

    private static final ModelBlockGlobalSkinLibrary GLOBE_MODEL = new ModelBlockGlobalSkinLibrary();
    private static final float SCALE = 0.0625F;
    
    @Override
    public void render(TileEntityGlobalSkinLibrary te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        GL11.glScalef(-1, -1, 1);
        float shrink = 0.4F;
        if (ConfigHandlerClient.useClassicBlockModels) {
            shrink = 1;
        } else {
            GlStateManager.translate(3.5F * SCALE, -2 * SCALE, 5F * SCALE);
        }
        
        GlStateManager.scale(shrink, shrink, shrink);
        
        GLOBE_MODEL.render(te, partialTicks, SCALE);
        GL11.glPopMatrix();
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}
