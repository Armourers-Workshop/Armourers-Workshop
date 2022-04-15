package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings({"NullableProblems", "unused"})
@OnlyIn(Dist.CLIENT)
public class AWImageButton extends Button {

    private ITextComponent disabledMessage;

    private final ResourceLocation texture;

    private final int xTexStart;
    private final int yTexStart;
    private final int textureWidth;
    private final int textureHeight;

    private int iconWidth;
    private int iconHeight;

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
        this.texture = texture;
        this.iconWidth = width;
        this.iconHeight = height;
    }

    public boolean isEnabled() {
        return active;
    }

    public void setEnabled(boolean isEnabled) {
        this.active = isEnabled;
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

    public void setDisabledMessage(ITextComponent message) {
        this.disabledMessage = message;
    }

    public void setIconSize(int width, int height) {
        this.iconWidth = width;
        this.iconHeight = height;
    }

    @Override
    public ITextComponent getMessage() {
        if (!this.active && this.disabledMessage != null) { {
            return this.disabledMessage;
        }}
        return super.getMessage();
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        int u = xTexStart + iconWidth * getState();
        int dx = (width - iconWidth) / 2;
        int dy = (height - iconHeight) / 2;
        if (texture != null) {
            RenderUtils.blit(matrixStack, x + dx, y + dy, u, yTexStart, iconWidth, iconHeight, textureWidth, textureHeight, texture);
        }
        if (this.isHovered) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    protected int getState() {
        int state = 0;
        if (!active) {
            return 2;
        }
        if (isHovered()) {
            state += 1;
        }
        if (isSelected) {
            state += 2;
        }
        return state;
    }
}