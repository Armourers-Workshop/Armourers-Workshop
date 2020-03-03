package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import com.google.common.util.concurrent.FutureCallback;

import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskInfo;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskInfo.TaskData;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelInfo extends GuiPanel {

    private static final String URL_DISCORD = "https://discord.gg/5Z3KKvU";
    private static final String URL_GITHUB = "https://github.com/RiskyKen/Armourers-Workshop";
    private static final String URL_REDDIT = "https://www.reddit.com/r/ArmourersWorkshop/";
    private static final String URL_DONATION = "https://ko-fi.com/riskyken";

    private final String guiName;
    
    private GuiCustomLabel statsText;
    private TaskData stats = null;

    private String failMessage = null;

    public GuiGlobalLibraryPanelInfo(GuiScreen parent) {
        super(parent, 0, 0, 1, 1);
        guiName = ((GuiGlobalLibrary)parent).getGuiName() + ".panel.info";
    }

    @Override
    public void initGui() {
        super.initGui();
        statsText = new GuiCustomLabel(fontRenderer, x + 2, y + 2, width - 4, height - 4);
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
    }

    public void updateInfo() {
        stats = null;
        failMessage = null;
        new GlobalTaskInfo().createTaskAndRun(new FutureCallback<GlobalTaskInfo.TaskData>() {

            @Override
            public void onSuccess(TaskData result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        stats = result;
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                failMessage = t.getMessage();
            }
        });
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        statsText.mouseClick(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        if (!this.visible) {
            return;
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();

        statsText.clearText();
        statsText.addLine("Info");
        statsText.addNewLine();

        if (stats != null) {
            statsText.addLine("Total skins uploaded: " + stats.getTotalSkin());
            statsText.addNewLine();
            statsText.addLine(stats.getSqlData()[7]);
            statsText.addNewLine();
            statsText.addText("Downloads last (hour " + stats.getDownloadsLastHour());
            statsText.addText(") (day " + stats.getDownloadsLastDay());
            statsText.addText(") (week: " + stats.getDownloadsLastWeek());
            statsText.addLine(").");
        } else {
            if (failMessage != null) {
                statsText.addLine("Error gettings stats:");
                statsText.addLine(failMessage);
            } else {
                statsText.addLine("Loading...");
                statsText.addNewLine();
                statsText.addNewLine();
                statsText.addNewLine();
                statsText.addNewLine();
            }
        }
        

        statsText.addNewLine();
        statsText.addText("Links");
        statsText.addNewLine();
        statsText.addNewLine();
        
        statsText.addText("Discord: ");
        statsText.addUrl("https://discord.gg/5Z3KKvU");
        statsText.addNewLine();
        statsText.addNewLine();
        
        statsText.addText("GitHub: ");
        statsText.addUrl("https://github.com/RiskyKen/Armourers-Workshop");
        statsText.addNewLine();
        statsText.addNewLine();
        
        statsText.addText("Reddit: ");
        statsText.addUrl("https://www.reddit.com/r/ArmourersWorkshop/");
        statsText.addNewLine();
        statsText.addNewLine();
        
        statsText.addNewLine();
        
        statsText.addText("Thank you for using Armourer's Workshop, if you want to help please consider donating to cover running cost of the global library. ");
        statsText.addUrl(URL_DONATION);
        statsText.addNewLine();
        statsText.addNewLine();
        
        statsText.draw(mouseX, mouseY);
    }

    private class GuiCustomLabel {

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

        public void clearText() {
            this.text = "";
        }

        public void mouseClick(int mouseX, int mouseY, int button) {
            if (button != 0) {
                return;
            }
            String textAtMouse = StringUtils.stripControlCodes(getWordAtPos(mouseX, mouseY));
            if (textAtMouse.startsWith("http")) {
                try {
                    openWebLink(new URI(textAtMouse));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }

        public void draw(int mouseX, int mouseY) {
            //drawRect(x, y, x + width, y + height, 0x88EEEEEE);
            String displayString = text;
            String textAtMouse = getWordAtPos(mouseX, mouseY);

            String replaceCheck = TextFormatting.BLUE.toString() + "http";

            if (textAtMouse.startsWith(replaceCheck)) {
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
}
