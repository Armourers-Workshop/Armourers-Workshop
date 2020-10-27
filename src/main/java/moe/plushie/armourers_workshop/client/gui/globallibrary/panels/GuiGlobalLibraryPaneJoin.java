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
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin.BetaJoinResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPaneJoin extends GuiPanel {

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

    public GuiGlobalLibraryPaneJoin(GuiScreen parent, int x, int y, int width, int height) {
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

        buttonPrevious = new GuiIconButton(parent, 0, rectangleRules.x + 5, rectangleRules.y + recHeight - 16 - 5, 16, 16, "Previous", TEXTURE_BUTTONS);
        buttonPrevious.setIconLocation(208, 80, 16, 16);
        buttonPrevious.setDrawButtonBackground(false);

        buttonNext = new GuiIconButton(parent, 0, rectangleRules.x + recWidth - 16 - 5, rectangleRules.y + recHeight - 16 - 5, 16, 16, "Next", TEXTURE_BUTTONS);
        buttonNext.setIconLocation(208, 96, 16, 16);
        buttonNext.setDrawButtonBackground(false);

        buttonJoin = new GuiButtonExt(0, rectangleRules.x + recWidth / 2 - 140 / 2, rectangleRules.y + recHeight - 16 - 5, 140, 16, GuiHelper.getLocalizedControlName(guiName, "button.join"));
        buttonJoin.visible = false;

        statsText = new GuiCustomLabel(fontRenderer, rectangleRules.x + 5, rectangleRules.y + 5, recWidth - 10, recHeight - 75 - 5);

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
        statsText.addText(TextFormatting.BLACK.toString());
        if (messagePageIndex == 0) {
            statsText.addText("Welcome");
            statsText.addNewLines(2);
            statsText.addText("Here you can join the Armourer's Workshop global skin library. Joining the global library will allow you to:");
            statsText.addNewLine();
            statsText.addText("* upload your creations");
            statsText.addNewLine();
            statsText.addText("* fill out your profile");
            statsText.addNewLine();
            statsText.addText("* rate skins");
            statsText.addNewLine();
            statsText.addText("* create lists of skins");
            statsText.addNewLine();
            statsText.addText("* earn badges");
            statsText.addNewLines(2);
            statsText.addText("Please read each page of the rules carefully. ignorance of the rules is not an excuse if you get banned.");
            statsText.addNewLines(2);
            statsText.addText("Click the arrow on the buttom right to go to the next page.");
        }
        if (messagePageIndex == 1) {
            statsText.addText("Rules");
            statsText.addNewLines(2);
            statsText.addText("The global library has a 3 strike rule, if you get 3 strikes your account will be permanently ban from uploading to the global library.");
            statsText.addNewLines(2);
            statsText.addText("1. Do not upload other peoples work without their permission.");
            statsText.addNewLine();
            statsText.addText("2. Do not abuse the report system.");
            statsText.addNewLine();
            statsText.addText("3. Do not upload loads of coloured versions of the same model. (use dyes)");
            statsText.addNewLine();
            statsText.addText("4. Do not upload adult content (sexual, drug use, etc)");
            statsText.addNewLine();
            statsText.addText("5. Do not upload illegal content.");
        }
        if (messagePageIndex == 2) {
            statsText.addText("Derivative Works");
            statsText.addNewLines(2);
            statsText.addText("Derivative works are allowed on the global library. Please make sure any skins you upload from others are heavily edited and credited.");
            statsText.addNewLines(2);
            statsText.addText("Basic edits like recolours or settings changes are not allowed.");
        }
        if (messagePageIndex == 3) {
            statsText.addText("Account Requirements");
            statsText.addNewLines(2);
            statsText.addText("Please note that you must have a legitimate Minecraft account to join. This is to help with moderation and prevent spammers.");
        }
        if (messagePageIndex == 4) {
            statsText.addText("All Done");
            statsText.addNewLines(2);
            statsText.addText("Ready to join? Click the button below to join the community!");
        }
        if (!StringUtils.isNullOrEmpty(joinFailMessage)) {
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
            statsText.clearText();
            statsText.addText(TextFormatting.RED.toString());
            statsText.addText("Your java version is too old to join the global skin library");
            statsText.addText(TextFormatting.BLACK.toString());
            statsText.addNewLine();
            statsText.addNewLine();
            statsText.addText("Your java version is [Java " + javaVersion[0] + " update " + javaVersion[1] + "]. Please update to [Java 8 update 101] or newer.");
            statsText.addNewLine();
            statsText.addNewLine();
            statsText.addText("Please check the FAQ for help fixing this issue.");
            statsText.addNewLine();
            statsText.addUrl("https://github.com/RiskyKen/Armourers-Workshop/wiki/FAQ");
            statsText.addNewLine();
            statsText.addNewLine();
            statsText.addText(TextFormatting.BLACK.toString());
            statsText.addText("There is also a video guide to help fix this issue. ");
            statsText.addNewLine();
            statsText.addUrl("https://youtu.be/xZfaXHulmKo");
            statsText.addNewLine();
            statsText.addNewLine();
        }
        
        statsText.draw(mouseX, mouseY);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        // TODO Auto-generated method stub
        super.drawForeground(mouseX, mouseY, partialTickTime);

    }
}
