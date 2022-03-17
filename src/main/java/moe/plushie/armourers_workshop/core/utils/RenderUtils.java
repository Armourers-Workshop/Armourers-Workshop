package moe.plushie.armourers_workshop.core.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.AWCore;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.nio.FloatBuffer;

@OnlyIn(Dist.CLIENT)
public final class RenderUtils {

    public static final ResourceLocation TEX_WARDROBE_1 = AWCore.resource("textures/gui/wardrobe/wardrobe-1.png");
    public static final ResourceLocation TEX_WARDROBE_2 = AWCore.resource("textures/gui/wardrobe/wardrobe-2.png");

    public static final ResourceLocation TEX_HOLOGRAM_PROJECTOR = AWCore.resource("textures/gui/hologram_projector/hologram-projector.png");

    public static final ResourceLocation TEX_TABS = AWCore.resource("textures/gui/controls/tabs.png");
    public static final ResourceLocation TEX_COMMON = AWCore.resource("textures/gui/common.png");
    public static final ResourceLocation TEX_TAB_ICONS = AWCore.resource("textures/gui/controls/tab_icons.png");

    public static final ResourceLocation TEX_BUTTONS = AWCore.resource("textures/gui/controls/buttons.png");

    public static final ResourceLocation TEX_PLAYER_INVENTORY = AWCore.resource("textures/gui/player_inventory.png");

    public static final ResourceLocation TEX_ITEMS = AWCore.resource("textures/atlas/items.png");

    public static final ResourceLocation TEX_CUBE = AWCore.resource("textures/armour/cube.png");
    public static final ResourceLocation TEX_CIRCLE = AWCore.resource("textures/other/nanoha-circle.png");
    public static final ResourceLocation TEX_GUI_PREVIEW = AWCore.resource("textures/gui/skin-preview.png");

    private static final FloatBuffer BUFFER = BufferUtils.createFloatBuffer(3);


    public static void call(Runnable task) {
        if (RenderSystem.isOnRenderThread()) {
            task.run();
        } else {
            RenderSystem.recordRenderCall(task::run);
        }
    }

    public static void bind(ResourceLocation texture) {
        Minecraft.getInstance().getTextureManager().bind(texture);
    }

    public static void blit(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height) {
        Screen.blit(matrixStack, x, y, 0, u, v, width, height, 256, 256);
    }

    public static void blit(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, ResourceLocation texture) {
        RenderUtils.bind(texture);
        Screen.blit(matrixStack, x, y, 0, u, v, width, height, 256, 256);
    }

    public static void blit(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight, ResourceLocation texture) {
        RenderUtils.bind(texture);
        Screen.blit(matrixStack, x, y, 0, u, v, width, height, texWidth, texHeight);
    }

    public static int getPixelColour(int x, int y) {
        MainWindow window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        int sx = (int) (x * guiScale);
        int sy = (int) ((window.getGuiScaledHeight() - y) * guiScale);
        BUFFER.rewind();
        GL11.glReadPixels(sx, sy, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, BUFFER);
        GL11.glFinish();
        int r = Math.round(BUFFER.get() * 255);
        int g = Math.round(BUFFER.get() * 255);
        int b = Math.round(BUFFER.get() * 255);
        return 0xff000000 | r << 16 | g << 8 | b;
    }


    public static void enableScissor(int x, int y, int width, int height) {
        MainWindow window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        int sx = (int) (x * guiScale);
        int sy = (int) ((window.getGuiScaledHeight() - y - height) * guiScale);
        int sw = (int) (width * guiScale);
        int sh = (int) (height * guiScale);
        RenderSystem.enableScissor(sx, sy, sw, sh);
    }

    public static void disableScissor() {
        RenderSystem.disableScissor();
    }

    private static void drawLine(IVertexBuilder builder, Matrix4f mat, float x0, float y0, float z0, float x1, float y1, float z1, Color color) {
        builder.vertex(mat, x0, y0, z0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.vertex(mat, x1, y1, z1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawBoundingBox(MatrixStack matrix, float x0, float y0, float z0, float x1, float y1, float z1, Color color, IVertexBuilder builder) {
        Matrix4f mat = matrix.last().pose();
        drawLine(builder, mat, x1, y0, z1, x0, y0, z1, color);
        drawLine(builder, mat, x1, y0, z1, x1, y1, z1, color);
        drawLine(builder, mat, x1, y0, z1, x1, y0, z0, color);
        drawLine(builder, mat, x1, y1, z0, x0, y1, z0, color);
        drawLine(builder, mat, x1, y1, z0, x1, y0, z0, color);
        drawLine(builder, mat, x1, y1, z0, x1, y1, z1, color);
        drawLine(builder, mat, x0, y1, z1, x1, y1, z1, color);
        drawLine(builder, mat, x0, y1, z1, x0, y0, z1, color);
        drawLine(builder, mat, x0, y1, z1, x0, y1, z0, color);
        drawLine(builder, mat, x0, y0, z0, x1, y0, z0, color);
        drawLine(builder, mat, x0, y0, z0, x0, y1, z0, color);
        drawLine(builder, mat, x0, y0, z0, x0, y0, z1, color);
    }

    public static void drawPoint(MatrixStack matrix, @Nullable IRenderTypeBuffer renderTypeBuffer) {
        drawPoint(matrix, null, 2, renderTypeBuffer);
    }

    public static void drawPoint(MatrixStack matrix, @Nullable Vector3f point, float size, @Nullable IRenderTypeBuffer renderTypeBuffer) {
        drawPoint(matrix, point, size, size, size, renderTypeBuffer);
    }

    public static void drawPoint(MatrixStack matrix, @Nullable Vector3f point, float width, float height, float depth, @Nullable IRenderTypeBuffer renderTypeBuffer) {
        if (renderTypeBuffer == null) {
            renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        }
        Matrix4f mat = matrix.last().pose();
        IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.lines());
        float x0 = 0;
        float y0 = 0;
        float z0 = 0;
        if (point != null) {
            x0 = point.x();
            y0 = point.y();
            z0 = point.z();
        }
        drawLine(builder, mat, x0 - width, y0, z0, x0 + width, y0, z0, Color.RED); // x
        drawLine(builder, mat, x0, y0 - height, z0, x0, y0 + height, z0, Color.GREEN); // Y
        drawLine(builder, mat, x0, y0, z0 - depth, x0, y0, z0 + depth, Color.BLUE); // Z
    }

    public static void drawTargetBox(MatrixStack matrixStack, float width, float height, float depth, IRenderTypeBuffer buffers) {
        if (AWConfig.debugTargetBounds) {
            drawBoundingBox(matrixStack, -width / 2, -height / 2, -depth / 2, width / 2, height / 2, depth / 2, Color.ORANGE, buffers);
        }
        if (AWConfig.debugTargetOrigin) {
            drawPoint(matrixStack, null, width, height, depth, buffers);
        }
    }


    public static void drawBoundingBox(MatrixStack matrix, float x0, float y0, float z0, float x1, float y1, float z1, Color color, IRenderTypeBuffer renderTypeBuffer) {
        IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.lines());
        drawBoundingBox(matrix, x0, y0, z0, x1, y1, z1, color, builder);
    }

    public static void drawBoundingBox(MatrixStack matrix, VoxelShape shape, Color color, IRenderTypeBuffer renderTypeBuffer) {
        AxisAlignedBB box = shape.bounds();
        float x0 = (float) box.minX;
        float y0 = (float) box.minY;
        float z0 = (float) box.minZ;
        float x1 = (float) box.maxX;
        float y1 = (float) box.maxY;
        float z1 = (float) box.maxZ;
        drawBoundingBox(matrix, x0, y0, z0, x1, y1, z1, color, renderTypeBuffer);
    }

    public static void drawAllEdges(MatrixStack matrix, VoxelShape shape, Color color) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
        Matrix4f mat = matrix.last().pose();
        shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            builder.vertex(mat, (float) x0, (float) y0, (float) z0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            builder.vertex(mat, (float) x1, (float) y1, (float) z1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        });
    }

    public static void drawBoundingBox(MatrixStack matrix, Rectangle3f rec, Color color, IRenderTypeBuffer renderTypeBuffer) {
        float x0 = rec.getMinX();
        float y0 = rec.getMinY();
        float z0 = rec.getMinZ();
        float x1 = rec.getMaxX();
        float y1 = rec.getMaxY();
        float z1 = rec.getMaxZ();
        drawBoundingBox(matrix, x0, y0, z0, x1, y1, z1, color, renderTypeBuffer);
    }

    public static void drawBoundingBox(MatrixStack matrix, Rectangle3i rec, Color color, IRenderTypeBuffer renderTypeBuffer) {
        int x0 = rec.getMinX();
        int y0 = rec.getMinY();
        int z0 = rec.getMinZ();
        int x1 = rec.getMaxX();
        int y1 = rec.getMaxY();
        int z1 = rec.getMaxZ();
        drawBoundingBox(matrix, x0, y0, z0, x1, y1, z1, color, renderTypeBuffer);
    }


    public static void drawBoundingBox(MatrixStack matrix, AxisAlignedBB rec, Color color, IRenderTypeBuffer renderTypeBuffer) {
        float x0 = (float) rec.minX;
        float y0 = (float) rec.minY;
        float z0 = (float) rec.minZ;
        float x1 = (float) rec.maxX;
        float y1 = (float) rec.maxY;
        float z1 = (float) rec.maxZ;
        drawBoundingBox(matrix, x0, y0, z0, x1, y1, z1, color, renderTypeBuffer);
    }

//    public static void disableLighting() {
//        net.minecraft.client.GameSettings
//        lightX = OpenGlHelper.lastBrightnessX;
//        lightY = OpenGlHelper.lastBrightnessY;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
//    }

//    public static void enableLighting() {
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
//    }

//    public static void setLightingForBlock(World world, BlockPos pos) {
//        int i = world.getCombinedLight(pos, 0);
//        int j = i % 65536;
//        int k = i / 65536;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
//    }

}
