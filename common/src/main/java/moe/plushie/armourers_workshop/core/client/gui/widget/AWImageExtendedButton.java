package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(value = EnvType.CLIENT)
public class AWImageExtendedButton extends AWImageButton {

    public AWImageExtendedButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, Button.OnPress pressable, Button.OnTooltip tooltip, Component title) {
        super(x, y, width, height, u, v, texture, pressable, tooltip, title);
    }

    public AWImageExtendedButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, int textureWidth, int textureHeight, Button.OnPress pressable, Button.OnTooltip tooltip, Component title) {
        super(x, y, width, height, u, v, texture, textureWidth, textureHeight, pressable, tooltip, title);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        int k = getYImage(isHovered());
        RenderUtils.tile(matrixStack, x, y, 0, 46 + k * 20, width, height, 200, 20, 2, 3, 2, 2, RenderUtils.TEX_WIDGETS);
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
    }
}