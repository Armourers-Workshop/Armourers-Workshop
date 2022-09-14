package com.apple.library.coregraphics;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIView;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

@Environment(value = EnvType.CLIENT)
public class CGGraphicsContext {

    public final Screen screen;
    public final UIFont font;

    public final PoseStack poseStack;
    public final float partialTicks;

    public final int mouseX;
    public final int mouseY;

    public CGGraphicsContext(PoseStack poseStack, int mouseX, int mouseY, float partialTicks, UIFont font, Screen screen) {
        this.poseStack = poseStack;
        this.partialTicks = partialTicks;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.font = font;
        this.screen = screen;
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

    public void drawImage(UIImage image, CGRect rect) {
        CGGraphicsRenderer.renderImage(image, rect, null, this);
    }

    public void drawText(NSString text, UIFont font, UIColor color, UIColor shadowColor, int x, int y) {
        CGGraphicsRenderer.renderText(text, x, y, color, shadowColor, font, this);
    }

    public void drawTooltip(Object tooltip, CGRect rect) {
        CGGraphicsRenderer.renderTooltip(tooltip, rect, this);
    }

    public void fillRect(UIColor color, CGRect rect) {
        CGGraphicsRenderer.renderColor(color, rect, this);
    }

    public void fillRect(CGGradient gradient, CGRect rect) {
        CGGraphicsRenderer.renderGradient(gradient, rect, this);
    }

    public void strokeRect(UIColor color, CGRect rect) {
        RenderSystem.drawBoundingBox(poseStack, rect, color);
    }

    public void addClipRect(CGRect rect) {
        RenderSystem.addClipRect(rect);
    }

    public void removeClipRect() {
        RenderSystem.removeClipRect();
    }

    public void saveGraphicsState() {
        poseStack.pushPose();
    }

    public void translateCTM(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) {
            return;
        }
        poseStack.translate(x, y, z);
    }

    public void restoreGraphicsState() {
        poseStack.popPose();
    }

    public void enableBlend() {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public void strokeDebugRect(int tag, CGRect rect) {
        if (ModDebugger.viewHierarchy) {
            strokeRect(ColorUtils.getPaletteColor(tag), rect);
        }
    }
}
