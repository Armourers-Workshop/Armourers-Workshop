package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import net.minecraft.client.renderer.entity.EntityRenderer;

import java.util.HashMap;
import java.util.function.Function;

public class EntityRendererStorage {

    private final EntityRenderer<?> entityRenderer;
    private final HashMap<Object, BakedArmatureTransformer> transformers = new HashMap<>();

    public EntityRendererStorage(EntityRenderer<?> entityRenderer) {
        this.entityRenderer = entityRenderer;
    }

    public static EntityRendererStorage of(EntityRenderer<?> entityRenderer) {
        return IAssociatedObjectProvider.of(entityRenderer, EntityRendererStorage::new);
    }

    public <T> BakedArmatureTransformer getTransformer(T key) {
        return transformers.get(key);
    }

    public <T> BakedArmatureTransformer computeTransformerIfAbsent(T key, Function<T, BakedArmatureTransformer> factory) {
        return transformers.computeIfAbsent(key, it -> factory.apply(key));
    }
}
