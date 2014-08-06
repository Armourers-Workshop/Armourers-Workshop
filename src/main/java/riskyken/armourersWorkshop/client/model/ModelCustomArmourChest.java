package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.AbstractCustomArmour;
import riskyken.armourersWorkshop.common.customarmor.ArmourBlockData;
import riskyken.armourersWorkshop.proxies.ClientProxy;

public class ModelCustomArmourChest extends ModelBiped {

	private ModelRenderer main;
	
	public ModelCustomArmourChest() {
		main = new ModelRenderer(this, 28, 20);
		main.addBox(0F, 0F, 0F,
				1, 1, 1);
		main.setRotationPoint(0, 0, 0);
	}
	
	@Override
	public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
		AbstractCustomArmour armourData = ClientProxy.getPlayerCustomArmour(entity, ArmourerType.CHEST);
		if (armourData == null) { return; }
		
		ArrayList<ArmourBlockData> armourBlockData = armourData.getArmourData();
		
		bindPlayerTexture();
		
		for (int i = 0; i < armourBlockData.size(); i++) {
			ArmourBlockData blockData = armourBlockData.get(i);
			renderArmourPart(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
		}
	}
	
	private void renderArmourPart(int x, int y, int z, int colour, float scale) {
        float colourRed = (colour >> 16 & 0xff) / 255F;
        float colourGreen = (colour >> 8 & 0xff) / 255F;
        float colourBlue = (colour & 0xff) / 255F;
		
		GL11.glPushMatrix();
		
		GL11.glColor3f(colourRed, colourGreen, colourBlue);
		
		//ModLogger.log(x + " " + y + " " + z);
		GL11.glTranslated(x * scale, y * scale, z * scale);
		main.render(scale);
		GL11.glPopMatrix();
	}
	
	private void bindPlayerTexture() {
		ResourceLocation playerSkin = Minecraft.getMinecraft().thePlayer.getLocationSkin();
		Minecraft.getMinecraft().getTextureManager().bindTexture(playerSkin);
	}
}
