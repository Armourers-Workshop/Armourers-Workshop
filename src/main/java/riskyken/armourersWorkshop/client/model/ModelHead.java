package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHead extends ModelBase {

    private ModelRenderer main;
    private ModelRenderer overlay;

    public ModelHead() {
        main = new ModelRenderer(this, 0, 0);
        main.addBox(-4F, -8F, -4F, 8, 8, 8);
        main.setRotationPoint(0, 0, 0);

        overlay = new ModelRenderer(this, 32, 0);
        overlay.addBox(-4F, -8F, -4F, 8, 8, 8, 0.5F);
        overlay.setRotationPoint(0F, 0F, 0F);
        overlay.setTextureSize(64, 32);
    }

    public void render(boolean showOverlay) {
        float mult = 0.0625F;
        main.render(mult);
        if (showOverlay) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            overlay.render(mult);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }
}
