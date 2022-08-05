package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

@Environment(value = EnvType.CLIENT)
public class AWCheckBox extends Button {

    private final int iconWidth;
    private final int iconHeight;

    private final int textWidth;
    private final int textHeight;

    private int textColour = 0x404040;
    private final Font font;
    private boolean isSelected;

    public AWCheckBox(int x, int y, int iconWidth, int iconHeight, Component title, boolean isSelected) {
        this(x, y, iconWidth, iconHeight, title, isSelected, null);
    }

    public AWCheckBox(int x, int y, int iconWidth, int iconHeight, Component title, boolean isSelected, Button.OnPress changeHandler) {
        super(x, y, iconWidth, iconHeight, title, changeHandler, NO_TOOLTIP);
        this.isSelected = isSelected;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.font = Minecraft.getInstance().font;
        this.textHeight = font.lineHeight;
        this.textWidth = font.width(title.getVisualOrderText());
        this.width = iconWidth + 2 + textWidth;
        this.height = Math.max(iconHeight, textHeight);
    }

    @Override
    public void setWidth(int width) {
        // can't change width
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isEnabled() {
        return active;
    }

    public void setEnabled(boolean enabled) {
        active = enabled;
    }

    public void setTextColour(int textColour) {
        this.textColour = textColour;
    }

    @Override
    public void onPress() {
        isSelected = !isSelected;
        if (onPress != null) {
            onPress.onPress(this);
        }
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        RenderUtils.bind(RenderUtils.TEX_WIDGETS);
        RenderUtils.drawContinuousTexturedBox(matrixStack, x, y, 0, 46, iconWidth, iconHeight, 200, 20, 2, 3, 2, 2, 0);
        if (this.isSelected()) {
            drawCenteredString(matrixStack, font, "x", x + iconWidth / 2 + 1, y, 0xffcccccc);
        }
        int color = this.textColour;
        if (!active) {
            color = 0xffa0a0a0;
        }
        font.draw(matrixStack, getMessage(), x + iconWidth + 2, y + 1, color);
        RenderSystem.enableAlphaTest();
    }
}
