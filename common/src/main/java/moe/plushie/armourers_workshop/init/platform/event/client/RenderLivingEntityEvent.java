package moe.plushie.armourers_workshop.init.platform.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

public interface RenderLivingEntityEvent {

    float getPartialTicks();

    int getPackedLight();

    LivingEntity getEntity();

    LivingEntityRenderer<?, ?> getRenderer();

    PoseStack getPoseStack();

    MultiBufferSource getMultiBufferSource();

    interface Pre extends RenderLivingEntityEvent {

    }

    interface Setup extends RenderLivingEntityEvent {

    }

    interface Post extends RenderLivingEntityEvent {

    }
}
