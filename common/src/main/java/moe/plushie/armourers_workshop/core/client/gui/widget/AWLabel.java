package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class AWLabel extends AbstractWidget {

    private int textColor = 0xffffff;
    private Font font;
    private ArrayList<FormattedText> wrappedTextLines;

    public AWLabel(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.font = Minecraft.getInstance().font;
        this.remake();
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
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
    public void setMessage(Component message) {
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
    public void renderButton(PoseStack matrixStack, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        if (wrappedTextLines == null) {
            remake();
        }
        if (!visible || wrappedTextLines == null) {
            return;
        }
        matrixStack.pushPose();
        Matrix4f mat = matrixStack.last().pose();
        MultiBufferSource.BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        int dx = x, dy = y;
        for (FormattedText line : wrappedTextLines) {
            font.drawInBatch(Language.getInstance().getVisualOrder(line), dx, dy, textColor, false, mat, renderType, false, 0, 15728880);
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
        FormattedText textProperties = wrappedTextLines.get(row);
        return font.getSplitter().componentStyleAtWidth(textProperties, (int) (mouseX - x));
    }

    private void remake() {
        Style style = Style.EMPTY;
        wrappedTextLines = new ArrayList<>(font.getSplitter().splitLines(getMessage(), width, style));
    }
}
