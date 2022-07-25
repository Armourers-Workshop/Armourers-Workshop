package moe.plushie.armourers_workshop.init.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.init.event.RenderTooltipCallback;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Screen.class)
public class FabricScreenMixin {

    @Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void hooked_renderTooltip(PoseStack poseStack, List<? extends FormattedCharSequence> list, int i, int j, CallbackInfo ci, int w, int x, int y, int w2, int h) {
        RenderTooltipCallback.EVENT.invoker().onRenderTooltip(poseStack, list, i, j, w, x, y, w2, h);
    }
}
