package moe.plushie.armourers_workshop.client.gui.controls;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomLabel extends Gui {

    private final FontRenderer fontRenderer;
    private int colour = 0xFFFFFFFF;
    private int x;
    private int y;
    private int width;
    private int height;

    private String text = "";

    public GuiCustomLabel(FontRenderer fontRenderer, int x, int y, int width, int height) {
        this.fontRenderer = fontRenderer;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }
    
    public void setColour(int colour) {
        this.colour = colour;
    }

    public void addLine(String text) {
        this.text += text + "\n";
    }

    public void addText(String text) {
        this.text += text;
    }

    public void addUrl(String url) {
        this.text += TextFormatting.BLUE.toString() + url + TextFormatting.RESET.toString();
    }

    public void addNewLine() {
        this.text += "\n";
    }
    
    public void addNewLines(int count) {
        for (int i = 0; i < count; i++) {
            this.text += "\n";
        }
    }
    
    public void clearText() {
        this.text = "";
    }

    public boolean mouseClick(int mouseX, int mouseY, int button) {
        if (isInside(mouseX, mouseY)) {
            if (button != 0) {
                return true;
            }
            String textAtMouse = StringUtils.stripControlCodes(getWordAtPos(mouseX, mouseY));
            if (textAtMouse.startsWith("http")) {
                try {
                    openWebLink(new URI(textAtMouse));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    public void draw(int mouseX, int mouseY) {
        // drawRect(x, y, x + width, y + height, 0x44EEEEEE);
        String displayString = text;
        String textAtMouse = getWordAtPos(mouseX, mouseY);

        String replaceCheck = "http";

        if (StringUtils.stripControlCodes(textAtMouse).startsWith(replaceCheck)) {
            String url = StringUtils.stripControlCodes(textAtMouse);
            displayString = displayString.replace(TextFormatting.BLUE.toString() + url, TextFormatting.BLUE.toString() + TextFormatting.UNDERLINE.toString() + url);
        }

        List<String> lines = fontRenderer.listFormattedStringToWidth(displayString, width);
        for (int i = 0; i < lines.size(); i++) {
            fontRenderer.drawString(lines.get(i), x, y + i * fontRenderer.FONT_HEIGHT, colour);
        }
    }

    public String getWordAtPos(int posX, int posY) {
        String line = getLineAtPos(posX, posY);
        if (line.isEmpty()) {
            return "";
        }
        String[] words = line.split(Pattern.quote(" "));

        String text = "";
        for (int i = 0; i < words.length; i++) {
            text += words[i];
            int textWidth = fontRenderer.getStringWidth(text);
            if (posX >= x & posX < x + textWidth) {
                return words[i];
            }
            text += " ";
        }
        return "";
    }

    public String getLineAtPos(int posX, int posY) {
        int line = getLineIndexAtPos(posX, posY);
        if (line >= 0) {
            List<String> lines = fontRenderer.listFormattedStringToWidth(text, width);
            if (line < lines.size()) {
                return lines.get(line);
            }
        }
        return "";
    }

    public int getLineIndexAtPos(int posX, int posY) {
        if (isInside(posX, posY)) {
            int line = 0;
            if (posY != 0) {
                line = MathHelper.floor((float) (posY - y) / (float) fontRenderer.FONT_HEIGHT);
            }
            return line;
        }
        return -1;
    }

    public boolean isInside(int posX, int posY) {
        return posX >= this.x & posY >= this.y & posX < this.x + this.width & posY < this.y + this.height;
    }

    private void openWebLink(URI url) {
        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop").invoke((Object) null);
            oclass.getMethod("browse", URI.class).invoke(object, url);
        } catch (Throwable throwable1) {
            Throwable throwable = throwable1.getCause();
            ModLogger.log(Level.ERROR, String.format("Couldn't open link: {}", (Object) (throwable == null ? "<UNKNOWN>" : throwable.getMessage())));
        }
    }
}
