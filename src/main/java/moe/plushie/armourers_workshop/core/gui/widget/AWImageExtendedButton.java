package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AWImageExtendedButton extends AWImageButton {

    public AWImageExtendedButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, IPressable pressable, ITooltip tooltip, ITextComponent title) {
        super(x, y, width, height, u, v, texture, pressable, tooltip, title);
    }

    public AWImageExtendedButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, int textureWidth, int textureHeight, IPressable pressable, ITooltip tooltip, ITextComponent title) {
        super(x, y, width, height, u, v, texture, textureWidth, textureHeight, pressable, tooltip, title);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        int k = getYImage(isHovered());
        RenderUtils.tile(matrixStack, x, y, 0, 46 + k * 20, width, height, 200, 20, 2, 3, 2, 2, WIDGETS_LOCATION);
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected int getYImage(boolean hovered) {
        return super.getYImage(hovered);
    }
}