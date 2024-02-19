package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, )")
@Mixin(GuiGraphics.class)
public class InventoryScreenMixin {

    @Inject(method = "enableScissor", at = @At(value = "HEAD"), cancellable = true)
    private void aw2$enableScissor(int i, int j, int k, int l, CallbackInfo ci) {
        if (RenderSystem.getExtendedScissorFlags() != 0) {
            ci.cancel();
        }
    }

    @Inject(method = "disableScissor", at = @At(value = "HEAD"), cancellable = true)
    private void aw2$disableScissor(CallbackInfo ci) {
        if (RenderSystem.getExtendedScissorFlags() != 0) {
            ci.cancel();
        }
    }
}
