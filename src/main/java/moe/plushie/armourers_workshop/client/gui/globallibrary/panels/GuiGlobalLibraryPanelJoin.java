package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.awt.Rectangle;

import com.google.common.util.concurrent.FutureCallback;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomLabel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.lib.LibURLs;
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin.BetaJoinResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelJoin extends GuiPanel {

    private static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.CONTROL_BUTTONS);
    private static final int MAX_PAGES = 5;

    private final String guiName;

    private Rectangle rectangleRules;
    private GuiIconButton buttonPrevious;
    private GuiIconButton buttonNext;
    private GuiButtonExt buttonJoin;

    private GuiCustomLabel statsText;

    private boolean joining = false;
    private String joinFailMessage = null;
    private int messagePageIndex = 0;

    public GuiGlobalLibraryPanelJoin(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        guiName = ((GuiGlobalLibrary) parent).getGuiName() + ".join";
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        int recWidth = 318;
        int recHeight = 180;
        rectangleRules = new Rectangle(x + width / 2 - recWidth / 2, y + height / 2 - recHeight / 2, recWidth, recHeight);

        buttonPrevious = new GuiIconButton(parent, 0, rectangleRules.x + 5, rectangleRules.y + recHeight - 16 - 5, 16, 16, I18n.format(LibGuiResources.Controls.BUTTON_PREVIOUS), TEXTURE_BUTTONS);
        buttonPrevious.setIconLocation(208, 80, 16, 16);
        buttonPrevious.setDrawButtonBackground(false);

        buttonNext = new GuiIconButton(parent, 0, rectangleRules.x + recWidth - 16 - 5, rectangleRules.y + recHeight - 16 - 5, 16, 16, I18n.format(LibGuiResources.Controls.BUTTON_NEXT), TEXTURE_BUTTONS);
        buttonNext.setIconLocation(208, 96, 16, 16);
        buttonNext.setDrawButtonBackground(false);

        buttonJoin = new GuiButtonExt(0, rectangleRules.x + recWidth / 2 - 140 / 2, rectangleRules.y + recHeight - 16 - 5, 140, 16, GuiHelper.getLocalizedControlName(guiName, "button.join"));
        buttonJoin.visible = false;

        statsText = new GuiCustomLabel(fontRenderer, rectangleRules.x + 5, rectangleRules.y + 5, recWidth - 10, recHeight - 75 - 5);
        statsText.setColour(0x333333);

        buttonList.add(buttonPrevious);
        buttonList.add(buttonNext);
        buttonList.add(buttonJoin);
    }

    @Override
    public void update() {
        buttonPrevious.enabled = messagePageIndex > 0;
        buttonNext.enabled = messagePageIndex < MAX_PAGES - 1;
        buttonJoin.visible = messagePageIndex == MAX_PAGES - 1;
    }

    private void joinedFailed(String reason) {
        buttonJoin.enabled = true;
        joining = false;
        joinFailMessage = reason;
    }

    private void joinedBeta() {
        buttonJoin.enabled = true;
        joining = false;
        joinFailMessage = "";
        ((GuiGlobalLibrary) parent).switchScreen(Screen.HOME);
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled) {
            return false;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled) {
            return false;
        }
        statsText.mouseClick(mouseX, mouseY, button);
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);
        return clicked;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonPrevious) {
            messagePageIndex--;
            messagePageIndex = MathHelper.clamp(messagePageIndex, 0, MAX_PAGES - 1);
        }
        if (button == buttonNext) {
            messagePageIndex++;
            messagePageIndex = MathHelper.clamp(messagePageIndex, 0, MAX_PAGES - 1);
        }
        if (button == buttonJoin) {
            join();
        }
    }

    private void join() {
        buttonJoin.enabled = false;
        joinFailMessage = "";
        new GlobalTaskBetaJoin().createTaskAndRun(new FutureCallback<GlobalTaskBetaJoin.BetaJoinResult>() {

            @Override
            public void onSuccess(BetaJoinResult result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        switch (result.getJoinResult()) {
                        case JOINED:
                            joinedBeta();
                            break;
                        case ALREADY_JOINED:
                            joinedFailed(result.getMessage());
                            break;
                        case MINECRAFT_AUTH_FAIL:
                            joinedFailed(result.getMessage());
                            break;
                        case JOIN_FAILED:
                            joinedFailed(result.getMessage());
                            break;
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        joinedFailed(t.toString());
                    }
                });
            }
        });
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);

        int textureWidth = 128;
        int textureHeight = 128;
        int borderSize = 4;
        mc.renderEngine.bindTexture(new ResourceLocation(LibGuiResources.COMMON));
        GuiUtils.drawContinuousTexturedBox(rectangleRules.x, rectangleRules.y, 0, 0, rectangleRules.width, rectangleRules.height, textureWidth, textureHeight, borderSize, zLevel);

        super.draw(mouseX, mouseY, partialTickTime);

        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "name"), x + 5, y + 5, 0xFFFFFF);

        statsText.clearText();
        if (messagePageIndex == 0) {
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_1.title"));
            statsText.addNewLines(2);
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_1.text"));
        }
        if (messagePageIndex == 1) {
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_2.title"));
            statsText.addNewLines(2);
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_2.text"));
        }
        if (messagePageIndex == 2) {
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_3.title"));
            statsText.addNewLines(2);
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_3.text"));
        }
        if (messagePageIndex == 3) {
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_4.title"));
            statsText.addNewLines(2);
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_4.text"));
        }
        if (messagePageIndex == 4) {
            String urlDiscord = TextFormatting.BLUE.toString() + LibURLs.URL_DISCORD + TextFormatting.RESET.toString();
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_5.title"));
            statsText.addNewLines(2);
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "message_5.text", urlDiscord));
        }
        if (!StringUtils.isNullOrEmpty(joinFailMessage) & messagePageIndex == 4) {
            statsText.addNewLines(2);
            statsText.addText(TextFormatting.RED.toString());
            statsText.addText("Error: " + joinFailMessage);
            statsText.addText(TextFormatting.RESET.toString());
            statsText.addNewLine();
            statsText.addNewLine();
        }

        int[] javaVersion = GlobalSkinLibraryUtils.getJavaVersion();
        boolean validJava = GlobalSkinLibraryUtils.isValidJavaVersion(javaVersion);

        buttonNext.visible = validJava;
        buttonPrevious.visible = validJava;

        if (!validJava) {
            String urlWikiFaq = TextFormatting.BLUE.toString() + LibURLs.URL_WIKI_FAQ + TextFormatting.RESET.toString();
            String urlVideoUpdateJava = TextFormatting.BLUE.toString() + LibURLs.URL_VIDEO_UPDATE_JAVA + TextFormatting.RESET.toString();
            statsText.clearText();
            statsText.addText(GuiHelper.getLocalizedControlName(guiName, "old_java", javaVersion[0], javaVersion[1], urlWikiFaq, urlVideoUpdateJava));
        }

        statsText.draw(mouseX, mouseY);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        // TODO Auto-generated method stub
        super.drawForeground(mouseX, mouseY, partialTickTime);

    }
}
