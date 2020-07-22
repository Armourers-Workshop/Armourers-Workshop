package moe.plushie.armourers_workshop.client.gui.controls;

import org.lwjgl.input.Keyboard;

import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTextFieldCustom extends GuiButtonExt {
    private int cursorCounter;
    private boolean isFocused;
    private String text = "";
    private float scrollAmount;
    private String emptyLabel = "";
    private int maxStringLength = 100;

    public GuiTextFieldCustom(int xPos, int yPos, int width, int height) {
        super(-1, xPos, yPos, width, height, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (!visible) {
            return;
        }
        drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
        drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);

        if (this.getText().trim().isEmpty()) {
            String s = mc.fontRenderer.trimStringToWidth(this.emptyLabel, width);
            if (s.length() > 0 & !isFocused()) {
                mc.fontRenderer.drawStringWithShadow(emptyLabel, x + 4, y + 3, 0xFF7F7F7F);
            }
        }

        String s = text;
        if (isFocused()) {
            if (this.cursorCounter / 6 % 2 == 0) {
                s += "_";
            }
        }

        ModRenderHelper.enableScissor(x + 2, y + 2, width - 4, height - 4, true);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -scrollAmount, 0F);
        mc.fontRenderer.drawSplitString(s, x + 5, y + 4, width - 8, 0x444444);
        mc.fontRenderer.drawSplitString(s, x + 4, y + 3, width - 8, 0xFFFFFF);
        GlStateManager.popMatrix();
        ModRenderHelper.disableScissor();
    }

    public boolean isFocused() {
        return isFocused;
    }

    public boolean keyTyped(char c, int keycode) {
        if (!visible | !isFocused) {
            return false;
        }
        switch (keycode) {
        case Keyboard.KEY_BACK:
            removeText(1);
            break;
        case Keyboard.KEY_RETURN:
            text += "\n";
            break;
        case Keyboard.KEY_A:
            if (GuiScreen.isCtrlKeyDown()) {
                break;
            }
        case Keyboard.KEY_X:
            if (GuiScreen.isCtrlKeyDown()) {
                break;
            }
        case Keyboard.KEY_C:
            if (GuiScreen.isCtrlKeyDown()) {
                break;
            }
        case Keyboard.KEY_V:
            if (GuiScreen.isCtrlKeyDown()) {
                addText(GuiScreen.getClipboardString());
                break;
            }
        default:
            if (!GuiScreen.isCtrlKeyDown()) {
                addText(Character.toString(c));
            }
            break;
        }

        return true;
    }

    private void removeText(int count) {
        if (!text.isEmpty()) {
            if (count > text.length()) {
                clearText();
            } else {
                text = text.substring(0, text.length() - count);
            }
        }
    }

    private void addText(String s) {
        s = s.replace("\n", "%n");
        s = ChatAllowedCharacters.filterAllowedCharacters(s);
        s = s.replace("%n", "\n");
        if (!s.isEmpty()) {
            text += s;
        }
        if (text.length() > maxStringLength) {
            text = text.substring(0, maxStringLength);
        }
    }

    private void clearText() {
        text = "";
    }

    private String trimStringNewline(String text) {
        while (text != null && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    public int getTextLineCount(FontRenderer fontRenderer) {
        return fontRenderer.listFormattedStringToWidth(trimStringNewline(text + " "), width - 4).size();
    }

    public int getTextHeight(FontRenderer fontRenderer) {
        return getTextLineCount(fontRenderer) * fontRenderer.FONT_HEIGHT;
    }

    public int getScrollHeight(FontRenderer fontRenderer) {
        return Math.max(0, getTextHeight(fontRenderer) - height - 4 + fontRenderer.FONT_HEIGHT);
    }

    public float getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(float amount) {
        this.scrollAmount = amount;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        boolean clicked = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
        this.isFocused = clicked;
        return clicked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text.length() > maxStringLength) {
            this.text = text.substring(0, maxStringLength);
        } else {
            this.text = text;
        }
    }

    public void setEmptyLabel(String emptyLabel) {
        this.emptyLabel = emptyLabel;
    }

    public void setMaxStringLength(int maxStringLength) {
        this.maxStringLength = maxStringLength;
    }

    public void updateCursorCounter() {
        this.cursorCounter++;
    }
}
