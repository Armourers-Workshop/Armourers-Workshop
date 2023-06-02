package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.20, )")
@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "renderEntityInInventoryFollowsMouse", at = @At("HEAD"))
    private static void aw2$renderEntityInInventoryPre(GuiGraphics context, int x, int y, int scale, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityInInventoryPre(entity, x, y, scale, mouseX, y);
    }

    @Inject(method = "renderEntityInInventoryFollowsMouse", at = @At("RETURN"))
    private static void aw2$renderEntityInInventoryPost(GuiGraphics context, int x, int y, int scale, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityInInventoryPost(entity);
    }
}
