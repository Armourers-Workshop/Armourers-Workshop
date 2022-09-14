package moe.plushie.armourers_workshop.compatibility.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderTooltipCallback;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public class AbstractFabricScreenMixin {

    private ItemStack aw$tooltipStack = ItemStack.EMPTY;

    @Shadow protected Font font;

    @Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "HEAD"))
    public void hooked_renderTooltipPre(PoseStack poseStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        aw$tooltipStack = itemStack;
    }

    @Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "RETURN"))
    public void hooked_renderTooltipPost(PoseStack poseStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
        aw$tooltipStack = ItemStack.EMPTY;
    }

    @Inject(method = "renderTooltipInternal", at = @At(value = "HEAD"))
    public void hooked_renderTooltip(PoseStack poseStack, List<ClientTooltipComponent> tooltips, int mouseX, int mouseY, CallbackInfo ci) {
        if (tooltips.isEmpty()) {
            return;
        }
        Screen screen = ObjectUtils.unsafeCast(this);
        int screenWidth = screen.width;
        int screenHeight = screen.height;
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
        RenderTooltipCallback.EVENT.invoker().onRenderTooltip(poseStack, aw$tooltipStack, j2, k2, i, j, mouseX, mouseY, screenWidth, screenHeight);
    }
}
