package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsState;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.RectangleTesselator;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import manifold.ext.rt.api.auto;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public interface GraphicsContextImpl {

    CGGraphicsState state();

    default void drawText(NSString text, float x, float y, int textColor) {
        drawText(Collections.singleton(text), x, y, textColor, false, null, 0);
    }

    default void drawText(NSString text, float x, float y, int textColor, @Nullable UIFont font) {
        drawText(Collections.singleton(text), x, y, textColor, false, font, 0);
    }

    default void drawText(NSString text, float x, float y, int textColor, boolean shadow, @Nullable UIFont font, float zLevel) {
        drawText(Collections.singleton(text), x, y, textColor, shadow, font, 0);
    }

    default void drawText(Collection<NSString> lines, float x, float y, int textColor, boolean shadow, @Nullable UIFont font, float zLevel) {
        PoseStack poseStack = state().ctm();
        if (font == null) {
            font = state().font();
        }

        float scale = font.fontSize() / 9f;
        poseStack.pushPose();
        poseStack.translate(x, y, zLevel);
        poseStack.scale(scale, scale, scale);

        auto pose = poseStack.last().pose();
        auto buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        Font renderer = font.impl();

        int dx = 0, dy = 0;
        for (NSString line : lines) {
            int qx = renderer.drawInBatch(line.chars(), dx, dy, textColor, shadow, pose, buffers, false, 0, 15728880);
            if (qx == dx) {
                dy += 7;
            } else {
                dy += 10;
            }
        }

        buffers.endBatch();
        poseStack.popPose();

        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest(); // drawing text causes the alpha test to reset
        RenderSystem.defaultBlendFunc();
    }

    default void drawMultilineText(NSString text, float x, float y, float maxWidth, int textColor, @Nullable UIFont font) {
        drawMultilineText(Collections.singleton(text), x, y, maxWidth, textColor, false, font, 0);
    }

    default void drawMultilineText(NSString text, float x, float y, float maxWidth, int textColor, boolean shadow, @Nullable UIFont font, float zLevel) {
        drawMultilineText(Collections.singleton(text), x, y, maxWidth, textColor, shadow, font, zLevel);
    }

    default void drawMultilineText(Collection<NSString> lines, float x, float y, float maxWidth, int textColor, boolean shadow, @Nullable UIFont font, float zLevel) {
        if (font == null) {
            font = state().font();
        }

        float scale = font.fontSize() / 9f;
        ArrayList<NSString> wrappedTextLines = new ArrayList<>();
        for (NSString line : lines) {
            wrappedTextLines.addAll(line.split(maxWidth / scale, font));
        }
        drawText(wrappedTextLines, x, y, textColor, shadow, font, zLevel);
    }

    default void drawImage(ResourceLocation texture, float x, float y, float width, float height, float u, float v, float texWidth, float texHeight) {
        drawResizableImage(texture, x, y, width, height, u, v, width, height, texWidth, texHeight, 0);
    }

    default void drawImage(ResourceLocation texture, float x, float y, float width, float height, float u, float v, float texWidth, float texHeight, float zLevel) {
        drawResizableImage(texture, x, y, width, height, u, v, width, height, texWidth, texHeight, zLevel);
    }

    default void drawResizableImage(ResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight) {
        drawResizableImage(rl, x, y, width, height, u, v, sourceWidth, sourceHeight, texWidth, texHeight, 0);
    }

    default void drawResizableImage(ResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight, float zLevel) {
        RenderSystem.setShaderTexture(0, rl);
        RectangleTesselator tesselator = new RectangleTesselator(state());
        tesselator.begin(SkinRenderType.GUI_IMAGE, texWidth, texHeight);
        tesselator.blit(x, y, width, height, u, v, sourceWidth, sourceHeight, zLevel);
        tesselator.end();
    }

    default void drawTilableImage(ResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float topBorder, float bottomBorder, float leftBorder, float rightBorder) {
        drawTilableImage(rl, x, y, width, height, u, v, sourceWidth, sourceHeight, 256, 256, topBorder, bottomBorder, leftBorder, rightBorder, 0);
    }

    default void drawTilableImage(ResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float topBorder, float bottomBorder, float leftBorder, float rightBorder, float zLevel) {
        drawTilableImage(rl, x, y, width, height, u, v, sourceWidth, sourceHeight, 256, 256, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
    }

    default void drawTilableImage(ResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight, float topBorder, float bottomBorder, float leftBorder, float rightBorder, float zLevel) {
        RenderSystem.setShaderTexture(0, rl);
        auto tesselator = new RectangleTesselator(state());
        tesselator.begin(SkinRenderType.GUI_IMAGE, texWidth, texHeight);

        float x0 = x + 0;
        float y0 = y + 0;
        float x3 = x0 + width;
        float y3 = y0 + height;
        float x1 = x0 + leftBorder;
        float y1 = y0 + topBorder;
        float x2 = x3 - rightBorder;
        float y2 = y3 - bottomBorder;

        float u0 = u + 0;
        float v0 = v + 0;
        float u3 = u0 + sourceWidth;
        float v3 = v0 + sourceHeight;
        float u1 = u0 + leftBorder;
        float v1 = v0 + topBorder;
        float u2 = u3 - rightBorder;
        float v2 = v3 - bottomBorder;

        tesselator.blit(x0, y0, x1 - x0, y1 - y0, u0, v0, zLevel); // tl
        tesselator.blit(x2, y0, x3 - x2, y1 - y0, u2, v0, zLevel); // tr
        tesselator.blit(x0, y2, x1 - x0, y3 - y2, u0, v2, zLevel); // bl
        tesselator.blit(x2, y2, x3 - x2, y3 - y2, u2, v2, zLevel); // br

        tesselator.tile(x1, y0, x2 - x1, y1 - y0, u1, v0, u2 - u1, v1 - v0, zLevel); // tc
        tesselator.tile(x1, y2, x2 - x1, y3 - y2, u1, v2, u2 - u1, v3 - v2, zLevel); // bc
        tesselator.tile(x0, y1, x1 - x0, y2 - y1, u0, v1, u1 - u0, v2 - v1, zLevel); // lc
        tesselator.tile(x2, y1, x3 - x2, y2 - y1, u2, v1, u3 - u2, v2 - v1, zLevel); // rc

        tesselator.tile(x1, y1, x2 - x1, y2 - y1, u1, v1, u2 - u1, v2 - v1, zLevel); // cc

        tesselator.end();
    }

    default void drawColor(float minX, float minY, float maxX, float maxY, float zLevel, int color1, int color2) {
        int a1 = color1 >> 24 & 0xff;
        int r1 = color1 >> 16 & 0xff;
        int g1 = color1 >> 8 & 0xff;
        int b1 = color1 & 0xff;
        int a2 = color2 >> 24 & 0xff;
        int r2 = color2 >> 16 & 0xff;
        int g2 = color2 >> 8 & 0xff;
        int b2 = color2 & 0xff;
        auto state = state();
        auto pose = state.ctm().last().pose();
        auto buffer = state.buffers().getBuffer(SkinRenderType.GUI_COLOR);
        buffer.vertex(pose, minX, minY, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, minX, maxY, zLevel).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(pose, maxX, maxY, zLevel).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(pose, maxX, minY, zLevel).color(r1, g1, b1, a1).endVertex();
        state.flush();
    }
}
