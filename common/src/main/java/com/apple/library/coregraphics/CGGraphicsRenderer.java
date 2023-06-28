package com.apple.library.coregraphics;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface CGGraphicsRenderer {

    void renderText(FormattedCharSequence text, float x, float y, int textColor, boolean shadow, boolean bl2, int j, int k, UIFont font, CGGraphicsContext context);

    void renderImage(ResourceLocation texture, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight, CGGraphicsContext context);

    void renderTooltip(NSString text, CGRect rect, @Nullable UIFont font, CGGraphicsContext context);

    void renderColor(int x1, int y1, int x2, int y2, int color, CGGraphicsContext context);

    void renderGradient(CGGradient gradient, CGRect rect, CGGraphicsContext context);

    void renderEntity(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY);
}

