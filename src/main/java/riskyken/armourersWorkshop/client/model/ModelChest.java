package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelChest extends ModelBase {

    private ModelRenderer main;
    private ModelRenderer leftArm;
    private ModelRenderer rightArm;
    
    public ModelChest() {
        main = new ModelRenderer(this, 16, 16);
        main.addBox(-4, -12, -2, 8, 12, 4);
        main.setRotationPoint(0, 0, 0);
        
        leftArm = new ModelRenderer(this, 40, 16);
        leftArm.mirror = true;
        leftArm.addBox(-2, -12, -2, 4, 12, 4);
        
        leftArm.setRotationPoint(0, 0, 0);

        rightArm = new ModelRenderer(this, 40, 16);
        rightArm.addBox(-2, -12, -2, 4, 12, 4);
        rightArm.setRotationPoint(0, 0, 0);
    }

    public void renderChest() {
        float mult = 0.0625F;
        main.render(mult);
    }
    
    public void renderLeftArm() {
        float mult = 0.0625F;
        leftArm.render(mult);
    }
    
    public void renderRightArm() {
        float mult = 0.0625F;
        rightArm.render(mult);
    }
}
