package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "renderEntityInInventory", at = @At("HEAD"))
    private static void hooked_renderEntityInInventory_begin(int x, int y, int scale, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityInInventoryPre(entity, x, y, scale, mouseX, y);
    }

    @Inject(method = "renderEntityInInventory", at = @At("RETURN"))
    private static void hooked_renderEntityInInventory_end(int x, int y, int scale, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityInInventoryPost(entity);
    }
}