package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.handler.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.model.SkinModel;
import riskyken.armourersWorkshop.client.model.bake.ColouredFace;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.proxies.ClientProxy;

@SideOnly(Side.CLIENT)
public class SkinPartRenderer extends ModelBase {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    public static final SkinPartRenderer INSTANCE = new SkinPartRenderer();
    private final Minecraft mc;

    public SkinPartRenderer() {
        mc = Minecraft.getMinecraft();
    }

    public void renderPart(SkinPartRenderData renderData) {
        // mc.mcProfiler.startSection(skinPart.getPartType().getPartName());
        ModClientFMLEventHandler.skinRendersThisTick++;
        // GL11.glColor3f(1F, 1F, 1F);

        ClientSkinPartData cspd = renderData.getSkinPart().getClientSkinPartData();
        SkinModel skinModel = cspd.getModelForDye(renderData);
        boolean multipassSkinRendering = ClientProxy.useMultipassSkinRendering();

        for (int i = 0; i < skinModel.displayList.length; i++) {
            if (skinModel.haveList[i]) {
                if (!skinModel.displayList[i].isCompiled()) {
                    skinModel.displayList[i].begin();
                    renderVertexList(cspd.vertexLists[i], renderData, cspd);
                    skinModel.displayList[i].end();
                    skinModel.setLoaded();
                }
            }
        }

        if (ClientProxy.useSafeTextureRender()) {
            mc.renderEngine.bindTexture(texture);
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        int startIndex = 0;
        int endIndex = 0;

        int loadingLod = skinModel.getLoadingLod();
        int lod = renderData.getLod();

        if (!renderData.isDoLodLoading()) {
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
                                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                                GL11.glDisable(GL11.GL_LIGHTING);
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
                                GL11.glEnable(GL11.GL_LIGHTING);
                                GL11.glPopAttrib();
                            }
                        }
                    }
                }
            }
        }

        if (!ClientProxy.useSafeTextureRender()) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glColor3f(1F, 1F, 1F);
        // mc.mcProfiler.endSection();
    }

    private void renderVertexList(ArrayList<ColouredFace> vertexList, SkinPartRenderData renderData, ClientSkinPartData cspd) {
        IRenderBuffer renderBuffer = new RenderBridge().INSTANCE;
        renderBuffer.startDrawingQuads();
        for (int i = 0; i < vertexList.size(); i++) {
            ColouredFace cVert = vertexList.get(i);
            cVert.renderVertex(renderBuffer, renderData, cspd, ClientProxy.useSafeTextureRender());
        }
        renderBuffer.draw();
    }
}
