package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.common.IEntityRendererProvider;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface AbstractFabricEntityRenderers {

    static <T extends Entity> void register(EntityType<? extends T> entityType, IEntityRendererProvider<T> provider) {
        EntityRendererRegistry.INSTANCE.register(entityType, (a, b) -> provider.getEntityRenderer(RendererManager.getEntityContext()));
    }
}
