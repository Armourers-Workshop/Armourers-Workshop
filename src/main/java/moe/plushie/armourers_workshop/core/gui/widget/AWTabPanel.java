package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class AWTabPanel extends Screen {

    protected Button lastHoveredButton;

    public int leftPos = 0;
    public int topPos = 0;

    private final String baseKey;

    protected AWTabPanel(String baseKey) {
        super(TranslateUtils.title(baseKey));
        this.baseKey = baseKey;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        for (IGuiEventListener listener : children) {
            if (listener instanceof Widget) {
                Widget widget = (Widget)listener;
                if (widget.visible) {
                    widget.render(matrixStack, mouseX, mouseY, p_230430_4_);
                }
            }
        }
    }

    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
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

    protected void addHoveredButton(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
        this.lastHoveredButton = button;
    }

    protected AWLabel addLabel(int x, int y, int width, int height, ITextComponent message) {
        AWLabel label = new AWLabel(x, y, width, height, message);
        label.setTextColor(4210752);
        label.active = false;
        addWidget(label);
        return label;
    }

    private boolean forwardToFocused(Predicate<IGuiEventListener> consumer) {
        IGuiEventListener focused = getFocused();
        if (focused instanceof Widget) {
            return ((Widget) focused).visible && consumer.test(focused);
        }
        if (focused != null) {
            return consumer.test(focused);
        }
        return false;
    }

    protected ITextComponent getCommonDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.common" + "." + key);
    }

    public ITextComponent getDisplayText(String... parts) {
        StringBuilder key1 = new StringBuilder(baseKey);
        for (String part : parts) {
            key1.append(".").append(part);
        }
        return TranslateUtils.title(key1.toString());
    }
}
