package moe.plushie.armourers_workshop.init.mixin.fabric;

import moe.plushie.armourers_workshop.core.client.gui.widget.ContainerMenuScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.SlotListView;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me.shedaniel.rei.RoughlyEnoughItemsCoreClient")
public class FabricScreenREIMixin {

    @Inject(method = "shouldReturn", at = @At("HEAD"), cancellable = true, remap = false)
    private static void aw$shouldReturn(Screen screen, CallbackInfoReturnable<Boolean> cir) {
        if (screen instanceof SlotListView.DelegateScreen) {
            cir.setReturnValue(true);
        }
        if (screen instanceof ContainerMenuScreen) {
            cir.setReturnValue(!((ContainerMenuScreen<?, ?>) screen).shouldDrawPluginScreen());
        }
    }
}
