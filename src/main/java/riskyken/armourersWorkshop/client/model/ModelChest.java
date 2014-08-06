package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelChest extends ModelBase {

	private ModelRenderer main;
	
	public ModelChest() {
		main = new ModelRenderer(this, 16, 16);
		main.addBox(-4, -12, -2,
				8, 12, 4);
	main.setRotationPoint(0, 0, 0);
	}
	
	public void render() {
		float mult = 0.0625F;
		main.render(mult);
	}
	
}
