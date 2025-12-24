package moe.plushie.armourers_workshop.init.event.client;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.BlockHitResult;

public interface RenderHighlightEvent {

    interface Block {

        float partialTick();

        Camera camera();

        BlockHitResult target();

        IGraphicsContext context();
    }
}
