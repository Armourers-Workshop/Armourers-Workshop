package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IEntityRendererProvider;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface AbstractForgeEntityRenderers {

    static <T extends Entity> void register(EntityType<? extends T> entityType, IEntityRendererProvider<T> provider) {
        EntityRenderers.register(entityType, context -> provider.getEntityRenderer(RendererManager.getEntityContext()));
    }

}
