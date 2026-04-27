package moe.plushie.armourers_workshop.compat.fabric.mixin.backpack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Conditional;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[19, 26)")
@Conditional("backpacked")
@Pseudo
@Mixin(BackpackLayer.class)
public class FabricMrcrayfishBackpackRendererMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFFF)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void aw2$render(PoseStack pose, MultiBufferSource source, int light, Player player, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_, CallbackInfo ci) {
        var renderData = EntityRenderData.of(player);
        if (renderData != null && renderData.overriddenManager().contains(SkinProperty.OVERRIDE_MODEL_BACKPACK)) {
            ci.cancel();
        }
    }
}
