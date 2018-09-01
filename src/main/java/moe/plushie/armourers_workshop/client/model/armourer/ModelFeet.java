package moe.plushie.armourers_workshop.client.model.armourer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFeet extends ModelBase {
    
    private ModelRenderer legLeft;
    private ModelRenderer legRight;
    
    public ModelFeet() {
        legLeft = new ModelRenderer(this, 0, 16);
        legLeft.addBox(-2, -12, -2, 4, 12, 4);
        legLeft.setRotationPoint(0, 0, 0);

        legRight = new ModelRenderer(this, 0, 16);
        legRight.mirror = true;
        legRight.addBox(-2, -12, -2, 4, 12, 4);
        legRight.setRotationPoint(0, 0, 0);
    }

    public void renderLeftLeft() {
        float mult = 0.0625F;
        legLeft.render(mult);
    }
    
    public void renderRightLeg() {
        float mult = 0.0625F;
        legRight.render(mult);
    }
}
