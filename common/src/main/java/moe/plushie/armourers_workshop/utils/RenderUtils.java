package moe.plushie.armourers_workshop.utils;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.IRectangle3i;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.math.Rectangle2i;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;

@Environment(value = EnvType.CLIENT)
public final class RenderUtils {

    public static final ResourceLocation TEX_WARDROBE_1 = res("textures/gui/wardrobe/wardrobe-1.png");
    public static final ResourceLocation TEX_WARDROBE_2 = res("textures/gui/wardrobe/wardrobe-2.png");

    public static final ResourceLocation TEX_HOLOGRAM_PROJECTOR = res("textures/gui/hologram_projector/hologram-projector.png");
    public static final ResourceLocation TEX_SKINNING_TABLE = res("textures/gui/skinning_table/skinning-table.png");
    public static final ResourceLocation TEX_DYE_TABLE = res("textures/gui/dye_table/dye-table.png");
    public static final ResourceLocation TEX_SKIN_LIBRARY = res("textures/gui/skin_library/armour-library.png");
    public static final ResourceLocation TEX_GLOBAL_SKIN_LIBRARY = res("textures/gui/global_library/global-library.png");
    public static final ResourceLocation TEX_COLOUR_MIXER = res("textures/gui/colour_mixer/colour-mixer.png");
    public static final ResourceLocation TEX_OUTFIT_MAKER = res("textures/gui/outfit_maker/outfit-maker.png");
    public static final ResourceLocation TEX_ARMOURER = res("textures/gui/armourer/armourer.png");

    public static final ResourceLocation TEX_TABS = res("textures/gui/controls/tabs.png");
    public static final ResourceLocation TEX_COMMON = res("textures/gui/common.png");
    public static final ResourceLocation TEX_WIDGETS = res("textures/gui/widgets.png");
    public static final ResourceLocation TEX_LIST = res("textures/gui/controls/list.png");
    public static final ResourceLocation TEX_RATING = res("textures/gui/controls/rating.png");
    public static final ResourceLocation TEX_TAB_ICONS = res("textures/gui/controls/tab_icons.png");
    public static final ResourceLocation TEX_HUE = res("textures/gui/controls/slider-hue.png");

    public static final ResourceLocation TEX_BUTTONS = res("textures/gui/controls/buttons.png");
    public static final ResourceLocation TEX_HELP = res("textures/gui/controls/help.png");

    public static final ResourceLocation TEX_PLAYER_INVENTORY = res("textures/gui/player_inventory.png");

    public static final ResourceLocation TEX_CUBE = res("textures/armour/cube.png");
    public static final ResourceLocation TEX_CIRCLE = res("textures/other/nanoha-circle.png");
    public static final ResourceLocation TEX_EARTH = res("textures/tile-entities/global-skin-library.png");

    public static final ResourceLocation TEX_GUI_PREVIEW = res("textures/gui/skin-preview.png");
    public static final ResourceLocation TEX_SKIN_PANEL = res("textures/gui/controls/skin-panel.png");

    public static final ResourceLocation TEX_MARKERS = res("textures/tile-entities/markers.png");
    public static final ResourceLocation TEX_GUIDES = res("textures/block/guide.png");

    public static final ResourceLocation TEX_BLOCK_CUBE = res("textures/block/colourable/colourable.png");
    public static final ResourceLocation TEX_BLOCK_CUBE_GLASS = res("textures/block/colourable/colourable-glass.png");

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

    private static final ArrayList<Rectangle2i> clipBounds = new ArrayList<>();

    private static ResourceLocation res(String name) {
        return ArmourersWorkshop.getResource(name);
    }

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

    public static void blit(PoseStack matrixStack, int x, int y, int u, int v, int width, int height) {
        Screen.blit(matrixStack, x, y, 0, u, v, width, height, 256, 256);
    }

    public static void blit(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, ResourceLocation texture) {
        RenderUtils.bind(texture);
        Screen.blit(matrixStack, x, y, 0, u, v, width, height, 256, 256);
    }

    public static void blit(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight, ResourceLocation texture) {
        RenderUtils.bind(texture);
        Screen.blit(matrixStack, x, y, 0, u, v, width, height, texWidth, texHeight);
    }

    public static void tile(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight, int r0, int r1, int r2, int r3) {
        RenderUtils.drawContinuousTexturedBox(matrixStack, x, y, u, v, width, height, texWidth, texHeight, r0, r1, r2, r3, 0);
    }

    public static void tile(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight, int r0, int r1, int r2, int r3, ResourceLocation texture) {
        RenderUtils.bind(texture);
        RenderUtils.drawContinuousTexturedBox(matrixStack, x, y, u, v, width, height, texWidth, texHeight, r0, r1, r2, r3, 0);
    }

    public static void resize(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int targetWidth, int targetHeight) {
        resize(matrixStack, x, y, u, v, width, height, targetWidth, targetHeight, 256, 256);
    }

    public static void resize(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int sourceWidth, int sourceHeight, int texWidth, int texHeight) {
        float f = 1.0f / texWidth;
        float f1 = 1.0f / texHeight;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(x, y + height, 0).uv(u * f, (v + sourceHeight) * f1).endVertex();
        bufferbuilder.vertex(x + width, y + height, 0).uv((u + sourceWidth) * f, (v + sourceHeight) * f1).endVertex();
        bufferbuilder.vertex(x + width, y, 0).uv((u + sourceWidth) * f, v * f1).endVertex();
        bufferbuilder.vertex(x, y, 0).uv(u * f, v * f1).endVertex();
        tessellator.end();
    }


    public static void resize(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int sourceWidth, int sourceHeight, ResourceLocation texture) {
        RenderUtils.bind(texture);
        RenderUtils.resize(matrixStack, x, y, u, v, width, height, sourceWidth, sourceHeight);
    }

    public static void resize(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int sourceWidth, int sourceHeight, int texWidth, int texHeight, ResourceLocation texture) {
        RenderUtils.bind(texture);
        RenderUtils.resize(matrixStack, x, y, u, v, width, height, sourceWidth, sourceHeight, texWidth, texHeight);
    }

    public static int getPixelColour(int x, int y) {
        Window window = Minecraft.getInstance().getWindow();
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
        Rectangle2i rect = new Rectangle2i(x, y, width, height);
        if (!clipBounds.isEmpty()) {
            Rectangle2i rect1 = clipBounds.get(clipBounds.size() - 1);
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

    public static void applyScissor(Rectangle2i rect) {
        Window window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        int sx = (int) (rect.getX() * guiScale);
        int sy = (int) ((window.getGuiScaledHeight() - rect.getY() - rect.getHeight()) * guiScale);
        int sw = (int) (rect.getWidth() * guiScale);
        int sh = (int) (rect.getHeight() * guiScale);
        RenderSystem.enableScissor(sx, sy, sw, sh);
    }

    public static boolean inScissorRect(int x, int y, int width, int height) {
        if (!clipBounds.isEmpty()) {
            Rectangle2i rectangle = clipBounds.get(clipBounds.size() - 1);
            return rectangle.intersects(x, y, width, height);
        }
        return true;
    }

    public static void drawText(PoseStack matrixStack, Font font, FormattedText text, int x, int y, int width, int zLevel, int textColor) {
        drawText(matrixStack, font, Collections.singleton(text), x, y, width, zLevel, false, 9, textColor);
    }

    public static void drawShadowText(PoseStack matrixStack, Iterable<FormattedText> lines, int x, int y, int width, int zLevel, Font font, int fontSize, int textColor) {
        drawText(matrixStack, font, lines, x, y, width, zLevel, true, fontSize, textColor);
    }

    public static void drawText(PoseStack matrixStack, Font font, Iterable<FormattedText> lines, int x, int y, int width, int zLevel, boolean shadow, int fontSize, int textColor) {
        float f = fontSize / 9f;
        ArrayList<FormattedText> wrappedTextLines = new ArrayList<>();
        for (FormattedText line : lines) {
            wrappedTextLines.addAll(font.getSplitter().splitLines(line, (int) (width / f), Style.EMPTY));
        }
        matrixStack.pushPose();
        matrixStack.translate(x, y, zLevel);
        matrixStack.scale(f, f, f);
        Matrix4f mat = matrixStack.last().pose();
        MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        int dx = 0, dy = 0;
        for (FormattedText line : wrappedTextLines) {
            int qx = font.drawInBatch(Language.getInstance().getVisualOrder(line), dx, dy, textColor, shadow, mat, buffers, false, 0, 15728880);
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

    public static void drawPlayerHead(PoseStack matrixStack, int x, int y, int width, int height, PlayerTextureDescriptor descriptor) {
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

    private static void drawLine(VertexConsumer builder, Matrix4f mat, float x0, float y0, float z0, float x1, float y1, float z1, Color color) {
        builder.vertex(mat, x0, y0, z0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.vertex(mat, x1, y1, z1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawBoundingBox(Matrix4f mat, float x0, float y0, float z0, float x1, float y1, float z1, Color color, VertexConsumer builder) {
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

    public static void drawPoint(PoseStack matrix, @Nullable MultiBufferSource renderTypeBuffer) {
        drawPoint(matrix, null, 2, renderTypeBuffer);
    }

    public static void drawPoint(PoseStack matrix, @Nullable Vector3f point, float size, @Nullable MultiBufferSource renderTypeBuffer) {
        drawPoint(matrix, point, size, size, size, renderTypeBuffer);
    }

    public static void drawPoint(PoseStack matrix, @Nullable Vector3f point, float width, float height, float depth, @Nullable MultiBufferSource renderTypeBuffer) {
        if (renderTypeBuffer == null) {
            renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        }
        Matrix4f mat = matrix.last().pose();
        VertexConsumer builder = renderTypeBuffer.getBuffer(RenderType.lines());
        float x0 = 0;
        float y0 = 0;
        float z0 = 0;
        if (point != null) {
            x0 = point.getX();
            y0 = point.getY();
            z0 = point.getZ();
        }
        drawLine(builder, mat, x0 - width, y0, z0, x0 + width, y0, z0, Color.RED); // x
        drawLine(builder, mat, x0, y0 - height, z0, x0, y0 + height, z0, Color.GREEN); // Y
        drawLine(builder, mat, x0, y0, z0 - depth, x0, y0, z0 + depth, Color.BLUE); // Z
    }

    public static void drawTargetBox(PoseStack matrixStack, float width, float height, float depth, MultiBufferSource buffers) {
        if (ModDebugger.targetBounds) {
            drawBoundingBox(matrixStack, -width / 2, -height / 2, -depth / 2, width / 2, height / 2, depth / 2, Color.ORANGE, buffers);
            drawPoint(matrixStack, null, width, height, depth, buffers);
        }
    }

    public static void drawBoundingBox(PoseStack matrix, float x0, float y0, float z0, float x1, float y1, float z1, Color color, MultiBufferSource renderTypeBuffer) {
        VertexConsumer builder = renderTypeBuffer.getBuffer(RenderType.lines());
        Matrix4f mat = matrix.last().pose();
        drawBoundingBox(mat, x0, y0, z0, x1, y1, z1, color, builder);
    }

    public static void drawBoundingBox(PoseStack matrix, VoxelShape shape, Color color, MultiBufferSource renderTypeBuffer) {
        AABB box = shape.bounds();
        float x0 = (float) box.minX;
        float y0 = (float) box.minY;
        float z0 = (float) box.minZ;
        float x1 = (float) box.maxX;
        float y1 = (float) box.maxY;
        float z1 = (float) box.maxZ;
        drawBoundingBox(matrix, x0, y0, z0, x1, y1, z1, color, renderTypeBuffer);
    }

    public static void drawAllEdges(PoseStack matrix, VoxelShape shape, Color color) {
        MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(RenderType.lines());
        Matrix4f mat = matrix.last().pose();
        shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            builder.vertex(mat, (float) x0, (float) y0, (float) z0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            builder.vertex(mat, (float) x1, (float) y1, (float) z1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        });
    }

    public static void drawBoundingBox(PoseStack matrix, Rectangle3f rec, Color color, MultiBufferSource renderTypeBuffer) {
        float x0 = rec.getMinX();
        float y0 = rec.getMinY();
        float z0 = rec.getMinZ();
        float x1 = rec.getMaxX();
        float y1 = rec.getMaxY();
        float z1 = rec.getMaxZ();
        drawBoundingBox(matrix, x0, y0, z0, x1, y1, z1, color, renderTypeBuffer);
    }

    public static void drawBoundingBox(PoseStack matrix, Rectangle3i rec, Color color, MultiBufferSource renderTypeBuffer) {
        int x0 = rec.getMinX();
        int y0 = rec.getMinY();
        int z0 = rec.getMinZ();
        int x1 = rec.getMaxX();
        int y1 = rec.getMaxY();
        int z1 = rec.getMaxZ();
        drawBoundingBox(matrix, x0, y0, z0, x1, y1, z1, color, renderTypeBuffer);
    }

    public static void drawBoundingBox(PoseStack matrix, AABB rec, Color color, MultiBufferSource renderTypeBuffer) {
        float x0 = (float) rec.minX;
        float y0 = (float) rec.minY;
        float z0 = (float) rec.minZ;
        float x1 = (float) rec.maxX;
        float y1 = (float) rec.maxY;
        float z1 = (float) rec.maxZ;
        drawBoundingBox(matrix, x0, y0, z0, x1, y1, z1, color, renderTypeBuffer);
    }

    public static void drawCube(PoseStack matrix, IRectangle3i rect, float r, float g, float b, float a, MultiBufferSource buffers) {
        float x = rect.getMinX();
        float y = rect.getMinY();
        float z = rect.getMinZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();
        drawCube(matrix, x, y, z, w, h, d, r, g, b, a, buffers);
    }

    public static void drawCube(PoseStack matrix, IRectangle3f rect, float r, float g, float b, float a, MultiBufferSource buffers) {
        float x = rect.getMinX();
        float y = rect.getMinY();
        float z = rect.getMinZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();
        drawCube(matrix, x, y, z, w, h, d, r, g, b, a, buffers);
    }

    public static void drawCube(PoseStack matrix, float x, float y, float z, float w, float h, float d, float r, float g, float b, float a, MultiBufferSource buffers) {
        if (w == 0 || h == 0 || d == 0) {
            return;
        }
        Matrix4f mat = matrix.last().pose();
        SkinVertexBufferBuilder builder1 = SkinVertexBufferBuilder.getBuffer(buffers);
        VertexConsumer builder = builder1.getBuffer(SkinRenderType.GUIDES);
        for (Direction dir : Direction.values()) {
            drawFace(mat, dir, x, y, z, w, h, d, 0, 0, r, g, b, a, builder);
        }
    }

    public static void drawFace(Matrix4f mat, Direction dir, float x, float y, float z, float w, float h, float d, float u, float v, float r, float g, float b, float a, VertexConsumer builder) {
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

    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
     * and filler. It is assumed that the desired texture ResourceLocation object has been bound using
     * Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation).
     *
     * @param matrixStack   the gui matrix stack
     * @param x             x axis offset
     * @param y             y axis offset
     * @param u             bound resource location image x offset
     * @param v             bound resource location image y offset
     * @param width         the desired box width
     * @param height        the desired box height
     * @param textureWidth  the width of the box texture in the resource location image
     * @param textureHeight the height of the box texture in the resource location image
     * @param borderSize    the size of the box's borders
     * @param zLevel        the zLevel to draw at
     */
    public static void drawContinuousTexturedBox(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight,
                                                 int borderSize, float zLevel) {
        drawContinuousTexturedBox(matrixStack, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
    }

    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
     * and filler. The provided ResourceLocation object will be bound using
     * Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation).
     *
     * @param matrixStack   the gui matrix stack
     * @param res           the ResourceLocation object that contains the desired image
     * @param x             x axis offset
     * @param y             y axis offset
     * @param u             bound resource location image x offset
     * @param v             bound resource location image y offset
     * @param width         the desired box width
     * @param height        the desired box height
     * @param textureWidth  the width of the box texture in the resource location image
     * @param textureHeight the height of the box texture in the resource location image
     * @param borderSize    the size of the box's borders
     * @param zLevel        the zLevel to draw at
     */
    public static void drawContinuousTexturedBox(PoseStack matrixStack, ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight,
                                                 int borderSize, float zLevel) {
        drawContinuousTexturedBox(matrixStack, res, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
    }

    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
     * and filler. The provided ResourceLocation object will be bound using
     * Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation).
     *
     * @param matrixStack   the gui matrix stack
     * @param res           the ResourceLocation object that contains the desired image
     * @param x             x axis offset
     * @param y             y axis offset
     * @param u             bound resource location image x offset
     * @param v             bound resource location image y offset
     * @param width         the desired box width
     * @param height        the desired box height
     * @param textureWidth  the width of the box texture in the resource location image
     * @param textureHeight the height of the box texture in the resource location image
     * @param topBorder     the size of the box's top border
     * @param bottomBorder  the size of the box's bottom border
     * @param leftBorder    the size of the box's left border
     * @param rightBorder   the size of the box's right border
     * @param zLevel        the zLevel to draw at
     */
    public static void drawContinuousTexturedBox(PoseStack matrixStack, ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight,
                                                 int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
        Minecraft.getInstance().getTextureManager().bind(res);
        drawContinuousTexturedBox(matrixStack, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
    }

    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
     * and filler. It is assumed that the desired texture ResourceLocation object has been bound using
     * Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation).
     *
     * @param matrixStack   the gui matrix stack
     * @param x             x axis offset
     * @param y             y axis offset
     * @param u             bound resource location image x offset
     * @param v             bound resource location image y offset
     * @param width         the desired box width
     * @param height        the desired box height
     * @param textureWidth  the width of the box texture in the resource location image
     * @param textureHeight the height of the box texture in the resource location image
     * @param topBorder     the size of the box's top border
     * @param bottomBorder  the size of the box's bottom border
     * @param leftBorder    the size of the box's left border
     * @param rightBorder   the size of the box's right border
     * @param zLevel        the zLevel to draw at
     */
    public static void drawContinuousTexturedBox(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight,
                                                 int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        drawTexturedModalRect(matrixStack, x, y, u, v, leftBorder, topBorder, zLevel);
        // Top Right
        drawTexturedModalRect(matrixStack, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
        // Bottom Left
        drawTexturedModalRect(matrixStack, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
        // Bottom Right
        drawTexturedModalRect(matrixStack, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++) {
            // Top Border
            drawTexturedModalRect(matrixStack, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder, zLevel);
            // Bottom Border
            drawTexturedModalRect(matrixStack, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder, zLevel);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
                drawTexturedModalRect(matrixStack, x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), zLevel);
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
            // Left Border
            drawTexturedModalRect(matrixStack, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
            // Right Border
            drawTexturedModalRect(matrixStack, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
        }
    }

    public static void drawTexturedModalRect(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, float zLevel) {
        final float uScale = 1f / 0x100;
        final float vScale = 1f / 0x100;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder wr = tessellator.getBuilder();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = matrixStack.last().pose();
        wr.vertex(matrix, x, y + height, zLevel).uv(u * uScale, ((v + height) * vScale)).endVertex();
        wr.vertex(matrix, x + width, y + height, zLevel).uv((u + width) * uScale, ((v + height) * vScale)).endVertex();
        wr.vertex(matrix, x + width, y, zLevel).uv((u + width) * uScale, (v * vScale)).endVertex();
        wr.vertex(matrix, x, y, zLevel).uv(u * uScale, (v * vScale)).endVertex();
        tessellator.end();
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

//    public static void setLightingForBlock(Level world, BlockPos pos) {
//        int i = world.getCombinedLight(pos, 0);
//        int j = i % 65536;
//        int k = i / 65536;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
//    }

}
