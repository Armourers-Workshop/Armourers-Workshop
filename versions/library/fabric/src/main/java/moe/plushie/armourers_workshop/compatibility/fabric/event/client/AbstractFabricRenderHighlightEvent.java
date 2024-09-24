package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderHighlightEvent;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;

@Available("[1.21, )")
public class AbstractFabricRenderHighlightEvent {

    public static IEventHandler<RenderHighlightEvent.Block> blockFactory() {
        return (priority, receiveCancelled, subscriber) -> WorldRenderEvents.BLOCK_OUTLINE.register(((context, outlineContext) -> {
            subscriber.accept(new RenderHighlightEvent.Block() {
                @Override
                public float getPartialTick() {
                    return context.tickCounter().getGameTimeDeltaTicks();
                }

                @Override
                public Camera getCamera() {
                    return context.camera();
                }

                @Override
                public PoseStack getPoseStack() {
                    return context.matrixStack();
                }

                @Override
                public MultiBufferSource getMultiBufferSource() {
                    return context.consumers();
                }

                @Override
                public BlockHitResult getTarget() {
                    Minecraft minecraft = Minecraft.getInstance();
                    return ObjectUtils.safeCast(minecraft.hitResult, BlockHitResult.class);
                }
            });
            return true;
        }));
    }
}
