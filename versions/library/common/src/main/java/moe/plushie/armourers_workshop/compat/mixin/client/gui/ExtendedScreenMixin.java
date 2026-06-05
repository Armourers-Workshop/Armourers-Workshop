package moe.plushie.armourers_workshop.compat.mixin.client.gui;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, )")
@Mixin(Screen.class)
public class ExtendedScreenMixin {

//    @Shadow
//    @Final
//    private List<Renderable> renderables;
//
//    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("RETURN"))
//    private void aw2$init(Minecraft minecraft, int i, int j, CallbackInfo ci) {
//        var screen = Screen.class.cast(this);
//        if (!AbstractWrappedScreen.shouldRenderExtendScreen(screen)) {
//            renderables.clear();
//        }
//    }
}
