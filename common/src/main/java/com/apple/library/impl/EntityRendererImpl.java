package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import net.minecraft.world.entity.Entity;

public interface EntityRendererImpl<T extends Entity> {

    void render(T entity, CGPoint offset, int scale, CGPoint focus, CGGraphicsContext context);
}
