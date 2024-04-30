package moe.plushie.armourers_workshop.init.platform.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;

public interface RenderHighlightEvent {

    interface Block {

        float getPartialTick();

        Camera getCamera();

        PoseStack getPoseStack();

        MultiBufferSource getMultiBufferSource();

        BlockHitResult getTarget();
    }
}
