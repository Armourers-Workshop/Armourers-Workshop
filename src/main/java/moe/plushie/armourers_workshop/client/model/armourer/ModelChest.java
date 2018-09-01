package moe.plushie.armourers_workshop.client.model.armourer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelChest extends ModelBase {

    public static final ModelChest MODEL = new ModelChest();
    
    private ModelRenderer main;
    private ModelRenderer leftArm;
    private ModelRenderer rightArm;
    
    public ModelChest() {
        main = new ModelRenderer(this, 16, 16);
        main.addBox(-4.0F, -12.0F, -2.0F, 8, 12, 4);
        main.setRotationPoint(0, 0, 0);
        
        leftArm = new ModelRenderer(this, 40, 16);
        leftArm.mirror = true;
        leftArm.addBox(-1.0F, -12.0F, -2.0F, 4, 12, 4);
        leftArm.setRotationPoint(0, 0, 0);

        rightArm = new ModelRenderer(this, 40, 16);
        rightArm.addBox(-3.0F, -12.0F, -2.0F, 4, 12, 4);
        rightArm.setRotationPoint(0, 0, 0);
    }

    public void renderChest(float scale) {
        main.render(scale);
    }
    
    public void renderLeftArm(float scale) {
        leftArm.render(scale);
    }
    
    public void renderRightArm(float scale) {
        rightArm.render(scale);
    }
}
