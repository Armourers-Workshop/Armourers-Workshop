package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;
import java.util.BitSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.custom.equipment.CustomModelRenderer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.proxies.ClientProxy;
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
    
    public void renderPart(SkinPart armourPart, float scale) {
        mc.mcProfiler.startSection(armourPart.getPartType().getPartName());
        
        GL11.glColor3f(1F, 1F, 1F);
        if (!armourPart.displayNormalCompiled) {
            if (hasNormalBlocks(armourPart.getArmourData())) {
                armourPart.hasNormalBlocks = true;
                armourPart.displayListNormal = GLAllocation.generateDisplayLists(1);
                GL11.glNewList(armourPart.displayListNormal, GL11.GL_COMPILE);
                this.renderNomralPartBlocks(armourPart.getArmourData(), scale);
                GL11.glEndList();
            }
            armourPart.displayNormalCompiled = true;
        }
        
        if (!armourPart.displayGlowingCompiled) {
            if (hasGlowingBlocks(armourPart.getArmourData())) {
                armourPart.hasGlowingBlocks = true;
                armourPart.displayListGlowing = GLAllocation.generateDisplayLists(1);
                GL11.glNewList(armourPart.displayListGlowing, GL11.GL_COMPILE);
                this.renderGlowingPartBlocks(armourPart.getArmourData(), scale);
                GL11.glEndList();
            }
            armourPart.displayGlowingCompiled = true;
        }
        
        if (armourPart.displayNormalCompiled & armourPart.displayGlowingCompiled) {
            if (!armourPart.modelBaked) {
                armourPart.bakeModel();
            }
        }
        
        if (ClientProxy.shadersModLoaded) {
            mc.renderEngine.bindTexture(texture);
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        
        if (armourPart.hasNormalBlocks) {
            GL11.glCallList(armourPart.displayListNormal);
        }
        
        if (armourPart.hasGlowingBlocks) {
            GL11.glDisable(GL11.GL_LIGHTING);
            ModRenderHelper.disableLighting();
            GL11.glCallList(armourPart.displayListGlowing);
            ModRenderHelper.enableLighting();
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        
        if (!ClientProxy.shadersModLoaded) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        
        GL11.glColor3f(1F, 1F, 1F);
        mc.mcProfiler.endSection();
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
        for (int i = 0; i < armourBlockData.size(); i++) {
            ICube blockData = armourBlockData.get(i);
            if (!blockData.isGlowing()) {
                int colour[] = {blockData.getColour(), blockData.getColour(), blockData.getColour(), blockData.getColour(), blockData.getColour(), blockData.getColour()};
                renderArmourBlock(blockData.getX(), blockData.getY(), blockData.getZ(), colour, scale, blockData.getFaceFlags(), blockData.needsPostRender());
            }
        }
    }
    
    private void renderGlowingPartBlocks(ArrayList<ICube> armourBlockData, float scale) {
        for (int i = 0; i < armourBlockData.size(); i++) {
            ICube blockData = armourBlockData.get(i);
            if (blockData.isGlowing()) {
                int colour[] = {blockData.getColour(), blockData.getColour(), blockData.getColour(), blockData.getColour(), blockData.getColour(), blockData.getColour()};
                renderArmourBlock(blockData.getX(), blockData.getY(), blockData.getZ(), colour, scale, blockData.getFaceFlags(), blockData.needsPostRender());
            }
        }
    }

    public void renderArmourBlock(int x, int y, int z, int[] colour, float scale, BitSet faceFlags, boolean glass) {
        float[] r = new float[6];
        float[] g = new float[6];
        float[] b = new float[6];
        float[] a = new float[6];
        for (int i = 0; i < 6; i++) {
            r[i] = (colour[i] >> 16 & 0xff) / 255F;
            g[i] = (colour[i] >> 8 & 0xff) / 255F;
            b[i] = (colour[i] & 0xff) / 255F;
            a[i] = 1F;
            if (glass) {
                a[i] = 0.5F;
            }
        }
        
        main.render(scale, faceFlags, x, y, z, r, g, b, a);
    }
}
