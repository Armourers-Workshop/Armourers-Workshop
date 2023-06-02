package moe.plushie.armourers_workshop.compatibility.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderTooltipEvents;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.20, )")
@Mixin(AbstractContainerScreen.class)
public abstract class FabricScreenMixin {

    @Shadow @Nullable protected Slot hoveredSlot;

    @Inject(method = "renderTooltip", at = @At("HEAD"))
    private void aw$renderTooltipPre(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        if (hoveredSlot != null && hoveredSlot.hasItem()) {
            RenderTooltipEvents.TOOLTIP_ITEM_STACK = hoveredSlot.getItem();
        }
    }

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void aw$renderTooltipPost(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        RenderTooltipEvents.TOOLTIP_ITEM_STACK = ItemStack.EMPTY;
    }
}
