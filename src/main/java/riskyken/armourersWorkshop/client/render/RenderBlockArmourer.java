package riskyken.armourersWorkshop.client.render;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
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
        float mult = 0.0625F;
        
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
        //GL11.glColor3f(0.8F, 0.8F, 0.8F);
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        //GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glTranslated(x, y, z);
        
        GL11.glTranslated(0, te.getHeightOffset(), 0);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glScalef(16, 16, 16);
        
        switch (type) {
        case NONE:
            break;
        case HEAD:
            modelHead.render(te.isShowOverlay());
            break;
        case CHEST:
            modelChest.renderChest();
            GL11.glTranslated(mult * 12, 0, 0);
            modelChest.renderLeftArm();
            GL11.glTranslated(mult * -24, 0, 0);
            modelChest.renderRightArm();
            break;
        case LEGS:
            GL11.glTranslated(mult * 6, 0, 0);
            modelLegs.renderLeftLeft();
            GL11.glTranslated(mult * -12, 0, 0);
            modelLegs.renderRightLeg();
            break;
        case SKIRT:
            GL11.glTranslated(mult * 2, 0, 0);
            modelLegs.renderLeftLeft();
            GL11.glTranslated(mult * -4, 0, 0);
            modelLegs.renderRightLeg();
            break;
        case FEET:
            GL11.glTranslated(mult * 6, 0, 0);
            modelFeet.renderLeftLeft();
            GL11.glTranslated(mult * -12, 0, 0);
            modelFeet.renderRightLeg();
            break;
        case WEAPON:
            modelHand.render();
            break;
        }
        //GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        
        if (te.isShowGuides()) {
            renderGuide(te, type, x, y, z);
        }
        
        RenderHelper.enableStandardItemLighting();
    }
    
    private void renderGuide(TileEntityArmourerBrain te, EnumEquipmentType type, double x, double y, double z) {
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(guideImage);
        
        switch (type) {
        case NONE:
            break;
        case HEAD:
            renderGuidePart(EnumEquipmentPart.HEAD, x, y + te.getHeightOffset(), z);
            break;
        case CHEST:
            renderGuidePart(EnumEquipmentPart.CHEST, x, y + te.getHeightOffset(), z);
            renderGuidePart(EnumEquipmentPart.LEFT_ARM, x, y + te.getHeightOffset(), z);
            renderGuidePart(EnumEquipmentPart.RIGHT_ARM, x, y + te.getHeightOffset(), z);
            break;
        case LEGS:
            renderGuidePart(EnumEquipmentPart.LEFT_LEG, x, y + te.getHeightOffset(), z);
            renderGuidePart(EnumEquipmentPart.RIGHT_LEG, x, y + te.getHeightOffset(), z);
            break;
        case SKIRT:
            renderGuidePart(EnumEquipmentPart.SKIRT, x, y + te.getHeightOffset(), z);
            break;
        case FEET:
            renderGuidePart(EnumEquipmentPart.LEFT_FOOT, x, y + te.getHeightOffset(), z);
            renderGuidePart(EnumEquipmentPart.RIGHT_FOOT, x, y + te.getHeightOffset(), z);
            break;
        case WEAPON:
            renderGuidePart(EnumEquipmentPart.WEAPON, x, y + te.getHeightOffset(), z);
            break;  
        }
    }
    
    private void renderGuidePart(EnumEquipmentPart part, double x, double y, double z) {
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glPushMatrix();
        
        GL11.glTranslated(part.xLocation, 0, part.zLocation);

        GL11.glDisable(GL11.GL_LIGHTING);
        
        renderGuideFace(ForgeDirection.SOUTH,
                x - part.xOrigin - part.xBuildSpace,
                y - part.botBuildSpace,
                z + part.zOrigin + part.zBuildSpace,
                part.xSize + part.xBuildSpace * 2,
                part.ySize + part.topBuildSpace + part.botBuildSpace);
        
        renderGuideFace(ForgeDirection.EAST,
                x + part.xOrigin + part.xBuildSpace,
                y - part.botBuildSpace,
                z + part.zOrigin + part.zBuildSpace,
                part.zSize + part.xBuildSpace * 2,
                part.ySize + part.topBuildSpace + part.botBuildSpace);
        
        renderGuideFace(ForgeDirection.WEST,
                x - part.xOrigin - part.xBuildSpace,
                y - part.botBuildSpace,
                z - part.zOrigin - part.zBuildSpace,
                part.zSize + part.xBuildSpace * 2,
                part.ySize + part.topBuildSpace + part.botBuildSpace);
        
        renderGuideFace(ForgeDirection.NORTH,
                x + part.xOrigin + part.xBuildSpace,
                y - part.botBuildSpace,
                z - part.zOrigin - part.zBuildSpace,
                part.xSize + part.xBuildSpace * 2,
                part.ySize + part.topBuildSpace + part.botBuildSpace);
        
        renderGuideFace(ForgeDirection.UP,
                x - part.xOrigin - part.xBuildSpace,
                y + part.ySize + part.topBuildSpace,
                z - part.zOrigin - part.zBuildSpace,
                part.xSize + part.xBuildSpace * 2,
                part.zSize + part.zBuildSpace * 2);
        
        renderGuideFace(ForgeDirection.DOWN,
                x - part.xOrigin - part.xBuildSpace,
                y - part.botBuildSpace,
                z - part.zOrigin - part.zBuildSpace,
                part.xSize + part.xBuildSpace * 2,
                part.zSize + part.zBuildSpace * 2);
        
        
        GL11.glEnable(GL11.GL_LIGHTING);
        
        GL11.glPopMatrix();
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
            GL11.glTranslated(0, 0, sizeY);
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
