package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;
import java.util.BitSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.custom.equipment.CustomModelRenderer;
import riskyken.armourersWorkshop.common.equipment.cubes.ICube;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EquipmentPartRenderer extends ModelBase {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    
    public static final EquipmentPartRenderer INSTANCE = new EquipmentPartRenderer();
    private final CustomModelRenderer main;
    private final Minecraft mc;
    
    public EquipmentPartRenderer() {
        textureWidth = 4;
        textureHeight = 4;
        
        main = new CustomModelRenderer(this, 0, 0);
        main.addBox(0F, 0F, 0F, 1, 1, 1);
        main.setRotationPoint(0, 0, 0);
        mc = Minecraft.getMinecraft();
    }
    
    public void renderPart(CustomEquipmentPartData armourPart, float scale) {
        mc.renderEngine.bindTexture(texture);
        //GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glColor3f(1F, 1F, 1F);
        if (!armourPart.displayNormalCompiled) {
            if (hasNormalBlocks(armourPart.getArmourData())) {
                armourPart.hasNormalBlocks = true;
                armourPart.displayListNormal = GLAllocation.generateDisplayLists(1);
                GL11.glNewList(armourPart.displayListNormal, GL11.GL_COMPILE);
                GL11.glPushMatrix();
                this.renderNomralPartBlocks(armourPart.getArmourData(), scale);
                GL11.glPopMatrix();
                GL11.glEndList();
            }
            armourPart.displayNormalCompiled = true;
        }
        
        if (!armourPart.displayGlowingCompiled) {
            if (hasGlowingBlocks(armourPart.getArmourData())) {
                armourPart.hasGlowingBlocks = true;
                armourPart.displayListGlowing = GLAllocation.generateDisplayLists(1);
                GL11.glNewList(armourPart.displayListGlowing, GL11.GL_COMPILE);
                GL11.glPushMatrix();
                this.renderGlowingPartBlocks(armourPart.getArmourData(), scale);
                GL11.glPopMatrix();
                GL11.glEndList();
            }
            armourPart.displayGlowingCompiled = true;
        }
        
        if (armourPart.hasNormalBlocks) {
            GL11.glCallList(armourPart.displayListNormal);
        }
        
        if (armourPart.hasGlowingBlocks) {
            float lastBrightnessX = OpenGlHelper.lastBrightnessX;
            float lastBrightnessY = OpenGlHelper.lastBrightnessY;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            GL11.glCallList(armourPart.displayListGlowing);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
        GL11.glColor3f(1F, 1F, 1F);
        //GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );
    }
    
    private boolean hasNormalBlocks(ArrayList<ICube> armourBlockData) {
        for (int i = 0; i < armourBlockData.size(); i++) {
            ICube blockData = armourBlockData.get(i);
            if (!blockData.isGlowing()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasGlowingBlocks(ArrayList<ICube> armourBlockData) {
        for (int i = 0; i < armourBlockData.size(); i++) {
            ICube blockData = armourBlockData.get(i);
            if (blockData.isGlowing()) {
                return true;
            }
        }
        return false;
    }
    
    private void renderNomralPartBlocks(ArrayList<ICube> armourBlockData, float scale) {
        GL11.glPushMatrix();
        for (int i = 0; i < armourBlockData.size(); i++) {
            ICube blockData = armourBlockData.get(i);
            if (!blockData.isGlowing()) {
                renderArmourBlock(blockData.getX(), blockData.getY(), blockData.getZ(), blockData.getColour(), scale, blockData.getFaceFlags(), blockData.needsPostRender());
            }
        }
        GL11.glPopMatrix();
    }
    
    private void renderGlowingPartBlocks(ArrayList<ICube> armourBlockData, float scale) {
        GL11.glPushMatrix();
        for (int i = 0; i < armourBlockData.size(); i++) {
            ICube blockData = armourBlockData.get(i);
            if (blockData.isGlowing()) {
                renderArmourBlock(blockData.getX(), blockData.getY(), blockData.getZ(), blockData.getColour(), scale, blockData.getFaceFlags(), blockData.needsPostRender());
            }
        }
        GL11.glPopMatrix();
    }

    public void renderArmourBlock(int x, int y, int z, int colour, float scale, BitSet faceFlags, boolean glass) {
        float colourRed = (colour >> 16 & 0xff) / 255F;
        float colourGreen = (colour >> 8 & 0xff) / 255F;
        float colourBlue = (colour & 0xff) / 255F;

        GL11.glPushMatrix();
        if (glass) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(colourRed, colourGreen, colourBlue, 0.5F);
        } else {
            GL11.glColor3f(colourRed, colourGreen, colourBlue);
        }
        
        GL11.glTranslated(x * scale, y * scale, z * scale);
        if (faceFlags != null) {
            main.render(scale, faceFlags);
        } else {
            ModLogger.log(Level.WARN, "No face flags found on armour part.");
        }
        if (glass) {
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();
    }
}
