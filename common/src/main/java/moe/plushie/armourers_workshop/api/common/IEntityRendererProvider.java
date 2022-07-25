package moe.plushie.armourers_workshop.api.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

public interface IEntityRendererProvider<T extends Entity> {

    @Environment(value = EnvType.CLIENT)
    EntityRenderer<? super T> getEntityRenderer(EntityRenderDispatcher dispatcher);

}
