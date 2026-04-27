package moe.plushie.armourers_workshop.compat.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.init.client.ClientAttachmentHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractParrotOnShoulderLayer {

    private static Player PLAYER;
    private static MultiBufferSource BUFFER_SOURCE;
    private static boolean IS_LEFT;

    public static void push(PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int i, Player player, boolean bl) {
        PLAYER = player;
        BUFFER_SOURCE = bufferSourceIn;
        IS_LEFT = bl;
    }

    public static void apply(PoseStack poseStackIn) {
        if (PLAYER != null && BUFFER_SOURCE != null) {
            ClientAttachmentHandler.onRenderParrot(PLAYER, IS_LEFT, poseStackIn, BUFFER_SOURCE);
        }
    }

    public static void pop() {
        PLAYER = null;
        BUFFER_SOURCE = null;
    }
}

