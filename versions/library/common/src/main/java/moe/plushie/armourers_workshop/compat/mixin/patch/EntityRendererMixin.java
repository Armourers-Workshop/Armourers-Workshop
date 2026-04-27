package moe.plushie.armourers_workshop.compat.mixin.patch;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.state.IEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, 26)")
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends IEntityRenderState> implements IEntityRenderer<T, S> {
}
