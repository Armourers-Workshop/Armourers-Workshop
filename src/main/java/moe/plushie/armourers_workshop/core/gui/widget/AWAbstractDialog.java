package moe.plushie.armourers_workshop.core.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class AWAbstractDialog extends Screen {

    protected int leftPos;
    protected int topPos;

    protected int titleLabelX;
    protected int titleLabelY;

    protected int imageWidth = 240;
    protected int imageHeight = 120;

    protected Consumer<AWAbstractDialog> completeHandler;

    protected AWAbstractDialog(ITextComponent title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.titleLabelX = this.imageWidth / 2 - this.font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = 8;
    }

    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), leftPos + titleLabelX, topPos + titleLabelY, 0x404040);
    }

    @Override
    public void renderBackground(MatrixStack matrixStack) {
        super.renderBackground(matrixStack);
        RenderUtils.tile(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 128, 128, 4, 4, 4, 4, RenderUtils.TEX_COMMON);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        this.renderBackground(matrixStack);
        this.renderLabels(matrixStack, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
    }

    @Override
    public void removed() {
        super.removed();
        this.children.clear();
        this.buttons.clear();
        this.completeHandler = null;
    }

    @Override
    public void onClose() {
        if (this.completeHandler != null) {
            this.completeHandler.accept(this);
            this.completeHandler = null;
        }
        this.removed();
    }

    public void whenOnClose(Consumer<AWAbstractDialog> completeHandler) {
        this.completeHandler = completeHandler;
    }

    @Override
    protected <T extends IGuiEventListener> T addWidget(T widget) {
        T value = super.addWidget(widget);
        if (isFirstResponder(value)) {
            for (int i = 0; i < children.size(); ++i) {
                if (!isFirstResponder(children.get(i))) {
                    children.remove(widget);
                    children.add(i, widget);
                    break;
                }
            }
        }
        return value;
    }

    protected boolean isFirstResponder(IGuiEventListener listener) {
        return listener instanceof AWComboBox;
    }
}
