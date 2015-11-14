package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;
import java.util.BitSet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.handler.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.model.SkinModel;
import riskyken.armourersWorkshop.client.model.bake.ColouredVertexWithUV;
import riskyken.armourersWorkshop.client.model.bake.CustomModelRenderer;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
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
    
    public void renderPart(SkinPart skinPart, float scale, ISkinDye skinDye) {
        mc.mcProfiler.startSection(skinPart.getPartType().getPartName());
        ModClientFMLEventHandler.skinRendersThisTick++;
        GL11.glColor3f(1F, 1F, 1F);
        
        ClientSkinPartData cspd = skinPart.getClientSkinPartData();
        SkinModel skinModel = cspd.getModelForDye(skinDye);
        
        for (int i = 0; i < skinModel.displayListCompiled.length; i++) {
            if (!skinModel.displayListCompiled[i]) {
                if (skinModel.hasList[i]) {
                    skinModel.displayList[i] = GLAllocation.generateDisplayLists(1);
                    GL11.glNewList(skinModel.displayList[i], GL11.GL_COMPILE);
                    renderVertexList(cspd.vertexLists[i], scale, skinDye);
                    //TODO Do not clear this!
                    //cspd.vertexLists[i].clear();
                    GL11.glEndList();
                }
                skinModel.displayListCompiled[i] = true;
            }
        }
        
        if (ClientProxy.useSafeTextureRender()) {
            mc.renderEngine.bindTexture(texture);
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        
        for (int i = 0; i < skinModel.displayList.length; i++) {
            boolean glowing = false;
            if (i % 2 == 1) {
                glowing = true;
            }
            if (skinModel.hasList[i]) {
                if (skinModel.displayListCompiled[i]) {
                    if (glowing) {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        ModRenderHelper.disableLighting();
                    }
                    if (ConfigHandler.wireframeRender) {
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    }
                    GL11.glCallList(skinModel.displayList[i]);
                    if (ConfigHandler.wireframeRender) {
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                    }
                    if (glowing) {
                        ModRenderHelper.enableLighting();
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }
            }
        }
        
        if (!ClientProxy.useSafeTextureRender()) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        
        GL11.glColor3f(1F, 1F, 1F);
        mc.mcProfiler.endSection();
    }
    
    private void renderVertexList(ArrayList<ColouredVertexWithUV> vertexList, float scale, ISkinDye skinDye) {
        IRenderBuffer renderBuffer = new RenderBridge().INSTANCE;
        renderBuffer.startDrawingQuads();
        for (int i = 0; i < vertexList.size(); i++) {
            ColouredVertexWithUV cVert = vertexList.get(i);
            if (ClientProxy.useSafeTextureRender()) {
                cVert.renderVertexWithUV(renderBuffer, skinDye);
            } else {
                cVert.renderVertex(renderBuffer, skinDye);
            }
        }
        renderBuffer.draw();
    }

    public void renderArmourBlock(int x, int y, int z, ICubeColour colour, float scale, BitSet faceFlags, boolean glass) {
        byte a = (byte) 255;
        if (glass) {
            a = (byte) 127;
        }
        
        main.render(scale, faceFlags, x, y, z, colour.getRed(), colour.getGreen(), colour.getBlue(), a);
    }
}
