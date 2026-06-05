package moe.plushie.armourers_workshop.compat.client.renderer.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractEntityRenderDispatcher {

    private static final List<Consumer<Map<EntityType<?>, Map<String, IEntityRenderer<?, ?>>>>> LISTENERS = new ArrayList<>();

    public static void addListener(Consumer<Map<EntityType<?>, Map<String, IEntityRenderer<?, ?>>>> listener) {
        LISTENERS.add(listener);
    }

    @SuppressWarnings("ConstantValue")
    public static void reload() {
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (dispatcher == null) {
            // if called in a mixin, it will be obtained before the dispatcher is initialized.
            RenderSystem.recordRenderCall(AbstractEntityRenderDispatcher::reload);
            return;
        }
        apply(dispatcher);
    }

    private static void apply(EntityRenderDispatcher dispatcher) {
        var builder = new Builder();
        // collect the player entity renderers.
        dispatcher.playerRenderers.forEach((key, value) -> {
            builder.add(EntityType.PLAYER, String.format("%s", key), value);
        });
        // collect the entity renderers.
        dispatcher.renderers.forEach((key, value) -> {
            builder.add(key, "default", value);
        });
        // notify the listeners.
        LISTENERS.forEach(listener -> listener.accept(builder.build()));
    }

    private static class Builder {

        private final Map<EntityType<?>, Map<String, IEntityRenderer<?, ?>>> renderers = new HashMap<>();

        public void add(EntityType<?> entityType, String model, Object renderer) {
            if (renderer instanceof IEntityRenderer<?, ?> entityRenderer) {
                var entityRenderers = renderers.computeIfAbsent(entityType, k -> new HashMap<>());
                entityRenderers.put(model, entityRenderer);
            }
        }

        public Map<EntityType<?>, Map<String, IEntityRenderer<?, ?>>> build() {
            return renderers;
        }
    }
}
