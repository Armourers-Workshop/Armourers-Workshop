package moe.plushie.armourers_workshop.client.render;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.IPoint3D;
import moe.plushie.armourers_workshop.api.common.IRectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.type.block.SkinBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ArmourerRenderHelper {

    private static final ResourceLocation guideImage = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/blocks/guide.png");

    public static void renderBuildingGuide(ISkinType skinType, float scale, SkinProperties skinProps, boolean showHelper) {
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            if (skinPart.isModelOverridden(skinProps)) {
                continue;
            }
            IPoint3D partOffset = skinPart.getOffset();
            GlStateManager.translate(partOffset.getX() * scale, partOffset.getY() * scale, partOffset.getZ() * scale);
            skinPart.renderBuildingGuide(scale, skinProps, showHelper);
            GlStateManager.translate(-partOffset.getX() * scale, -partOffset.getY() * scale, -partOffset.getZ() * scale);

        }
    }

    public static void renderBuildingGrid(ISkinType skinType, float scale, boolean showGuides, SkinProperties skinProps, boolean multiblock) {
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPartType = skinType.getSkinParts().get(i);
            IPoint3D partOffset = skinPartType.getOffset();
            GlStateManager.translate(partOffset.getX() * scale, partOffset.getY() * scale, partOffset.getZ() * scale);
            if (skinType == SkinTypeRegistry.skinBlock) {
                if (skinPartType.getPartName().equals("multiblock") & multiblock) {
                    // GL11.glColor4f(1F, 1F, 0.0F, 0.2F);
                    renderBuildingGrid(((SkinBlock) SkinTypeRegistry.skinBlock).partBase, scale, showGuides, skinProps, 1F, 1F, 0.0F, 0.2F);
                    GL11.glPolygonOffset(6F, 6F);
                    // GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.25F);
                    renderBuildingGrid(skinPartType, scale, showGuides, skinProps, 0.5F, 0.5F, 0.5F, 0.25F);
                } else if (skinPartType.getPartName().equals("base") & !multiblock) {
                    // GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.25F);
                    renderBuildingGrid(skinPartType, scale, showGuides, skinProps, 0.5F, 0.5F, 0.5F, 0.25F);
                }
            } else {
                // GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.25F);
                renderBuildingGrid(skinPartType, scale, showGuides, skinProps, 0.5F, 0.5F, 0.5F, 0.25F);
            }
            GlStateManager.translate(-partOffset.getX() * scale, -partOffset.getY() * scale, -partOffset.getZ() * scale);
        }
    }

    public static void renderBuildingGrid(ISkinPartType skinPartType, float scale, boolean showGuides, SkinProperties skinProps, float r, float g, float b, float a) {
        GlStateManager.translate(0, skinPartType.getBuildingSpace().getY() * scale, 0);
        GlStateManager.scale(-1, -1, 1);
        ArmourerRenderHelper.renderGuidePart(skinPartType, scale, showGuides, skinProps, r, g, b, a);
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.translate(0, -skinPartType.getBuildingSpace().getY() * scale, 0);
    }

    private static void renderGuidePart(ISkinPartType part, float scale, boolean showGuides, SkinProperties skinProps, float r, float g, float b, float a) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(guideImage);

        IRectangle3D buildRec = part.getBuildingSpace();
        IRectangle3D guideRec = part.getGuideSpace();

        GlStateManager.pushMatrix();
        // GL11.glDisable(GL11.GL_LIGHTING);

        if (showGuides) {
            // render building grid
            //
            renderGuideBox(buildRec.getX(), buildRec.getY(), buildRec.getZ(), buildRec.getWidth(), buildRec.getHeight(), buildRec.getDepth(), scale, r, g, b, a);
            // render origin
            // GL11.glColor4f(0F, 1F, 0F, 0.5F);
            renderGuideBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, scale, 0F, 1F, 0F, 0.5F);
        }

        if (ConfigHandlerClient.showArmourerDebugRender) {
            // render debug box
            // GL11.glColor4f(1F, 0F, 0F, 0.25F);
            renderGuideBox(guideRec.getX(), guideRec.getY(), guideRec.getZ(), guideRec.getWidth(), guideRec.getHeight(), guideRec.getDepth(), scale, 1F, 0F, 0F, 0.25F);
        }

        if (part.isModelOverridden(skinProps)) {
            // GL11.glColor4f(0F, 0F, 1F, 0.25F);
            renderGuideBox(guideRec.getX(), guideRec.getY(), guideRec.getZ(), guideRec.getWidth(), guideRec.getHeight(), guideRec.getDepth(), scale, 0F, 0F, 1F, 0.25F);
        }

        // GL11.glColor4f(1F, 1F, 1F, 1F);

        // GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.popMatrix();
    }

    private static void renderGuideBox(double x, double y, double z, int width, int height, int depth, float scale, float r, float g, float b, float a) {
        renderGuideFace(EnumFacing.DOWN, x, y, z, width, depth, scale, r, g, b, a);
        renderGuideFace(EnumFacing.UP, x, y + height, z, width, depth, scale, r, g, b, a);
        renderGuideFace(EnumFacing.EAST, x + width, y, z, depth, height, scale, r, g, b, a);
        renderGuideFace(EnumFacing.WEST, x, y, z, depth, height, scale, r, g, b, a);
        renderGuideFace(EnumFacing.NORTH, x, y, z, width, height, scale, r, g, b, a);
        renderGuideFace(EnumFacing.SOUTH, x, y, z + depth, width, height, scale, r, g, b, a);
    }

    private static void renderGuideFace(EnumFacing dir, double x, double y, double z, double sizeX, double sizeY, float scale, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        float scale1 = 0.999F;
        // GL11.glScalef(scale1, scale1, scale1);
        GlStateManager.translate(x * scale, y * scale, z * scale);

        // GlStateManager.resetColor();
        // GlStateManager.color(1F, 1F, 1F, 1F);

        switch (dir) {
        case EAST:
            GlStateManager.rotate(-90, 0, 1, 0);
            break;
        case WEST:
            GlStateManager.rotate(-90, 0, 1, 0);
            break;
        case UP:
            GlStateManager.rotate(90, 1, 0, 0);
            break;
        case DOWN:
            GlStateManager.rotate(90, 1, 0, 0);
            break;
        default:
            break;
        }

        GlStateManager.enableCull();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buff = tess.getBuffer();
        buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        buff.pos(0, 0, 0).tex(0, 0).color(r, g, b, a).endVertex();

        buff.pos(0, sizeY * scale, 0).tex(sizeY, 0).color(r, g, b, a).endVertex();

        buff.pos(sizeX * scale, sizeY * scale, 0).tex(sizeY, sizeX).color(r, g, b, a).endVertex();

        buff.pos(sizeX * scale, 0, 0).tex(0, sizeX).color(r, g, b, a).endVertex();

        tess.draw();

        ModRenderHelper.disableAlphaBlend();
        GlStateManager.disableBlend();
        // GlStateManager.blendFunc(SourceFactor.SRC_ALPHA,
        // DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.enableCull();

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
}
