package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.gui.AbstractWrappedScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Available("[1.16, )")
@Mixin(Minecraft.class)
public class ExtendedScreenMixin {

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen aw2$setScreen(Screen screen) {
        if (!AbstractWrappedScreen.shouldRenderExtendScreen(screen)) {
            return new AbstractWrappedScreen(screen);
        }
        return screen;
    }
}
