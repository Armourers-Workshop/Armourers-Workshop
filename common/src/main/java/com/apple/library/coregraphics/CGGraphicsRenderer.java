package com.apple.library.coregraphics;

import com.apple.library.foundation.NSString;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.TooltipRenderer;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        // there are some versions of tooltip that don't split normally,
        // and while we can't decide on the final tooltip size,
        // but we can to handle the break the newline
        List<? extends FormattedCharSequence> texts = context.font.font().split(text.component(), 100000);
        context.screen.renderTooltip(context.poseStack, texts, context.mouseX, context.mouseY);
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
        renderColor(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, color.getRGB(), context);
    }

    public static void renderColor(int x1, int y1, int x2, int y2, int color, CGGraphicsContext context) {
        Screen.fill(context.poseStack, x1, y1, x2, y2, color);
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
        InternalRenderer.INSTANCE.fillGradient(context, i, j, k, l, m, n);
    }

    private static class InternalRenderer extends Screen {

        static final InternalRenderer INSTANCE = new InternalRenderer();

        protected InternalRenderer() {
            super(new NSString("").component());
        }

        public void fillGradient(CGGraphicsContext context, int i, int j, int k, int l, int m, int n) {
            fillGradient(context.poseStack, i, j, k, l, m, n);
        }
    }
}
