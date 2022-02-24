package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AWImageButton extends Button {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int textureWidth;
    private final int textureHeight;

    private boolean isSelected = false;

    public AWImageButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, Button.IPressable pressable, Button.ITooltip tooltip, ITextComponent title) {
        this(x, y, width, height, u, v, texture, 256, 256, pressable, tooltip, title);
    }

    public AWImageButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, int textureWidth, int textureHeight, Button.IPressable pressable, Button.ITooltip tooltip, ITextComponent title) {
        super(x, y, width, height, title, pressable, tooltip);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = u;
        this.yTexStart = v;
        this.resourceLocation = texture;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean getSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        RenderUtils.bind(resourceLocation);
        int u = xTexStart + width * getState();
        int v = yTexStart;
//        RenderSystem.enableDepthTest();
        blit(matrixStack, x, y, u, v, width, height, textureWidth, textureHeight);
        if (this.isHovered()) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    private int getState() {
        int state = 0;
        if (this.isHovered()) {
            state += 1;
        }
        if (isSelected) {
            state += 2;
        }
        return state;
    }
}