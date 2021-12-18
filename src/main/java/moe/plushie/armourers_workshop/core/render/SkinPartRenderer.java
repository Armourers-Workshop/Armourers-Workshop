package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPart;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.config.skin.ClientSkinPartData;
import moe.plushie.armourers_workshop.core.model.SkinModel;
import moe.plushie.armourers_workshop.core.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.core.render.other.SkinPartRenderData;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SkinPartRenderer {

    private static final ResourceLocation texture = new ResourceLocation("examplemod", "textures/armour/cube.png");
    public static final SkinPartRenderer INSTANCE = new SkinPartRenderer();

    public static final RenderType SOLID = RenderType.create("skin_part_solid", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false, RenderType.State.builder().createCompositeState(true));

    public SkinPartRenderer() {
    }


    public void renderPart(SkinPartRenderData renderData, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
        // ignore part when part is disable
        if (SkinConfig.isEnableSkinPart(renderData.getSkinPart())) {
            return;
        }

        // mc.mcProfiler.startSection(skinPart.getType().getPartName());
//        ModClientFMLEventHandler.skinRendersThisTick++;
        matrixStack.pushPose();

//        Vector3d ro =  mp1;//.get(renderData.getSkinPart().getType().getRegistryName());
//        if (ro != null) {
//            matrixStack.mulPose(Vector3f.XP.rotationDegrees((float) ro.x));
//            matrixStack.mulPose(Vector3f.YP.rotationDegrees((float) ro.y));
//            matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) ro.z));
//        }


        //        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

//        IVertexBuilder builder = renderer.getBuffer(RenderType.lines());
//        Matrix4f mat = matrix.last().pose();
//        builder.vertex(mat, 0, 0, 0).color(255, 0, 0, 255).endVertex();
//        builder.vertex(mat, 10, 0, 0).color(255, 0, 0, 255).endVertex();


        ClientSkinPartData cspd = renderData.getSkinPart().getClientSkinPartData();
        SkinModel skinModel = cspd.getModelForDye(renderData);
        boolean multipassSkinRendering = SkinConfig.multipassSkinRendering;

//        for (int i = 0; i < skinModel.displayList.length; i++) {
//            if (skinModel.haveList[i] && i == 8) { // 0 4 8 12 16
////                if (!skinModel.displayList[i].isCompiled()) {
////                    skinModel.displayList[i].begin();
//                    renderVertexList(cspd.vertexLists[i], renderData, cspd, matrix, renderer);
////                    skinModel.displayList[i].end();
////                    skinModel.setLoaded();
////                }
//            }
//        }
//
//        Minecraft.getInstance().textureManager.bind(texture);

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

//        GlStateManager.pushAttrib();
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GlStateManager.matrixMode(GL11.GL_TEXTURE);
//        double f = PaintingHelper.getPaintTextureOffset();
//        matrix.translate(0, f / 256.0, 0);

        int listCount = skinModel.displayList.length;
        for (int i = startIndex; i < endIndex; i++) {
            if (i >= startIndex & i < endIndex) {
                boolean glowing = false;
                if (i % 2 == 1) {
                    glowing = true;
                }
                if (i >= 0 & i < skinModel.displayList.length) {
                    if (skinModel.haveList[i]) {
//                        if (skinModel.displayList[i].isCompiled()) {
//                            if (glowing) {
//                                GlStateManager.pushAttrib();
//                                GlStateManager.disableLighting();
//                                ModRenderHelper.disableLighting();
//                            }
//                            if (ConfigHandlerClient.wireframeRender) {
//                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
//                                GL11.glLineWidth(1.0F);
//                            }
//                            skinModel.displayList[i].render();
                        IVertexBuilder builder = renderer.getBuffer(SOLID);
                        renderVertexList(cspd.vertexLists[i], renderData, cspd, matrixStack, builder);
//
//                            if (ConfigHandlerClient.wireframeRender) {
//                                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
//                            }
//                            if (glowing) {
//                                ModRenderHelper.enableLighting();
//                                GlStateManager.enableLighting();
//                                GlStateManager.popAttrib();
//                            }
//                        }
                    }
                }
            }
        }

        matrixStack.popPose();
//        GlStateManager.popMatrix();
//        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//
//        GlStateManager.resetColor();
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GlStateManager.popAttrib();
        // mc.mcProfiler.endSection();
    }

    private void renderVertexList(ArrayList<ColouredFace> vertexList, SkinPartRenderData renderData, ClientSkinPartData cspd, MatrixStack matrixStack, IVertexBuilder builder) {

        for (ColouredFace cVert : vertexList) {
            cVert.renderVertex(builder, matrixStack, renderData, cspd);
        }
    }
}
