package riskyken.armourersWorkshop.client.model;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelLegs extends ModelBase {
	
	private ModelRenderer leg1;
	private ModelRenderer leg2;
	
	public ModelLegs() {
		leg1 = new ModelRenderer(this, 0, 16);
		leg1.addBox(-4, -12, -2,
				4, 12, 4);
		leg1.setRotationPoint(0, 0, 0);
		
		leg2 = new ModelRenderer(this, 0, 16);
		leg2.mirror = true;
		leg2.addBox(0, -12, -2,
				4, 12, 4);
		leg2.setRotationPoint(0, 0, 0);
	}
	
	public void render() {
		float mult = 0.0625F;
		GL11.glPushMatrix();
		leg1.render(mult);
		
		leg2.render(mult);
		GL11.glPopMatrix();
	}
	
}
