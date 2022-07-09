package moe.plushie.armourers_workshop.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModDebugger;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;

@OnlyIn(Dist.CLIENT)
public final class RenderUtils {

    public static final ResourceLocation TEX_WARDROBE_1 = AWCore.resource("textures/gui/wardrobe/wardrobe-1.png");
    public static final ResourceLocation TEX_WARDROBE_2 = AWCore.resource("textures/gui/wardrobe/wardrobe-2.png");

    public static final ResourceLocation TEX_HOLOGRAM_PROJECTOR = AWCore.resource("textures/gui/hologram_projector/hologram-projector.png");
    public static final ResourceLocation TEX_SKINNING_TABLE = AWCore.resource("textures/gui/skinning_table/skinning-table.png");
    public static final ResourceLocation TEX_DYE_TABLE = AWCore.resource("textures/gui/dye_table/dye-table.png");
    public static final ResourceLocation TEX_SKIN_LIBRARY = AWCore.resource("textures/gui/skin_library/armour-library.png");
    public static final ResourceLocation TEX_GLOBAL_SKIN_LIBRARY = AWCore.resource("textures/gui/global_library/global-library.png");
    public static final ResourceLocation TEX_COLOUR_MIXER = AWCore.resource("textures/gui/colour_mixer/colour-mixer.png");
    public static final ResourceLocation TEX_OUTFIT_MAKER = AWCore.resource("textures/gui/outfit_maker/outfit-maker.png");
    public static final ResourceLocation TEX_ARMOURER = AWCore.resource("textures/gui/armourer/armourer.png");

    public static final ResourceLocation TEX_TABS = AWCore.resource("textures/gui/controls/tabs.png");
    public static final ResourceLocation TEX_COMMON = AWCore.resource("textures/gui/common.png");
    public static final ResourceLocation TEX_WIDGETS = AWCore.resource("textures/gui/widgets.png");
    public static final ResourceLocation TEX_LIST = AWCore.resource("textures/gui/controls/list.png");
    public static final ResourceLocation TEX_RATING = AWCore.resource("textures/gui/controls/rating.png");
    public static final ResourceLocation TEX_TAB_ICONS = AWCore.resource("textures/gui/controls/tab_icons.png");
    public static final ResourceLocation TEX_HUE = AWCore.resource("textures/gui/controls/slider-hue.png");

    public static final ResourceLocation TEX_BUTTONS = AWCore.resource("textures/gui/controls/buttons.png");
    public static final ResourceLocation TEX_HELP = AWCore.resource("textures/gui/controls/help.png");

    public static final ResourceLocation TEX_PLAYER_INVENTORY = AWCore.resource("textures/gui/player_inventory.png");

    public static final ResourceLocation TEX_CUBE = AWCore.resource("textures/armour/cube.png");
    public static final ResourceLocation TEX_CIRCLE = AWCore.resource("textures/other/nanoha-circle.png");
    public static final ResourceLocation TEX_EARTH = AWCore.resource("textures/tile-entities/global-skin-library.png");

    public static final ResourceLocation TEX_GUI_PREVIEW = AWCore.resource("textures/gui/skin-preview.png");
    public static final ResourceLocation TEX_SKIN_PANEL = AWCore.resource("textures/gui/controls/skin-panel.png");

    public static final ResourceLocation TEX_MARKERS = AWCore.resource("textures/tile-entities/markers.png");
    public static final ResourceLocation TEX_GUIDES = AWCore.resource("textures/block/guide.png");

    public static final ResourceLocation TEX_BLOCK_CUBE = AWCore.resource("textures/block/colourable/colourable.png");
    public static final ResourceLocation TEX_BLOCK_CUBE_GLASS = AWCore.resource("textures/block/colourable/colourable-glass.png");

    private static final FloatBuffer BUFFER = BufferUtils.createFloatBuffer(3);

    private static final byte[][][] FACE_MARK_TEXTURES = {
            // 0, 1(w), 2(h), 3(d)
            {{1, 3}, {1, 0}, {0, 0}, {0, 3}},
            {{1, 3}, {1, 0}, {0, 0}, {0, 3}},

            {{1, 2}, {1, 0}, {0, 0}, {0, 2}},
            {{1, 2}, {1, 0}, {0, 0}, {0, 2}},

            {{3, 2}, {3, 0}, {0, 0}, {0, 2}},
            {{3, 2}, {3, 0}, {0, 0}, {0, 2}},
    };

    private static final byte[][][] FACE_MARK_VERTEXES = new byte[][][]{
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // -y
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // +y
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // -z
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // +z
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // -x
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // +x
    };

    private static final ArrayList<Rectangle> clipBounds = new ArrayList<>();


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

    public static void tile(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight, int r0, int r1, int r2, int r3) {
        GuiUtils.drawContinuousTexturedBox(matrixStack, x, y, u, v, width, height, texWidth, texHeight, r0, r1, r2, r3, 0);
    }

    public static void tile(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight, int r0, int r1, int r2, int r3, ResourceLocation texture) {
        RenderUtils.bind(texture);
        GuiUtils.drawContinuousTexturedBox(matrixStack, x, y, u, v, width, height, texWidth, texHeight, r0, r1, r2, r3, 0);
    }

    public static void resize(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int targetWidth, int targetHeight) {
        resize(matrixStack, x, y, u, v, width, height, targetWidth, targetHeight, 256, 256);
    }

    public static void resize(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int sourceWidth, int sourceHeight, int texWidth, int texHeight) {
        float f = 1.0f / texWidth;
        float f1 = 1.0f / texHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.vertex(x, y + height, 0).uv(u * f, (v + sourceHeight) * f1).endVertex();
        bufferbuilder.vertex(x + width, y + height, 0).uv((u + sourceWidth) * f, (v + sourceHeight) * f1).endVertex();
        bufferbuilder.vertex(x + width, y, 0).uv((u + sourceWidth) * f, v * f1).endVertex();
        bufferbuilder.vertex(x, y, 0).uv(u * f, v * f1).endVertex();
        tessellator.end();
    }


    public static void resize(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int sourceWidth, int sourceHeight, ResourceLocation texture) {
        RenderUtils.bind(texture);
        RenderUtils.resize(matrixStack, x, y, u, v, width, height, sourceWidth, sourceHeight);
    }

    public static void resize(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int sourceWidth, int sourceHeight, int texWidth, int texHeight, ResourceLocation texture) {
        RenderUtils.bind(texture);
        RenderUtils.resize(matrixStack, x, y, u, v, width, height, sourceWidth, sourceHeight, texWidth, texHeight);
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
        Rectangle rect = new Rectangle(x, y, width, height);
        if (!clipBounds.isEmpty()) {
            Rectangle rect1 = clipBounds.get(clipBounds.size() - 1);
            rect = rect.intersection(rect1);
        }
        clipBounds.add(rect);
        applyScissor(rect);
    }

    public static void disableScissor() {
        if (clipBounds.isEmpty()) {
            return;
        }
        clipBounds.remove(clipBounds.size() - 1);
        if (!clipBounds.isEmpty()) {
            applyScissor(clipBounds.get(clipBounds.size() - 1));
        } else {
            RenderSystem.disableScissor();
        }
    }

    public static void applyScissor(Rectangle rect) {
        MainWindow window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        int sx = (int) (rect.x * guiScale);
        int sy = (int) ((window.getGuiScaledHeight() - rect.y - rect.height) * guiScale);
        int sw = (int) (rect.width * guiScale);
        int sh = (int) (rect.height * guiScale);
        RenderSystem.enableScissor(sx, sy, sw, sh);
    }

    public static boolean inScissorRect(int x, int y, int width, int height) {
        if (!clipBounds.isEmpty()) {
            Rectangle rectangle = clipBounds.get(clipBounds.size() - 1);
            return rectangle.intersects(x, y, width, height);
        }
        return true;
    }

    public static void drawText(MatrixStack matrixStack, FontRenderer font, ITextProperties text, int x, int y, int width, int zLevel, int textColor) {
        drawText(matrixStack, font, Collections.singleton(text), x, y, width, zLevel, false, 9, textColor);
    }

    public static void drawShadowText(MatrixStack matrixStack, Iterable<ITextProperties> lines, int x, int y, int width, int zLevel, FontRenderer font, int fontSize, int textColor) {
        drawText(matrixStack, font, lines, x, y, width, zLevel, true, fontSize, textColor);
    }

    public static void drawText(MatrixStack matrixStack, FontRenderer font, Iterable<ITextProperties> lines, int x, int y, int width, int zLevel, boolean shadow, int fontSize, int textColor) {
        float f = fontSize / 9f;
        ArrayList<ITextProperties> wrappedTextLines = new ArrayList<>();
        for (ITextProperties line : lines) {
            wrappedTextLines.addAll(font.getSplitter().splitLines(line, (int) (width / f), Style.EMPTY));
        }
        matrixStack.pushPose();
        matrixStack.translate(x, y, zLevel);
        matrixStack.scale(f, f, f);
        Matrix4f mat = matrixStack.last().pose();
        IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());

        int dx = 0, dy = 0;
        for (ITextProperties line : wrappedTextLines) {
            int qx = font.drawInBatch(LanguageMap.getInstance().getVisualOrder(line), dx, dy, textColor, shadow, mat, buffers, false, 0, 15728880);
            if (qx == dx) {
                dy += 7;
            } else {
                dy += 10;
            }
        }

        buffers.endBatch();
        matrixStack.popPose();

        // drawing text causes the Alpha test to reset
        RenderSystem.enableAlphaTest();
    }

    public static void  drawPlayerHead(MatrixStack matrixStack, int x, int y, int width, int height, PlayerTextureDescriptor descriptor) {
        ResourceLocation texture = DefaultPlayerSkin.getDefaultSkin();
        if (!descriptor.isEmpty()) {
            PlayerTexture texture1 = PlayerTextureLoader.getInstance().loadTexture(descriptor);
            if (texture1 != null) {
                texture = texture1.getLocation();
            }
        }
        RenderUtils.bind(texture);
        RenderUtils.resize(matrixStack, x, y, 8, 8, width, height, 8, 8, 64, 64);
        RenderUtils.resize(matrixStack, x - 1, y - 1, 40, 8, width + 2, height + 2, 8, 8, 64, 64);
    }

    private static void drawLine(IVertexBuilder builder, Matrix4f mat, float x0, float y0, float z0, float x1, float y1, float z1, Color color) {
        builder.vertex(mat, x0, y0, z0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.vertex(mat, x1, y1, z1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawBoundingBox(Matrix4f mat, float x0, float y0, float z0, float x1, float y1, float z1, Color color, IVertexBuilder builder) {
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
        if (ModDebugger.targetBounds) {
            drawBoundingBox(matrixStack, -width / 2, -height / 2, -depth / 2, width / 2, height / 2, depth / 2, Color.ORANGE, buffers);
            drawPoint(matrixStack, null, width, height, depth, buffers);
        }
    }

    public static void drawBoundingBox(MatrixStack matrix, float x0, float y0, float z0, float x1, float y1, float z1, Color color, IRenderTypeBuffer renderTypeBuffer) {
        IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.lines());
        Matrix4f mat = matrix.last().pose();
        drawBoundingBox(mat, x0, y0, z0, x1, y1, z1, color, builder);
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

    public static void drawCube(MatrixStack matrix, Rectangle3i rect, float r, float g, float b, float a, IRenderTypeBuffer buffers) {
        float x = rect.getMinX();
        float y = rect.getMinY();
        float z = rect.getMinZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();
        drawCube(matrix, x, y, z, w, h, d, r, g, b, a, buffers);
    }

    public static void drawCube(MatrixStack matrix, Rectangle3f rect, float r, float g, float b, float a, IRenderTypeBuffer buffers) {
        float x = rect.getMinX();
        float y = rect.getMinY();
        float z = rect.getMinZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();
        drawCube(matrix, x, y, z, w, h, d, r, g, b, a, buffers);
    }

    public static void drawCube(MatrixStack matrix, float x, float y, float z, float w, float h, float d, float r, float g, float b, float a, IRenderTypeBuffer buffers) {
        if (w == 0 || h == 0 || d == 0) {
            return;
        }
        Matrix4f mat = matrix.last().pose();
        SkinVertexBufferBuilder builder1 = SkinVertexBufferBuilder.getBuffer(buffers);
        IVertexBuilder builder = builder1.getBuffer(SkinRenderType.GUIDES);
        for (Direction dir : Direction.values()) {
            drawFace(mat, dir, x, y, z, w, h, d, 0, 0, r, g, b, a, builder);
        }
    }

    public static void drawFace(Matrix4f mat, Direction dir, float x, float y, float z, float w, float h, float d, float u, float v, float r, float g, float b, float a, IVertexBuilder builder) {
        byte[][] vertexes = FACE_MARK_VERTEXES[dir.get3DDataValue()];
        byte[][] textures = FACE_MARK_TEXTURES[dir.get3DDataValue()];
        float[] values = {0, w, h, d};
        for (int i = 0; i < 4; ++i) {
            builder.vertex(mat, x + vertexes[i][0] * w, y + vertexes[i][1] * h, z + vertexes[i][2] * d)
                    .color(r, g, b, a)
                    .uv(u + values[textures[i][0]], v + values[textures[i][1]])
                    .endVertex();
        }
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
