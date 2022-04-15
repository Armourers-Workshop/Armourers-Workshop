package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class AWLabel extends Widget {

    private int textColor = 0xffffff;
    private FontRenderer font;
    private ArrayList<ITextProperties> wrappedTextLines;

    public AWLabel(int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width, height, message);
        this.font = Minecraft.getInstance().font;
        this.remake();
    }

    public FontRenderer getFont() {
        return font;
    }

    public void setFont(FontRenderer font) {
        this.font = font;
        this.wrappedTextLines = null;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.wrappedTextLines = null;
    }

    @Override
    public void setMessage(ITextComponent message) {
        super.setMessage(message);
        this.wrappedTextLines = null;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.wrappedTextLines = null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
        Style style = getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null && style.getClickEvent() != null) {
            if (style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                String value = style.getClickEvent().getValue();
                try {
                    URI uri = new URI(value);
                    String s = uri.getScheme();
                    if (s == null) {
                        throw new URISyntaxException(value, "Missing protocol");
                    }
                    Util.getPlatform().openUri(uri);
                    return true;
                } catch (URISyntaxException urisyntaxexception) {
                    ModLog.error("Can't open url for {}", value, urisyntaxexception);
                }
            }
        }
        return false;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        if (wrappedTextLines == null) {
            remake();
        }
        if (!visible || wrappedTextLines == null) {
            return;
        }
        matrixStack.pushPose();
        Matrix4f mat = matrixStack.last().pose();
        IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        int dx = x, dy = y;
        for (ITextProperties line : wrappedTextLines) {
            font.drawInBatch(LanguageMap.getInstance().getVisualOrder(line), dx, dy, textColor, false, mat, renderType, false, 0, 15728880);
            dy += 9;
        }
        renderType.endBatch();
        matrixStack.popPose();

        // drawing text causes the Alpha test to reset
        RenderSystem.enableAlphaTest();
    }

    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        return false;
    }

    @Nullable
    public Style getClickedComponentStyleAt(double mouseX, double mouseY) {
        if (font == null || wrappedTextLines == null) {
            return null;
        }
        int row = (int) ((mouseY - y) / 9);
        if (row < 0 || row >= wrappedTextLines.size()) {
            return null;
        }
        ITextProperties textProperties = wrappedTextLines.get(row);
        return font.getSplitter().componentStyleAtWidth(textProperties, (int) (mouseX - x));
    }

    private void remake() {
        Style style = Style.EMPTY;
        wrappedTextLines = new ArrayList<>(font.getSplitter().splitLines(getMessage(), width, style));
    }
}
