package com.apple.library.coregraphics;

import com.apple.library.foundation.NSString;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.ClipContextImpl;
import com.apple.library.impl.TooltipRenderer;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.gui.element.ClipGuiElement;
import moe.plushie.armourers_workshop.core.client.gui.element.EntityGuiElement;
import moe.plushie.armourers_workshop.core.client.gui.element.ImageGuiElement;
import moe.plushie.armourers_workshop.core.client.gui.element.ItemGuiElement;
import moe.plushie.armourers_workshop.core.client.gui.element.ShapeGuiElement;
import moe.plushie.armourers_workshop.core.client.gui.element.StateGuiElement;
import moe.plushie.armourers_workshop.core.client.gui.element.TextGuiElement;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("unused")
public class CGGraphicsContext {

    private final CGGraphicsState state;
    private final CGGraphicsParameters param;
    private final CGGraphicsRenderer impl;

    private final ClipContextImpl clip = new ClipContextImpl();

    public CGGraphicsContext(CGGraphicsRenderer renderer) {
        this(new CGGraphicsState(new OpenPoseStack()), new CGGraphicsParameters(0, 0, 0, 0, 0, null), renderer);
    }

    public CGGraphicsContext(CGGraphicsState state, CGGraphicsParameters param, CGGraphicsRenderer renderer) {
        this.state = state;
        this.param = param;
        this.impl = renderer;
    }

    public void flush() {
        draw(StateGuiElement.flush());
    }

    public void drawImage(UIImage image, CGRect rect) {
        if (image == null) {
            return;
        }
        float u = 0, v = 0, w = rect.width(), h = rect.height(), mw = 256, mh = 256;
        var texturePos = image.uv();
        if (texturePos != null) {
            u = texturePos.x();
            v = texturePos.y();
        }
        var size = image.size();
        if (size != null) {
            w = size.width();
            h = size.height();
        }
        var limitSize = image.limit();
        if (limitSize != null) {
            mw = limitSize.width();
            mh = limitSize.height();
        }
        var animation = image.animationData();
        if (animation != null && animation.frames != 0) {
            int frame = (int) ((System.currentTimeMillis() / animation.speed) % animation.frames);
            v += h * frame;
        }
        var clipData = image.clipData();
        if (clipData != null) {
            float t = clipData.contentInsets.top;
            float b = clipData.contentInsets.bottom;
            float l = clipData.contentInsets.left;
            float r = clipData.contentInsets.right;
            drawTilableImage(image.rl(), rect.x, rect.y, rect.width, rect.height, u, v, w, h, mw, mh, t, b, l, r);
            return;
        }
        var sourceSize = image.source();
        if (sourceSize != null) {
            float sw = sourceSize.width;
            float sh = sourceSize.height;
            drawResizableImage(image.rl(), rect.x, rect.y, w, h, u, v, sw, sh, mw, mh);
            return;
        }
        drawResizableImage(image.rl(), rect.x, rect.y, w, h, u, v, w, h, mw, mh);
    }

    public void drawImage(OpenResourceLocation texture, float x, float y, float width, float height, float u, float v, float texWidth, float texHeight) {
        drawResizableImage(texture, x, y, width, height, u, v, width, height, texWidth, texHeight);
    }

    public void drawResizableImage(OpenResourceLocation texture, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight) {
        draw(ImageGuiElement.resizable(x, y, width, height, texture, u, v, sourceWidth, sourceHeight, texWidth, texHeight));
    }

    public void drawTilableImage(OpenResourceLocation texture, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float topBorder, float bottomBorder, float leftBorder, float rightBorder) {
        drawTilableImage(texture, x, y, width, height, u, v, sourceWidth, sourceHeight, 256, 256, topBorder, bottomBorder, leftBorder, rightBorder);
    }

    public void drawTilableImage(OpenResourceLocation texture, float x, float y, float width, float height, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight, float topBorder, float bottomBorder, float leftBorder, float rightBorder) {
        var border = new UIEdgeInsets(topBorder, leftBorder, bottomBorder, rightBorder);
        draw(ImageGuiElement.tilable(x, y, width, height, texture, u, v, sourceWidth, sourceHeight, texWidth, texHeight, border));
    }

    public void drawText(NSString text, float x, float y, UIFont font, @Nullable UIColor color, @Nullable UIColor shadowColor) {
        if (text == null) {
            return;
        }
        if (color == null) {
            color = AppearanceImpl.DEFAULT_TEXT_COLOR;
        }
        drawText(text, x, y, color.value(), shadowColor != null, font);
    }

    public void drawText(NSString text, float x, float y, int textColor) {
        drawText(Collections.singleton(text), x, y, textColor, false, UIFont.systemFont());
    }

    public void drawText(NSString text, float x, float y, int textColor, UIFont font) {
        drawText(Collections.singleton(text), x, y, textColor, false, font);
    }

    public void drawText(NSString text, float x, float y, int textColor, boolean shadow, UIFont font) {
        drawText(Collections.singleton(text), x, y, textColor, shadow, font);
    }

    public void drawText(Collection<NSString> lines, float x, float y, int textColor, boolean shadow, UIFont font) {
        draw(TextGuiElement.normal(x, y, lines, font, textColor, shadow));
    }

    public void drawMultilineText(NSString text, float x, float y, float maxWidth, int textColor, UIFont font) {
        drawMultilineText(Collections.singleton(text), x, y, maxWidth, textColor, false, font);
    }

    public void drawMultilineText(NSString text, float x, float y, float maxWidth, int textColor, boolean shadow, UIFont font) {
        drawMultilineText(Collections.singleton(text), x, y, maxWidth, textColor, shadow, font);
    }

    public void drawMultilineText(Collection<NSString> lines, float x, float y, float maxWidth, int textColor, boolean shadow, UIFont font) {
        var scale = font.fontSize() / 9f;
        var wrappedTextLines = new ArrayList<NSString>();
        for (var line : lines) {
            wrappedTextLines.addAll(line.split(font, maxWidth / scale));
        }
        drawText(wrappedTextLines, x, y, textColor, shadow, font);
    }

    public void drawTooltip(Object tooltip, CGRect rect) {
        if (tooltip == null) {
            return;
        }
        if (tooltip instanceof NSString text) {
            draw(TextGuiElement.tooltip(text, UIFont.systemFont()));
            return;
        }
        if (tooltip instanceof ItemStack itemStack) {
            draw(ItemGuiElement.tooltip(itemStack, UIFont.systemFont()));
            return;
        }
        if (tooltip instanceof TooltipRenderer view) {
            view.render(rect, this);
            return;
        }
        // not supported tooltip content.
    }

    public void drawContents(Object contents, CGRect rect, UIView view) {
        if (contents == null) {
            return;
        }
        if (contents instanceof UIImage image) {
            drawImage(image, rect);
            return;
        }
        if (contents instanceof UIColor color) {
            fillRect(rect, color);
            return;
        }
        if (contents instanceof CGGradient gradient) {
            fillRect(gradient, rect);
            return;
        }
        // not supported contents.
    }

    public void drawEntity(Entity entity, CGPoint origin, int scale, CGPoint focus) {
        draw(EntityGuiElement.newInstance(entity, origin, scale, focus));
    }

    public void drawItem(ItemStack itemStack, int x, int y) {
        draw(ItemGuiElement.icon(itemStack, x, y));
    }

    public void draw(CGGraphicsElement element) {
        element.prepare(this);
        impl.render(element);
    }

    public void fillRect(CGRect rect, UIColor color) {
        if (color != null && color != UIColor.CLEAR) {
            fillRect(rect, color.value());
        }
    }

    public void fillRect(CGRect rect, int color) {
        fillRect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, color, color);
    }

    public void fillRect(float x1, float y1, float x2, float y2, int color) {
        fillRect(x1, y1, x2, y2, color, color);
    }

    public void fillRect(CGGradient gradient, CGRect rect) {
        var startColor = gradient.startColor.value();
        var endColor = gradient.endColor.value();
        fillRect(rect.minX(), rect.minY(), rect.maxX(), rect.maxY(), startColor, endColor);
    }

    public void fillRect(float minX, float minY, float maxX, float maxY, int color1, int color2) {
        draw(ShapeGuiElement.fill(minX, minY, maxX, maxY, color1, color2));
    }

    public void strokeRect(CGRect rect, UIColor color) {
        strokeRect(rect, 1, color.value());
    }

    public void strokeRect(CGRect rect, float lineHeight, UIColor color) {
        strokeRect(rect, lineHeight, color.value());
    }

    public void strokeRect(CGRect rect, float lineHeight, int rgb) {
        strokeRect(rect.minX(), rect.minY(), rect.maxX(), rect.maxY(), lineHeight, rgb);
    }

    public void strokeRect(float minX, float minY, float maxX, float maxY, float height, int color) {
        draw(ShapeGuiElement.stroke(minX, minY, maxX, maxY, height, color));
    }

    public void strokeDebugRect(CGRect rect, int tag) {
        if (ModDebugger.viewHierarchy) {
            var color = Colors.getPaletteColor(tag);
            strokeRect(rect.minX(), rect.minY(), rect.maxX(), rect.maxY(), 0, color);
        }
    }

    public void addClipPath(CGRect rect) {
        addClipPath(rect, 0);
    }

    public void addClipPath(CGRect rect, float cornerRadius) {
        clip.push(rect);
        draw(ClipGuiElement.beginClipLayer(rect, cornerRadius));
    }

    public void removeClipPath() {
        draw(ClipGuiElement.endClipLayer());
        clip.pop();
    }

    public CGRect boundingBoxOfClipPath() {
        return clip.peek();
    }

    public void beginTransparencyLayer() {
        draw(StateGuiElement.beginTransparencyLayer());
    }

    public void endTransparencyLayer() {
        draw(StateGuiElement.endTransparencyLayer());
    }

    public void saveGraphicsState() {
        state.save();
    }

    public void translateCTM(float x, float y, float z) {
        state.translate(x, y, z);
    }

    public void scaleCTM(float x, float y, float z) {
        state.scale(x, y, z);
    }

    public void rotateCTM(float x, float y, float z) {
        state.rotate(x, y, z);
    }

    public void concatenateCTM(CGAffineTransform transform) {
        state.concatenate(transform);
    }

    public void restoreGraphicsState() {
        state.restore();
    }

    public void setBlendMode(CGBlendMode mode) {
        state.setBlendMode(mode);
    }

    public CGBlendMode blendMode() {
        return state.blendMode();
    }

    public void setBlendColor(UIColor color) {
        state.setBlendColor(color);
    }

    public UIColor blendColor() {
        return state.blendColor();
    }

    public IPoseStack ctm() {
        return state.ctm();
    }

    public CGGraphicsState state() {
        return state;
    }

    public CGGraphicsParameters param() {
        return param;
    }
}
