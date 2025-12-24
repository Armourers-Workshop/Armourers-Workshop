package moe.plushie.armourers_workshop.compat.mixin.patch.attachment;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.renderer.layer.AbstractParrotOnShoulderLayer;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, 1.26)")
public class ParrotOnShoulderLayerMixin {

    @Mixin(ParrotOnShoulderLayer.class)
    public static class LayerPatch {

        // we can't inject `ifPresent` by the `lambda$render$1` directly, because optifine will patch it, it will cause our mixin to fail.

        @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFZ)V", at = @At("HEAD"))
        private void aw2$renderOnShoulderPre(PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int i, Player player, float f, float g, float h, float j, boolean bl, CallbackInfo ci) {
            AbstractParrotOnShoulderLayer.push(poseStackIn, bufferSourceIn, i, player, bl);
        }

        @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFZ)V", at = @At("TAIL"))
        private void aw2$renderOnShoulderPost(PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int i, Player player, float f, float g, float h, float j, boolean bl, CallbackInfo ci) {
            AbstractParrotOnShoulderLayer.pop();
        }
    }

    @Mixin(ParrotModel.class)
    public static class ModelPatch {

        @Inject(method = "renderOnShoulder", at = @At("HEAD"))
        private void aw2$renderOnShoulder(PoseStack poseStackIn, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k, int l, CallbackInfo ci) {
            AbstractParrotOnShoulderLayer.apply(poseStackIn);
        }
    }
}
