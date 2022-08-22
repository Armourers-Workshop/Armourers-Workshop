package com.apple.library.coregraphics;

import com.apple.library.foundation.NSString;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.TooltipRenderer;
import com.apple.library.uikit.*;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class CGGraphicsRenderer {

    public static void init() {
    }

    public static void renderTooltip(Object contents, CGRect rect, CGGraphicsContext context) {
        if (contents == null) {
            return;
        }
        NSString text = ObjectUtils.safeCast(contents, NSString.class);
        if (text != null) {
            renderTooltipText(text, rect, context);
            return;
        }
        TooltipRenderer view = ObjectUtils.safeCast(contents, TooltipRenderer.class);
        if (view != null) {
            renderTooltipRender(view, rect, context);
            return;
        }
    }

    public static void renderImage(UIImage image, CGRect rect, UIView view, CGGraphicsContext context) {
        if (image == null) {
            return;
        }
        int u = 0, v = 0, w = rect.width, h = rect.height, mw = 256, mh = 256;
        CGPoint texturePos = image.uv();
        if (texturePos != null) {
            u = texturePos.x;
            v = texturePos.y;
        }
        CGSize size = image.size();
        if (size != null) {
            w = size.width;
            h = size.height;
        }
        CGSize limitSize = image.limit();
        if (limitSize != null) {
            mw = limitSize.width;
            mh = limitSize.height;
        }
        UIImage.AnimationData animation = image.animationData();
        if (animation != null && animation.frames != 0) {
            int frame = (int) ((System.currentTimeMillis() / animation.speed) % animation.frames);
            v += h * frame;
        }
        UIImage.ClipData clipData = image.clipData();
        if (clipData != null) {
            int t = clipData.contentInsets.top;
            int b = clipData.contentInsets.bottom;
            int l = clipData.contentInsets.left;
            int r = clipData.contentInsets.right;
            RenderSystem.drawContinuousTexturedBox(context.poseStack, image.rl(), rect.x, rect.y, u, v, rect.width, rect.height, w, h, t, b, l, r, 0);
            return;
        }
        CGSize sourceSize = image.source();
        if (sourceSize != null) {
            int sw = sourceSize.width;
            int sh = sourceSize.height;
            RenderSystem.resize(context.poseStack, rect.x, rect.y, u, v, w, h, sw, sh, mw, mh, image.rl());
            return;
        }
        RenderSystem.blit(context.poseStack, rect.x, rect.y, u, v, w, h, mw, mh, image.rl());
    }

    public static void renderText(NSString text, int x, int y, @Nullable UIColor color, @Nullable UIColor shadowColor, @Nullable UIFont font, CGGraphicsContext context) {
        if (text == null) {
            return;
        }
        if (font == null) {
            font = context.font;
        }
        if (color == null) {
            color = AppearanceImpl.TEXT_COLOR;
        }
        if (shadowColor != null) {
            font.font().drawShadow(context.poseStack, text.chars(), x, y, color.getRGB());
        } else {
            font.font().draw(context.poseStack, text.chars(), x, y, color.getRGB());
        }
    }

    public static void renderTooltipText(NSString text, CGRect rect, CGGraphicsContext context) {
        if (text == null) {
            return;
        }
        context.screen.renderTooltip(context.poseStack, text.component(), context.mouseX, context.mouseY);
    }

    public static void renderTooltipRender(TooltipRenderer renderer, CGRect rect, CGGraphicsContext context) {
        if (renderer != null) {
            renderer.render(rect, context);
        }
    }

    public static void renderColor(UIColor color, CGRect rect, CGGraphicsContext context) {
        if (color == null || color == UIColor.CLEAR) {
            return;
        }
        Screen.fill(context.poseStack, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, color.getRGB());
    }

    public static void renderGradient(CGGradient gradient, CGRect rect, CGGraphicsContext context) {
        if (gradient == null) {
            return;
        }
        int i = rect.x;
        int j = rect.y;
        int k = rect.getMaxX();
        int l = rect.getMaxY();
        int m = gradient.startColor.getRGB();
        int n = gradient.endColor.getRGB();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
        _fillGradient(context.poseStack.last().pose(), bufferBuilder, i, j, k, l, 0, m, n);
        tesselator.end();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    private static void _fillGradient(Matrix4f matrix4f, BufferBuilder bufferBuilder, int i, int j, int k, int l, int m, int n, int o) {
        float f = (float)(n >> 24 & 0xFF) / 255.0f;
        float g = (float)(n >> 16 & 0xFF) / 255.0f;
        float h = (float)(n >> 8 & 0xFF) / 255.0f;
        float p = (float)(n & 0xFF) / 255.0f;
        float q = (float)(o >> 24 & 0xFF) / 255.0f;
        float r = (float)(o >> 16 & 0xFF) / 255.0f;
        float s = (float)(o >> 8 & 0xFF) / 255.0f;
        float t = (float)(o & 0xFF) / 255.0f;
        bufferBuilder.vertex(matrix4f, k, j, m).color(g, h, p, f).endVertex();
        bufferBuilder.vertex(matrix4f, i, j, m).color(g, h, p, f).endVertex();
        bufferBuilder.vertex(matrix4f, i, l, m).color(r, s, t, q).endVertex();
        bufferBuilder.vertex(matrix4f, k, l, m).color(r, s, t, q).endVertex();
    }
}
