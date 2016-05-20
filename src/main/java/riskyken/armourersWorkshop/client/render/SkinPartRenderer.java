package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.handler.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.model.SkinModel;
import riskyken.armourersWorkshop.client.model.bake.ColouredFace;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.plushieWrapper.client.IRenderBuffer;
import riskyken.plushieWrapper.client.RenderBridge;

@SideOnly(Side.CLIENT)
public class SkinPartRenderer extends ModelBase {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    public static final SkinPartRenderer INSTANCE = new SkinPartRenderer();
    private final Minecraft mc;
    
    public SkinPartRenderer() {
        mc = Minecraft.getMinecraft();
    }
    
    public void renderPart(SkinPart skinPart, float scale, ISkinDye skinDye, byte[] extraColour) {
        renderPart(skinPart, scale, skinDye, extraColour, 0);
    }
    
    public void renderPart(SkinPart skinPart, float scale, ISkinDye skinDye, byte[] extraColour, int lod) {
        mc.mcProfiler.startSection(skinPart.getPartType().getPartName());
        ModClientFMLEventHandler.skinRendersThisTick++;
        GL11.glColor3f(1F, 1F, 1F);
        
        ClientSkinPartData cspd = skinPart.getClientSkinPartData();
        SkinModel skinModel = cspd.getModelForDye(skinDye, extraColour);
        boolean multipassSkinRendering = ClientProxy.useMultipassSkinRendering();
        
        for (int i = 0; i < skinModel.displayListCompiled.length; i++) {
            if (!skinModel.displayListCompiled[i]) {
                if (skinModel.hasList[i]) {
                    skinModel.displayList[i] = GLAllocation.generateDisplayLists(1);
                    GL11.glNewList(skinModel.displayList[i], GL11.GL_COMPILE);
                    renderVertexList(cspd.vertexLists[i], scale, skinDye, extraColour, cspd);
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
            if (i >= lod * 4 & i < (lod * 4) + 4) {
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
        }
        
        if (!ClientProxy.useSafeTextureRender()) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        
        GL11.glColor3f(1F, 1F, 1F);
        mc.mcProfiler.endSection();
    }
    
    private void renderVertexList(ArrayList<ColouredFace> vertexList, float scale, ISkinDye skinDye, byte[] extraColour, ClientSkinPartData cspd) {
        IRenderBuffer renderBuffer = new RenderBridge().INSTANCE;
        renderBuffer.startDrawingQuads();
        for (int i = 0; i < vertexList.size(); i++) {
            ColouredFace cVert = vertexList.get(i);
            cVert.renderVertex(renderBuffer, skinDye, extraColour, cspd, ClientProxy.useSafeTextureRender());
        }
        renderBuffer.draw();
    }
}
