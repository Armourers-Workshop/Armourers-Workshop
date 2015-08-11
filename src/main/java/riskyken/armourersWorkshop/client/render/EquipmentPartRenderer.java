package riskyken.armourersWorkshop.client.render;

import java.util.BitSet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.client.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.model.bake.ColouredVertexWithUV;
import riskyken.armourersWorkshop.client.model.bake.CustomModelRenderer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.plushieWrapper.client.IRenderBuffer;
import riskyken.plushieWrapper.client.RenderBridge;

@SideOnly(Side.CLIENT)
public class EquipmentPartRenderer extends ModelBase {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    public static final EquipmentPartRenderer INSTANCE = new EquipmentPartRenderer();
    public final CustomModelRenderer main;
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
        ModClientFMLEventHandler.skinRendersThisTick++;
        GL11.glColor3f(1F, 1F, 1F);
        if (!armourPart.displayNormalCompiled) {
            if (armourPart.hasNormalBlocks) {
                armourPart.displayListNormal = GLAllocation.generateDisplayLists(1);
                GL11.glNewList(armourPart.displayListNormal, GL11.GL_COMPILE);
                this.renderNomralPartBlocks(armourPart, scale);
                GL11.glEndList();
            }
            armourPart.displayNormalCompiled = true;
        }
        
        if (!armourPart.displayGlowingCompiled) {
            if (armourPart.hasGlowingBlocks) {
                armourPart.displayListGlowing = GLAllocation.generateDisplayLists(1);
                GL11.glNewList(armourPart.displayListGlowing, GL11.GL_COMPILE);
                this.renderGlowingPartBlocks(armourPart, scale);
                GL11.glEndList();
            }
            armourPart.displayGlowingCompiled = true;
        }
        
        if (ClientProxy.useSafeTextureRender()) {
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
        
        if (!ClientProxy.useSafeTextureRender()) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        
        GL11.glColor3f(1F, 1F, 1F);
        mc.mcProfiler.endSection();
    }
    
    private void renderNomralPartBlocks(SkinPart skinPart, float scale) {
        IRenderBuffer renderBuffer = new RenderBridge().INSTANCE;
        renderBuffer.startDrawingQuads();
        for (int i = 0; i < skinPart.normalVertexList.size(); i++) {
            ColouredVertexWithUV cVert = skinPart.normalVertexList.get(i);
            if (ClientProxy.useSafeTextureRender()) {
                cVert.renderVertexWithUV(renderBuffer);
            } else {
                cVert.renderVertex(renderBuffer);
            }
        }
        renderBuffer.draw();
        
        skinPart.normalVertexList.clear();
        skinPart.normalVertexList = null;
    }
    
    private void renderGlowingPartBlocks(SkinPart skinPart, float scale) {
        IRenderBuffer renderBuffer = new RenderBridge().INSTANCE;
        renderBuffer.startDrawingQuads();
        for (int i = 0; i < skinPart.glowingVertexList.size(); i++) {
            ColouredVertexWithUV cVert = skinPart.glowingVertexList.get(i);
            if (ClientProxy.useSafeTextureRender()) {
                cVert.renderVertexWithUV(renderBuffer);
            } else {
                cVert.renderVertex(renderBuffer);
            }
        }
        renderBuffer.draw();
        
        skinPart.glowingVertexList.clear();
        skinPart.glowingVertexList = null;
    }

    public void renderArmourBlock(int x, int y, int z, ICubeColour colour, float scale, BitSet faceFlags, boolean glass) {
        byte a = (byte) 255;
        if (glass) {
            a = (byte) 127;
        }
        
        main.render(scale, faceFlags, x, y, z, colour.getRed(), colour.getGreen(), colour.getBlue(), a);
    }
}
