package riskyken.armourersWorkshop.client.render;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentType;
import riskyken.armourersWorkshop.client.model.ModelChest;
import riskyken.armourersWorkshop.client.model.ModelFeet;
import riskyken.armourersWorkshop.client.model.ModelHand;
import riskyken.armourersWorkshop.client.model.ModelHead;
import riskyken.armourersWorkshop.client.model.ModelLegs;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockArmourer extends TileEntitySpecialRenderer {
    
    private static final ResourceLocation guideImage = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/blocks/guide.png");
    private static final ModelHead modelHead = new ModelHead();
    private static final ModelChest modelChest = new ModelChest();
    private static final ModelLegs modelLegs = new ModelLegs();
    private static final ModelFeet modelFeet = new ModelFeet();
    private static final ModelHand modelHand = new ModelHand();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {

        TileEntityArmourerBrain te = (TileEntityArmourerBrain) tileEntity;
        EnumEquipmentType type = te.getType();
        
        if (!te.isFormed()) { return; }
        
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        if (te.getGameProfile() != null) {
            Minecraft minecraft = Minecraft.getMinecraft();
            Map map = minecraft.func_152342_ad().func_152788_a(te.getGameProfile());
            if (map.containsKey(Type.SKIN)) {
                resourcelocation = minecraft.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
            }
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourcelocation);
        
        GL11.glPushMatrix();
        GL11.glColor3f(0.8F, 0.8F, 0.8F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glTranslated(x + te.getXOffset() + 11, y, z + te.getZOffset() + 11);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glScalef(16, 16, 16);
        
        switch (type) {
        case NONE:
            break;
        case HEAD:
            modelHead.render(te.isShowOverlay());
            break;
        case CHEST:
            modelChest.render();
            break;
        case LEGS:
            modelLegs.render(false);
            break;
        case SKIRT:
            modelLegs.render(true);
            break;
        case FEET:
            modelFeet.render();
            break;
        case WEAPON:
            modelHand.render();
            break;
        }
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        
        if (te.isShowGuides()) {
            renderGuide(te, type, x + te.getXOffset(), y, z + te.getZOffset());
        }
    }
    
    private void renderGuide(TileEntityArmourerBrain te, EnumEquipmentType type, double x, double y, double z) {
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(guideImage);
        
        switch (type) {
        case NONE:
            break;
        case HEAD:
            renderGuidePart(EnumEquipmentPart.HEAD, x, y, z);
            break;
        case CHEST:
            renderGuidePart(EnumEquipmentPart.CHEST, x, y, z);
            renderGuidePart(EnumEquipmentPart.LEFT_ARM, x, y, z);
            renderGuidePart(EnumEquipmentPart.RIGHT_ARM, x, y, z);
            break;
        case LEGS:
            renderGuidePart(EnumEquipmentPart.LEFT_LEG, x, y, z);
            renderGuidePart(EnumEquipmentPart.RIGHT_LEG, x, y, z);
            break;
        case SKIRT:
            renderGuidePart(EnumEquipmentPart.SKIRT, x, y, z);
            break;
        case FEET:
            renderGuidePart(EnumEquipmentPart.LEFT_FOOT, x, y, z);
            renderGuidePart(EnumEquipmentPart.RIGHT_FOOT, x, y, z);
            break;
        case WEAPON:
            renderGuidePart(EnumEquipmentPart.WEAPON, x, y, z);
            break;  
        }
    }
    
    private void renderGuidePart(EnumEquipmentPart part, double x, double y, double z) {
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);
        renderGuideFace(ForgeDirection.SOUTH, x + part.xOffset, y + 1 + part.yOffset, z  + part.zOffset, part.xSize, part.ySize);
        renderGuideFace(ForgeDirection.EAST, x + part.xOffset, y + 1 + part.yOffset, z  + part.zOffset + part.zSize, part.zSize, part.ySize);
        renderGuideFace(ForgeDirection.WEST, x + part.xOffset + part.xSize, y + 1 + part.yOffset, z  + part.zOffset, part.zSize, part.ySize);
        renderGuideFace(ForgeDirection.NORTH, x + part.xOffset + part.xSize, y + 1 + part.yOffset, z  + part.zOffset + part.zSize, part.xSize, part.ySize);
        renderGuideFace(ForgeDirection.UP, x + part.xOffset, y + 1 + part.ySize  + part.yOffset, z  + part.zOffset, part.xSize, part.zSize);
        renderGuideFace(ForgeDirection.DOWN, x + part.xOffset, y + 1  + part.yOffset, z  + part.zOffset + part.zSize, part.xSize, part.zSize);
        GL11.glEnable(GL11.GL_LIGHTING);
        
        GL11.glPopMatrix();
        
        /*
        //GL11.glColor3f(1F, 0.5F, 0.5F);
        renderGuideFace(ForgeDirection.DOWN, x + part.xOrigin, y + 0 + part.yOrigin, z + 1 + part.zOrigin, 1, 1);
        renderGuideFace(ForgeDirection.UP, x + part.xOrigin, y + 1 + part.yOrigin, z + part.zOrigin, 1, 1);
        */
    }
    
    private void renderGuideFace(ForgeDirection dir, double x, double y, double z, int sizeX, int sizeY) {
        RenderManager renderManager = RenderManager.instance;
        Tessellator tessellator = Tessellator.instance;
        
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.5F);
        
        float scale1 = 0.999F;
        GL11.glScalef(scale1, scale1, scale1);
        
        GL11.glTranslated(x, y, z);
        
        switch (dir) {
        case EAST:
            GL11.glRotated(90, 0, 1, 0);
            break;
        case WEST:
            GL11.glRotated(-90, 0, 1, 0);
            break;
        case NORTH:
            GL11.glRotated(180, 0, 1, 0);
            break; 
        case UP:
            GL11.glRotated(90, 1, 0, 0);
            break;
        case DOWN:
            GL11.glRotated(-90, 1, 0, 0);
            break;
        default:
            break;
        }
        
        tessellator.setBrightness(15728880);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0, 0, 0, 0, 0);
        tessellator.addVertexWithUV(0, sizeY, 0, sizeY, 0);
        tessellator.addVertexWithUV(sizeX, sizeY, 0, sizeY, sizeX);
        tessellator.addVertexWithUV(sizeX, 0, 0, 0, sizeX);
        tessellator.draw();
        
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
}
