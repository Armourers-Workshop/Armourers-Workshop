package com.apple.library.coregraphics;

import com.apple.library.foundation.NSString;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.TooltipRenderer;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CGGraphicsContext {

    private final CGGraphicsState state;
    private final CGGraphicsRenderer renderer;

    public CGGraphicsContext(CGGraphicsState state, CGGraphicsRenderer renderer) {
        this.state = state;
        this.renderer = renderer;
    }

    public void drawImage(UIImage image, CGRect rect) {
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
            RenderSystem.drawClipImage(image.rl(), rect.x, rect.y, u, v, rect.width, rect.height, w, h, t, b, l, r, 0, state.ctm());
            return;
        }
        CGSize sourceSize = image.source();
        if (sourceSize != null) {
            int sw = sourceSize.width;
            int sh = sourceSize.height;
            RenderSystem.drawImage(image.rl(), rect.x, rect.y, u, v, w, h, sw, sh, mw, mh, state.ctm());
            return;
        }
        renderer.renderImage(image.rl(), rect.x, rect.y, u, v, w, h, mw, mh, this);
    }

    public void drawImage(ResourceLocation texture, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight) {
        renderer.renderImage(texture, x, y, u, v, width, height, texWidth, texHeight, this);
    }

    public void drawImage(ResourceLocation texture, int x, int y, int u, int v, int width, int height, int sourceWidth, int sourceHeight, int texWidth, int texHeight) {
        RenderSystem.drawImage(texture, x, y, u, v, width, height, sourceWidth, sourceHeight, texWidth, texHeight, state.ctm());
    }

    public void drawText(NSString text, int x, int y, @Nullable UIFont font, @Nullable UIColor color, @Nullable UIColor shadowColor) {
        if (text == null) {
            return;
        }
        if (color == null) {
            color = AppearanceImpl.TEXT_COLOR;
        }
        if (shadowColor != null) {
            renderer.renderText(text.chars(), x, y, color.getRGB(), true, false, 0, 0xF000F0, font, this);
        } else {
            renderer.renderText(text.chars(), x, y, color.getRGB(), false, false, 0, 0xF000F0, font, this);
        }
    }

    public void drawText(String text, int x, int y, int textColor) {
        renderer.renderText(Component.literal(text).getVisualOrderText(), x, y, textColor, false, false, 0, 0xF000F0, null, this);
    }

    public void drawText(FormattedCharSequence text, int x, int y, int textColor) {
        renderer.renderText(text, x, y, textColor, false, false, 0, 0xF000F0, null, this);
    }

    public void drawText(Component text, int x, float y, int textColor) {
        renderer.renderText(text.getVisualOrderText(), x, y, textColor, false, false, 0, 0xF000F0, null, this);
    }

    public void drawTooltip(Object tooltip, CGRect rect) {
        if (tooltip == null) {
            return;
        }
        NSString text = ObjectUtils.safeCast(tooltip, NSString.class);
        if (text != null) {
            renderer.renderTooltip(text, rect, null, this);
            return;
        }
        TooltipRenderer view = ObjectUtils.safeCast(tooltip, TooltipRenderer.class);
        if (view != null) {
            view.render(rect, this);
            return;
        }
        // not supported tooltip content.
    }

    public void drawContents(Object contents, CGRect rect, UIView view) {
        if (contents == null) {
            return;
        }
        if (contents instanceof UIImage) {
            drawImage((UIImage) contents, rect);
            return;
        }
        if (contents instanceof UIColor) {
            fillRect((UIColor) contents, rect);
            return;
        }
        if (contents instanceof CGGradient) {
            fillRect((CGGradient) contents, rect);
            return;
        }
        // not supported contents.
    }

    public void drawEntity(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY) {
        renderer.renderEntity(entity, x, y, scale, mouseX, mouseY, this);
    }

    public void drawItem(ItemStack itemStack, int x, int y) {
        renderer.renderItem(itemStack, x, y, this);
    }

    public void drawAvatarContents(ResourceLocation texture, int x, int y, int width, int height) {
        RenderSystem.enableAlphaTest();
        RenderSystem.drawImage(texture, x, y, 8, 8, width, height, 8, 8, 64, 64, state.ctm());
        RenderSystem.drawImage(texture, x - 1, y - 1, 40, 8, width + 2, height + 2, 8, 8, 64, 64, state.ctm());
    }

    public void fillRect(UIColor color, CGRect rect) {
        if (color == null || color == UIColor.CLEAR) {
            return;
        }
        renderer.renderColor(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, color.getRGB(), this);
    }

    public void fillRect(int x1, int y1, int x2, int y2, int color) {
        renderer.renderColor(x1, y1, x2, y2, color, this);
    }

    public void fillRect(CGGradient gradient, CGRect rect) {
        renderer.renderGradient(gradient, rect, this);
    }

    public void strokeRect(UIColor color, CGRect rect) {
        RenderSystem.drawBoundingBox(state.ctm(), rect, color);
    }

    public void addClipRect(CGRect rect) {
        RenderSystem.addClipRect(rect);
    }

    public void removeClipRect() {
        RenderSystem.removeClipRect();
    }

    public void saveGraphicsState() {
        state.save();
    }

    public void translateCTM(float x, float y, float z) {
        state.translate(x, y, z);
    }

    public void rotateCTM(float x, float y, float z) {
        state.rotate(x, y, z);
    }

    public void restoreGraphicsState() {
        state.restore();
    }

    public void enableBlend() {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public void setBlendColor(UIColor color) {
        RenderSystem.setShaderColor(color);
    }

    public void strokeDebugRect(int tag, CGRect rect) {
        if (ModDebugger.viewHierarchy) {
            strokeRect(ColorUtils.getPaletteColor(tag), rect);
        }
    }

    public CGGraphicsState state() {
        return state;
    }
}
