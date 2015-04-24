package riskyken.armourersWorkshop.client.model.armourer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHand extends ModelBase {
    
    public static final ModelHand MODEL = new ModelHand();
    
    private ModelRenderer armSolid;
    private ModelRenderer armTransparent;
    
    public ModelHand() {
        armSolid = new ModelRenderer(this, 40, 16);
        armSolid.addBox(-2, -10, -4, 4, 8, 4);
        armSolid.setRotationPoint(0, 0, 0);
        
        armTransparent = new ModelRenderer(this, 40, 24);
        armTransparent.addBox(-2, -2, -4, 4, 4, 4);
        armTransparent.setRotationPoint(0, 0, 0);
    }
    
    public void render(float scale) {
        GL11.glPushMatrix();
        GL11.glRotatef(-90, 1, 0, 0);
        GL11.glPushMatrix();
        armSolid.render(scale);
        GL11.glPopMatrix();
        ModRenderHelper.enableAlphaBlend();
        GL11.glColor4f(1F, 1F, 1F, 0.75F);
        GL11.glTranslatef(0F, -0.00001F, 0F);
        armTransparent.render(scale);
        ModRenderHelper.disableAlphaBlend();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
    }
}
