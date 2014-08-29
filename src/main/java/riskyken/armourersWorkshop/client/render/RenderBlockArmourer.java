package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelChest;
import riskyken.armourersWorkshop.client.model.ModelHead;
import riskyken.armourersWorkshop.client.model.ModelLegs;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockArmourer extends TileEntitySpecialRenderer {

    private static ModelHead modelHead = new ModelHead();
    private static ModelChest modelChest = new ModelChest();
    private static ModelLegs modelLegs = new ModelLegs();
    // private static ModelFeet modelFeet = new ModelFeet();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {

        TileEntityArmourerBrain te = (TileEntityArmourerBrain) tileEntity;
        ArmourerType type = te.getType();
        
        if (!te.isFormed()) { return; }

        this.bindTexture(Minecraft.getMinecraft().thePlayer.getLocationSkin());

        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        GL11.glDisable(GL11.GL_LIGHTING);
        
        GL11.glTranslated(x + te.getXOffset() + 11, y, z + te.getZOffset() + 11);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glScalef(16, 16, 16);
        
        switch (type) {
        case HEAD:
            modelHead.render();
            break;
        case CHEST:
            modelChest.render();
            break;
        case LEGS:
            modelLegs.render(te.isSkirtMode());
            break;
        }
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glColor3f(1F, 1F, 1F);
    }
}
