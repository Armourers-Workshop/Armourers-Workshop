package moe.plushie.armourers_workshop.compat.mixin.client.gui;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractItemStackRenderState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[20, 26)")
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("HEAD"))
    private void aw2$renderGuiItemPre(LivingEntity livingEntity, Level level, ItemStack itemStack, int i, int j, int k, int l, CallbackInfo ci) {
        AbstractItemStackRenderState.startRenderGuiItem(itemStack);
    }

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("RETURN"))
    private void aw2$renderGuiItemPost(LivingEntity livingEntity, Level level, ItemStack itemStack, int i, int j, int k, int l, CallbackInfo ci) {
        AbstractItemStackRenderState.endRenderGuiItem(itemStack);
    }
}
