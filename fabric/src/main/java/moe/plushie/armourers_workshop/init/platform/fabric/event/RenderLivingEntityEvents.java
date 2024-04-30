package moe.plushie.armourers_workshop.init.platform.fabric.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

public class RenderLivingEntityEvents {

    public static final Event<Render> PRE = EventFactory.createArrayBacked(Render.class, callbacks -> (entity, partialTicks, light, poseStack, buffers, renderer) -> {
        for (Render callback : callbacks) {
            callback.render(entity, partialTicks, light, poseStack, buffers, renderer);
        }
    });

    public static final Event<Render> POST = EventFactory.createArrayBacked(Render.class, callbacks -> (entity, partialTicks, light, poseStack, buffers, renderer) -> {
        for (Render callback : callbacks) {
            callback.render(entity, partialTicks, light, poseStack, buffers, renderer);
        }
    });

    public interface Render {

        void render(LivingEntity entity, float partialTicks, int lightmap, PoseStack poseStack, MultiBufferSource bufferSource, LivingEntityRenderer<?, ?> renderer);
    }
}
