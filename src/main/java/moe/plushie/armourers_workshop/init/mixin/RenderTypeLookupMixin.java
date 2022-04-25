package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.client.render.IHasCustomizeRenderType;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTypeLookup.class)
public class RenderTypeLookupMixin {

    @Inject(method = "getRenderType(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/client/renderer/RenderType;", at = @At(value = "HEAD"), cancellable = true)
    private static void hooked_put(ItemStack itemStack, boolean flags, CallbackInfoReturnable<RenderType> callback) {
       if (itemStack.getItem() instanceof BlockItem) {
           Block block = ((BlockItem) itemStack.getItem()).getBlock();
           if (block instanceof IHasCustomizeRenderType) {
               callback.setReturnValue(((IHasCustomizeRenderType) block).getItemRenderType(flags));
           }
       }
    }
}
