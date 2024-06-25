package moe.plushie.armourers_workshop.init.platform.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;

public interface RenderSpecificHandEvent {

    /**
     * {@return the arm being rendered}
     */
    InteractionHand getHand();

    /**
     * {@return the pose stack used for rendering}
     */
    PoseStack getPoseStack();

    /**
     * {@return the source of rendering buffers}
     */
    MultiBufferSource getMultiBufferSource();

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    int getPackedLight();

    /**
     * {@return the client player that is having their arm rendered} In general, this will be the same as
     * {@link net.minecraft.client.Minecraft#player}.
     */
    AbstractClientPlayer getPlayer();

    void setCancelled(boolean isCancelled);
}
