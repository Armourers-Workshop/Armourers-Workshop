package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.init.client.ClientMenuHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.ftb.mods.ftblibrary.FTBLibraryClient")
public class ForgeScreenFTBMixin {

    // https://github.com/FTBTeam/FTB-Library/blob/1.16/main/common/src/main/java/dev/ftb/mods/ftblibrary/FTBLibraryClient.java#L127
    @Inject(method = "areButtonsVisible", at = @At("HEAD"), cancellable = true, remap = false)
    private static void aw2$areButtonsVisible(Screen screen, CallbackInfoReturnable<Boolean> cir) {
        if (!ClientMenuHandler.shouldRenderExtendScreen(screen)) {
            cir.setReturnValue(false);
        }
    }
}
