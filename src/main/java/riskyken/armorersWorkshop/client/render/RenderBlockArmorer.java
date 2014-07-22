package riskyken.armorersWorkshop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import riskyken.armorersWorkshop.client.model.ModelChest;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockArmorer extends TileEntitySpecialRenderer {

	private ModelChest modelChest = new ModelChest();
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
		
		this.bindTexture(Minecraft.getMinecraft().thePlayer.getLocationSkin());
		
		GL11.glPushMatrix();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		GL11.glTranslated(x, y + 6, z + 4);
		GL11.glScalef(-1, -1, 1);
		GL11.glScalef(16, 16, 16);
		modelChest.render();
		GL11.glPopMatrix();
	}

}
