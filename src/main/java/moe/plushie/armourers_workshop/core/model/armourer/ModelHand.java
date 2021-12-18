package moe.plushie.armourers_workshop.core.model.armourer;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelHand extends ModelBase {
    
    public static final ModelHand MODEL = new ModelHand();
    
    private ModelRenderer armSolid;
    private ModelRenderer armTransparent;
    
    public ModelHand() {
        armSolid = new ModelRenderer(this, 40, 16);
        armSolid.addBox(-2, -10, -4, 4, 8, 4);
        armSolid.setPos(0, 0, 0);
        
        armTransparent = new ModelRenderer(this, 40, 24);
        armTransparent.addBox(-2, -2, -4, 4, 4, 4);
        armTransparent.setPos(0, 0, 0);
    }
    
//    @Override
//    public void render(Entity entity, float scale, float f2, float f3, float f4, float f5, float f6) {
//        render(scale);
//    }
//
//    public void render(float scale) {
//
//        GL11.glPushMatrix();
//        GL11.glRotatef(-90, 1, 0, 0);
//        GL11.glPushMatrix();
//        armSolid.render(scale);
//        GL11.glPopMatrix();
//        ModRenderHelper.enableAlphaBlend();
//        GL11.glColor4f(1F, 1F, 1F, 0.75F);
//        GL11.glTranslatef(0F, -0.00001F, 0F);
//        armTransparent.render(scale);
//        ModRenderHelper.disableAlphaBlend();
//        GL11.glColor4f(1F, 1F, 1F, 1F);
//        GL11.glPopMatrix();
//
//    }
}
