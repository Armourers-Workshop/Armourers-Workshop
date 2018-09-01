package moe.plushie.armourers_workshop.client.model.armourer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLegs extends ModelBase {

    public static final ModelLegs MODEL = new ModelLegs();
    
    private ModelRenderer legLeft;
    private ModelRenderer legRight;

    public ModelLegs() {
        legLeft = new ModelRenderer(this, 0, 16);
        legLeft.mirror = true;
        legLeft.addBox(-2, -12, -2, 4, 12, 4);
        legLeft.setRotationPoint(0, 0, 0);

        legRight = new ModelRenderer(this, 0, 16);
        legRight.addBox(-2, -12, -2, 4, 12, 4);
        legRight.setRotationPoint(0, 0, 0);
    }

    public void renderLeftLeft(float scale) {
        legLeft.render(scale);
    }
    
    public void renderRightLeg(float scale) {
        legRight.render(scale);
    }
}
