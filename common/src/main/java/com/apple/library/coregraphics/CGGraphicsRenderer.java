package com.apple.library.coregraphics;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CGGraphicsRenderer {

    void renderTooltip(NSString text, CGRect rect, @Nullable UIFont font, CGGraphicsContext context);

    void renderEntity(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY, CGGraphicsContext context);

    void renderItem(ItemStack itemStack, int x, int y, CGGraphicsContext context);
}

