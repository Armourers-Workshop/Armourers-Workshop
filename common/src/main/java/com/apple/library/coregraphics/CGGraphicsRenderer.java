package com.apple.library.coregraphics;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface CGGraphicsRenderer {

    void renderTooltip(NSString text, CGRect rect, UIFont font, CGGraphicsContext context);

    void renderTooltip(ItemStack itemStack, CGRect rect, UIFont font, CGGraphicsContext context);

    void renderEntity(Entity entity, CGPoint offset, int scale, CGPoint focus, CGGraphicsContext context);

    void renderItem(ItemStack itemStack, int x, int y, CGGraphicsContext context);
}

