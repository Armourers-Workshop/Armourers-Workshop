package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

@Environment(value = EnvType.CLIENT)
public class AWTabPanel extends Screen {

    private final String baseKey;
    public int leftPos = 0;
    public int topPos = 0;
    protected Button lastHoveredButton;

    protected AWTabPanel(String baseKey) {
        super(TranslateUtils.title(baseKey));
        this.baseKey = baseKey;
    }

    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        if (this.lastHoveredButton != null) {
            this.renderTooltip(matrixStack, lastHoveredButton.getMessage(), mouseX, mouseY);
            this.lastHoveredButton = null;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (forwardToFocused(f -> f.mouseClicked(mouseX, mouseY, button))) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double p_231043_5_) {
        if (forwardToFocused(f -> f.mouseScrolled(mouseX, mouseY, p_231043_5_))) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, p_231043_5_);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_231045_6_, double p_231045_8_) {
        if (forwardToFocused(f -> f.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_))) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean results = forwardToFocused(f -> f.mouseReleased(mouseX, mouseY, button));
        super.mouseReleased(mouseX, mouseY, button);
        return results;
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        if (forwardToFocused(s -> s.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_))) {
            return true;
        }
        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    public boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int k) {
        return mouseX < (double) left || mouseY < (double) top || mouseX >= (double) (left + width) || mouseY >= (double) (top + height);
    }

    protected void addHoveredButton(Button button, PoseStack matrixStack, int mouseX, int mouseY) {
        this.lastHoveredButton = button;
    }

    protected AWLabel addLabel(int x, int y, int width, int height, Component message) {
        AWLabel label = new AWLabel(x, y, width, height, message);
        label.setTextColor(4210752);
        label.active = false;
        addButton(label);
        return label;
    }

    private boolean forwardToFocused(Predicate<GuiEventListener> consumer) {
        GuiEventListener focused = getFocused();
        if (focused instanceof AbstractWidget) {
            return ((AbstractWidget) focused).visible && consumer.test(focused);
        }
        if (focused != null) {
            return consumer.test(focused);
        }
        return false;
    }

    protected Component getCommonDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.common" + "." + key);
    }

    protected Component getDisplayText(String key) {
        return TranslateUtils.title(baseKey + "." + key);
    }

    protected Component getDisplayText(String key, Object... objects) {
        return TranslateUtils.title(baseKey + "." + key, objects);
    }
}
