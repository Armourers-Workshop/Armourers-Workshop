package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsState;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.RectangleTesselator;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public interface GraphicsContextImpl {

    CGGraphicsState state();

    default void drawText(NSString text, float x, float y, int textColor) {
        drawText(Collections.singleton(text), x, y, textColor, false, UIFont.systemFont(), 0);
    }

    default void drawText(NSString text, float x, float y, int textColor, UIFont font) {
        drawText(Collections.singleton(text), x, y, textColor, false, font, 0);
    }

    default void drawText(NSString text, float x, float y, int textColor, boolean shadow, UIFont font, float zLevel) {
        drawText(Collections.singleton(text), x, y, textColor, shadow, font, 0);
    }

    default void drawText(Collection<NSString> lines, float x, float y, int textColor, boolean shadow, UIFont font, float zLevel) {
        var poseStack = state().ctm();
        var scale = font._getScale();
        poseStack.pushPose();
        poseStack.translate(x, y, zLevel);
        poseStack.scale(scale, scale, scale);

        var pose = AbstractPoseStack.unwrap(poseStack).last();
        var buffers = AbstractBufferSource.tesselator();
        var renderer = font.impl();

        var dx = 0;
        var dy = 0;
        for (var line : lines) {
            var qx = renderer.drawInBatch(line.characters(), dx, dy, textColor, shadow, pose.pose(), buffers.bufferSource(), false, 0, 15728880);
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

    default void drawMultilineText(NSString text, float x, float y, float maxWidth, int textColor, UIFont font) {
        drawMultilineText(Collections.singleton(text), x, y, maxWidth, textColor, false, font, 0);
    }

    default void drawMultilineText(NSString text, float x, float y, float maxWidth, int textColor, boolean shadow, UIFont font, float zLevel) {
        drawMultilineText(Collections.singleton(text), x, y, maxWidth, textColor, shadow, font, zLevel);
    }

    default void drawMultilineText(Collection<NSString> lines, float x, float y, float maxWidth, int textColor, boolean shadow, UIFont font, float zLevel) {
        var scale = font.fontSize() / 9f;
        var wrappedTextLines = new ArrayList<NSString>();
        for (var line : lines) {
            wrappedTextLines.addAll(line.split(font, maxWidth / scale));
        }
        drawText(wrappedTextLines, x, y, textColor, shadow, font, zLevel);
    }

    default void drawImage(IResourceLocation texture, float x, float y, float width, float height, float u, float v, float texWidth, float texHeight) {
        drawResizableImage(texture, x, y, width, height, u, v, width, height, texWidth, texHeight, 0);
    }

    default void drawImage(IResourceLocation texture, float x, float y, float width, float height, float u, float v, float texWidth, float texHeight, float zLevel) {
        drawResizableImage(texture, x, y, width, height, u, v, width, height, texWidth, texHeight, zLevel);
    }

    default void drawResizableImage(IResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight) {
        drawResizableImage(rl, x, y, width, height, u, v, sourceWidth, sourceHeight, texWidth, texHeight, 0);
    }

    default void drawResizableImage(IResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight, float zLevel) {
        var tesselator = new RectangleTesselator(state());
        tesselator.begin(SkinRenderType.GUI_IMAGE, rl, texWidth, texHeight);
        tesselator.blit(x, y, width, height, u, v, sourceWidth, sourceHeight, zLevel);
        tesselator.end();
    }

    default void drawTilableImage(IResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float topBorder, float bottomBorder, float leftBorder, float rightBorder) {
        drawTilableImage(rl, x, y, width, height, u, v, sourceWidth, sourceHeight, 256, 256, topBorder, bottomBorder, leftBorder, rightBorder, 0);
    }

    default void drawTilableImage(IResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float topBorder, float bottomBorder, float leftBorder, float rightBorder, float zLevel) {
        drawTilableImage(rl, x, y, width, height, u, v, sourceWidth, sourceHeight, 256, 256, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
    }

    default void drawTilableImage(IResourceLocation rl, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight, float topBorder, float bottomBorder, float leftBorder, float rightBorder, float zLevel) {
        var x0 = x + 0f;
        var y0 = y + 0f;
        var x3 = x0 + width;
        var y3 = y0 + height;
        var x1 = x0 + leftBorder;
        var y1 = y0 + topBorder;
        var x2 = x3 - rightBorder;
        var y2 = y3 - bottomBorder;

        var u0 = u + 0f;
        var v0 = v + 0f;
        var u3 = u0 + sourceWidth;
        var v3 = v0 + sourceHeight;
        var u1 = u0 + leftBorder;
        var v1 = v0 + topBorder;
        var u2 = u3 - rightBorder;
        var v2 = v3 - bottomBorder;

        var tesselator = new RectangleTesselator(state());

        tesselator.begin(SkinRenderType.GUI_IMAGE, rl, texWidth, texHeight);

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
        var a1 = color1 >> 24 & 0xff;
        var r1 = color1 >> 16 & 0xff;
        var g1 = color1 >> 8 & 0xff;
        var b1 = color1 & 0xff;
        var a2 = color2 >> 24 & 0xff;
        var r2 = color2 >> 16 & 0xff;
        var g2 = color2 >> 8 & 0xff;
        var b2 = color2 & 0xff;
        var state = state();
        var pose = state.ctm().last();
        var buffer = state.bufferSource().getBuffer(SkinRenderType.GUI_COLOR);
        buffer.vertex(pose, minX, minY, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, minX, maxY, zLevel).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(pose, maxX, maxY, zLevel).color(r2, g2, b2, a2).endVertex();
        buffer.vertex(pose, maxX, minY, zLevel).color(r1, g1, b1, a1).endVertex();
        state.flush();
    }

    default void drawBorder(float minX, float minY, float maxX, float maxY, float zLevel, int color) {
        var a1 = color >> 24 & 0xff;
        var r1 = color >> 16 & 0xff;
        var g1 = color >> 8 & 0xff;
        var b1 = color & 0xff;
        var state = state();
        var pose = state.ctm().last();
        var buffer = state.bufferSource().getBuffer(SkinRenderType.lineStrip());
        buffer.vertex(pose, minX, minY, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, minX, maxY, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, maxX, maxY, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, maxX, minY, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, minX, minY, zLevel).color(r1, g1, b1, a1).endVertex();
        state.flush();
    }

    default void drawBorder(float minX, float minY, float maxX, float maxY, float zLevel, float height, int color) {
        var a1 = color >> 24 & 0xff;
        var r1 = color >> 16 & 0xff;
        var g1 = color >> 8 & 0xff;
        var b1 = color & 0xff;
        var sp = height * 0.5f;
        var state = state();
        var pose = state.ctm().last();
        var buffer = state.bufferSource().getBuffer(SkinRenderType.GUI_COLOR);

        buffer.vertex(pose, minX - sp, minY - sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, minX - sp, maxY + sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, minX + sp, maxY + sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, minX + sp, minY - sp, zLevel).color(r1, g1, b1, a1).endVertex();

        buffer.vertex(pose, maxX - sp, minY - sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, maxX - sp, maxY + sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, maxX + sp, maxY + sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, maxX + sp, minY - sp, zLevel).color(r1, g1, b1, a1).endVertex();

        buffer.vertex(pose, minX + sp, minY - sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, minX + sp, minY + sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, maxX - sp, minY + sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, maxX - sp, minY - sp, zLevel).color(r1, g1, b1, a1).endVertex();

        buffer.vertex(pose, minX + sp, maxY - sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, minX + sp, maxY + sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, maxX - sp, maxY + sp, zLevel).color(r1, g1, b1, a1).endVertex();
        buffer.vertex(pose, maxX - sp, maxY - sp, zLevel).color(r1, g1, b1, a1).endVertex();

        state.flush();
    }
}
