package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
@Environment(value = EnvType.CLIENT)
public class AWImageButton extends Button {

    private final ResourceLocation texture;
    private final int xTexStart;
    private final int yTexStart;
    private final int textureWidth;
    private final int textureHeight;
    private Component disabledMessage;
    private int iconWidth;
    private int iconHeight;

    private boolean isSelected = false;

    public AWImageButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, Button.OnPress pressable, Button.OnTooltip tooltip, Component title) {
        this(x, y, width, height, u, v, texture, 256, 256, pressable, tooltip, title);
    }

    public AWImageButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, int textureWidth, int textureHeight, Button.OnPress pressable, Button.OnTooltip tooltip, Component title) {
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

    public void setDisabledMessage(Component message) {
        this.disabledMessage = message;
    }

    public void setIconSize(int width, int height) {
        this.iconWidth = width;
        this.iconHeight = height;
    }

    @Override
    public Component getMessage() {
        if (!this.active && this.disabledMessage != null) {
            {
                return this.disabledMessage;
            }
        }
        return super.getMessage();
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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