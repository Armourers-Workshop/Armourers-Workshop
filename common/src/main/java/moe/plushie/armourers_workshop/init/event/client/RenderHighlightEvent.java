package moe.plushie.armourers_workshop.init.event.client;

import moe.plushie.armourers_workshop.api.client.ICamera;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import net.minecraft.world.phys.BlockHitResult;

public interface RenderHighlightEvent {

    interface Block {

        float partialTick();

        ICamera camera();

        BlockHitResult target();

        IGraphicsContext context();
    }
}
