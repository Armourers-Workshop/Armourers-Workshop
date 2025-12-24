package moe.plushie.armourers_workshop.compat.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.event.client.RenderHighlightEvent;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;

@Available("[1.21, 1.22)")
public class AbstractFabricRenderHighlightEvent {

    public static IEventHandler<RenderHighlightEvent.Block> blockFactory() {
        return (priority, receiveCancelled, subscriber) -> WorldRenderEvents.BLOCK_OUTLINE.register(((context, outlineContext) -> {
            subscriber.accept(new RenderHighlightEvent.Block() {
                @Override
                public float partialTick() {
                    return context.tickCounter().getGameTimeDeltaTicks();
                }

                @Override
                public Camera camera() {
                    return context.camera();
                }

                @Override
                public BlockHitResult target() {
                    var minecraft = Minecraft.getInstance();
                    return Objects.safeCast(minecraft.hitResult, BlockHitResult.class);
                }

                @Override
                public IGraphicsContext context() {
                    return AbstractGraphicsRenderer.wrap(context.matrixStack(), context.consumers());
                }
            });
            return true;
        }));
    }
}
