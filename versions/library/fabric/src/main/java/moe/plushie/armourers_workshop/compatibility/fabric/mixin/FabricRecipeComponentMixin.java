package moe.plushie.armourers_workshop.compatibility.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderTooltipEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Available("[1.20, )")
@Mixin(RecipeBookComponent.class)
public abstract class FabricRecipeComponentMixin {

    @Inject(method = "renderGhostRecipeTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void aw$renderTooltipPre(GuiGraphics guiGraphics, int i, int j, int k, int l, CallbackInfo ci, ItemStack itemStack) {
        RenderTooltipEvents.TOOLTIP_ITEM_STACK = itemStack;
    }

    @Inject(method = "renderGhostRecipeTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V", shift = At.Shift.AFTER))
    private void aw$renderTooltipPost(GuiGraphics guiGraphics, int i, int j, int k, int l, CallbackInfo ci) {
        RenderTooltipEvents.TOOLTIP_ITEM_STACK = ItemStack.EMPTY;
    }
}
