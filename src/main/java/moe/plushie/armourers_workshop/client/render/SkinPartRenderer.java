package moe.plushie.armourers_workshop.client.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.handler.ModClientFMLEventHandler;
import moe.plushie.armourers_workshop.client.model.SkinModel;
import moe.plushie.armourers_workshop.client.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData;
import moe.plushie.armourers_workshop.common.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinPartRenderer extends ModelBase {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    public static final SkinPartRenderer INSTANCE = new SkinPartRenderer();
    private final Minecraft mc;
    
    public SkinPartRenderer() {
        mc = Minecraft.getMinecraft();
    }
    
    public void renderPart(SkinPart skinPart, float scale, ISkinDye skinDye, byte[] extraColour, boolean doLodLoading) {
        renderPart(skinPart, scale, skinDye, extraColour, 0, doLodLoading);
    }
    
    public void renderPart(SkinPart skinPart, float scale, ISkinDye skinDye, byte[] extraColour, double distance, boolean doLodLoading) {
        int lod = MathHelper.floor(distance / ConfigHandlerClient.lodDistance);
        lod = MathHelper.clamp(lod, 0, ConfigHandlerClient.maxLodLevels);
        renderPart(skinPart, scale, skinDye, extraColour, lod, doLodLoading);
    }
    
    private void renderPart(SkinPart skinPart, float scale, ISkinDye skinDye, byte[] extraColour, int lod, boolean doLodLoading) {
        //mc.profiler.startSection(skinPart.getPartType().getPartName());
        ModClientFMLEventHandler.skinRendersThisTick++;
        //GL11.glColor3f(1F, 1F, 1F);
        
        ClientSkinPartData cspd = skinPart.getClientSkinPartData();
        SkinModel skinModel = cspd.getModelForDye(skinDye, extraColour);
        boolean multipassSkinRendering = ClientProxy.useMultipassSkinRendering();
        
        for (int i = 0; i < skinModel.displayList.length; i++) {
            if (skinModel.haveList[i]) {
                if (!skinModel.displayList[i].isCompiled()) {
                    skinModel.displayList[i].begin();
                    renderVertexList(cspd.vertexLists[i], scale, skinDye, extraColour, cspd);
                    skinModel.displayList[i].end();
                    skinModel.setLoaded();
                }
            }
        }
        
        if (ClientProxy.useSafeTextureRender()) {
            mc.renderEngine.bindTexture(texture);
        } else {
            GlStateManager.disableTexture2D();
        }
        
        int startIndex = 0;
        int endIndex = 0;
        
        int loadingLod = skinModel.getLoadingLod();
        if (!doLodLoading) {
            loadingLod = 0;
        }
        if (loadingLod > lod) {
            lod = loadingLod;
        }
        
        if (lod != 0) {
            if (multipassSkinRendering) {
                startIndex = lod * 4;
            } else {
                startIndex = lod * 2;
            }
        }
        
        if (multipassSkinRendering) {
            endIndex = startIndex + 4;
        } else {
            endIndex = startIndex + 2;
        }

        
        int listCount = skinModel.displayList.length;
        for (int i = startIndex; i < endIndex; i++) {
            if (i >= startIndex & i < endIndex) {
                boolean glowing = false;
                if (i % 2 == 1) {
                    glowing = true;
                }
                if (i >= 0 & i < skinModel.displayList.length) {
                    if (skinModel.haveList[i]) {
                        if (skinModel.displayList[i].isCompiled()) {
                            if (glowing) {
                                GlStateManager.pushAttrib();
                                GlStateManager.disableLighting();
                                ModRenderHelper.disableLighting();
                            }
                            if (ConfigHandlerClient.wireframeRender) {
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                                GL11.glLineWidth(1.0F);
                            }
                            skinModel.displayList[i].render();
                            if (ConfigHandlerClient.wireframeRender) {
                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                            }
                            if (glowing) {
                                ModRenderHelper.enableLighting();
                                GlStateManager.enableLighting();
                                GlStateManager.popAttrib();
                            }
                        }
                    }
                }
            }
        }
        
        if (!ClientProxy.useSafeTextureRender()) {
            GlStateManager.enableTexture2D();
        }
        GlStateManager.resetColor();
        //mc.profiler.endSection();
    }
    
    private void renderVertexList(ArrayList<ColouredFace> vertexList, float scale, ISkinDye skinDye, byte[] extraColour, ClientSkinPartData cspd) {
        IRenderBuffer renderBuffer = RenderBridge.INSTANCE;
        renderBuffer.startDrawingQuads(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        for (int i = 0; i < vertexList.size(); i++) {
            ColouredFace cVert = vertexList.get(i);
            cVert.renderVertex(renderBuffer, skinDye, extraColour, cspd, ClientProxy.useSafeTextureRender());
        }
        renderBuffer.draw();
    }
}
