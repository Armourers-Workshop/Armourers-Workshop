package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLegs extends ModelBase {

    private ModelRenderer legOpen1;
    private ModelRenderer legOpen2;
    private ModelRenderer legClosed1;
    private ModelRenderer legClosed2;
    
    public ModelLegs() {
        legOpen1 = new ModelRenderer(this, 0, 16);
        legOpen1.addBox(-6, -12, -2, 4, 12, 4);
        legOpen1.setRotationPoint(0, 0, 0);

        legOpen2 = new ModelRenderer(this, 0, 16);
        legOpen2.mirror = true;
        legOpen2.addBox(2, -12, -2, 4, 12, 4);
        legOpen2.setRotationPoint(0, 0, 0);
        
        legClosed1 = new ModelRenderer(this, 0, 16);
        legClosed1.addBox(-4, -12, -2, 4, 12, 4);
        legClosed1.setRotationPoint(0, 0, 0);

        legClosed2 = new ModelRenderer(this, 0, 16);
        legClosed2.mirror = true;
        legClosed2.addBox(0, -12, -2, 4, 12, 4);
        legClosed2.setRotationPoint(0, 0, 0);
    }

    public void render(boolean skirtMode) {
        float mult = 0.0625F;
        if (!skirtMode) {
            legClosed1.render(mult);
            legClosed2.render(mult);
        } else {
            legOpen1.render(mult);
            legOpen2.render(mult);
        }
    }
}
