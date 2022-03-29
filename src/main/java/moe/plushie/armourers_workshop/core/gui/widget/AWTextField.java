package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class AWTextField extends TextFieldWidget {

    private final int lineHeight;
    protected FontRenderer font;

    protected Consumer<String> returnHandler;

    public AWTextField(FontRenderer font, int x, int y, int width, int height, ITextComponent message) {
        super(font, x, y, width, height, message);
        this.font = font;
        this.lineHeight = font.lineHeight;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        super.renderButton(matrixStack, mouseX, mouseY, p_230431_4_);
        if (getValue().isEmpty()) {
            this.font.draw(matrixStack, getMessage(), x + 4, y + (height - lineHeight) / 2f, 0x404040);
        }
    }

    @Override
    public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
        if (super.keyPressed(key, p_231046_2_, p_231046_3_)) {
            return true;
        }
        if (returnHandler != null && key == GLFW.GLFW_KEY_ENTER) {
            returnHandler.accept(getValue());
            return true;
        }
        return false;
    }

    public void setReturnHandler(Consumer<String> returnHandler) {
        this.returnHandler = returnHandler;
    }
}
