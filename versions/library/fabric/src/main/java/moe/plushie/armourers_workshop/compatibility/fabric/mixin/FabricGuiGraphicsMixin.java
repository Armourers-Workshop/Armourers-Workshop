package moe.plushie.armourers_workshop.compatibility.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderTooltipEvents;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SkinResourceLocation;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Available("[1.20, )")
@Mixin(GuiGraphics.class)
public class FabricGuiGraphicsMixin {

    @Inject(method = "renderTooltipInternal", at = @At("RETURN"))
    private void aw$renderTooltipInternal(Font font, List<ClientTooltipComponent> tooltips, int mouseX, int mouseY, ClientTooltipPositioner positioner, CallbackInfo ci) {
        if (tooltips.isEmpty()) {
            return;
        }
        ItemStack itemStack = RenderTooltipEvents.TOOLTIP_ITEM_STACK;
        if (itemStack.isEmpty()) {
            return;
        }
        GuiGraphics graphics = ObjectUtils.unsafeCast(this);
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();;
        int i = 0;
        int j = tooltips.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent tooltip : tooltips) {
            int k = tooltip.getWidth(font);
            if (k > i) {
                i = k;
            }
            j += tooltip.getHeight();
        }
        int j2 = mouseX + 12;
        int k2 = mouseY - 12;
        if (j2 + i > screenWidth) {
            j2 -= 28 + i;
        }
        if (k2 + j + 6 > screenHeight) {
            k2 = screenHeight - j - 6;
        }
        RenderTooltipEvents.BEFORE.invoker().onRenderTooltip(graphics.pose(), itemStack, j2, k2, i, j, mouseX, mouseY, screenWidth, screenHeight);
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void aw$renderTooltipPre(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        RenderTooltipEvents.TOOLTIP_ITEM_STACK = itemStack;
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
    private void aw$renderTooltipPost(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        RenderTooltipEvents.TOOLTIP_ITEM_STACK = ItemStack.EMPTY;
    }
}
