package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelChest extends ModelBase {

    private ModelRenderer main;
    private ModelRenderer arm1;
    private ModelRenderer arm2;
    
    public ModelChest() {
        main = new ModelRenderer(this, 16, 16);
        main.addBox(-4, -14, -7, 8, 12, 4);
        main.setRotationPoint(0, 0, 0);
        
        arm1 = new ModelRenderer(this, 40, 16);
        arm1.addBox(-7, -14, 3, 4, 12, 4);
        arm1.setRotationPoint(0, 0, 0);

        arm2 = new ModelRenderer(this, 40, 16);
        arm2.mirror = true;
        arm2.addBox(3, -14, 3, 4, 12, 4);
        arm2.setRotationPoint(0, 0, 0);
    }

    public void render() {
        float mult = 0.0625F;
        main.render(mult);
        arm1.render(mult);
        arm2.render(mult);
    }
}
