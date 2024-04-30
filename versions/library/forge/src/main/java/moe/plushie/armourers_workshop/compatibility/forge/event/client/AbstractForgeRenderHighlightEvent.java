package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderHighlightEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;

@Available("[1.19, )")
public class AbstractForgeRenderHighlightEvent {

    public static IEventHandler<RenderHighlightEvent.Block> blockFactory() {
        return AbstractForgeClientEventsImpl.RENDER_HIGHLIGHT_BLOCK.map(event -> new RenderHighlightEvent.Block() {
            @Override
            public float getPartialTick() {
                return event.getPartialTick();
            }

            @Override
            public Camera getCamera() {
                return event.getCamera();
            }

            @Override
            public PoseStack getPoseStack() {
                return event.getPoseStack();
            }

            @Override
            public MultiBufferSource getMultiBufferSource() {
                return event.getMultiBufferSource();
            }

            @Override
            public BlockHitResult getTarget() {
                return event.getTarget();
            }
        });
    }
}
