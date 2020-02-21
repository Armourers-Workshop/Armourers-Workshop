package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.util.UUID;

import com.google.common.util.concurrent.FutureCallback;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin.BetaJoinResult;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin.BetaJoinResult.JoinResult;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPaneJoinBeta extends GuiPanel {

    private final String guiName;

    private GuiLabeledTextField textBetaCode;
    private GuiButtonExt buttonCheckBetaCode;

    private boolean joining = false;
    private String joinState = "";
    private String joinFailMessage = null;

    public GuiGlobalLibraryPaneJoinBeta(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        guiName = ((GuiGlobalLibrary) parent).getGuiName() + ".joinBeta";
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        textBetaCode = new GuiLabeledTextField(fontRenderer, x + 5, y + 35, 230, 12);
        textBetaCode.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterBetaCode"));
        textBetaCode.setMaxStringLength(36);

        buttonCheckBetaCode = new GuiButtonExt(0, x + 5, y + 50, 230, 20, GuiHelper.getLocalizedControlName(guiName, "buttonJoinBeta"));
        buttonCheckBetaCode.enabled = false;

        buttonList.add(buttonCheckBetaCode);
    }

    private void joinedBetaFailed(String reason) {
        joining = false;
        joinFailMessage = reason;
    }

    private void joinedBeta() {
        joining = false;
        joinFailMessage = "";
        ((GuiGlobalLibrary) parent).switchScreen(Screen.HOME);
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled) {
            return false;
        }
        if (textBetaCode.textboxKeyTyped(c, keycode)) {
            buttonCheckBetaCode.enabled = textBetaCode.getText().length() == 36;
            if (!GlobalSkinLibraryUtils.isValidJavaVersion()) {
                buttonCheckBetaCode.enabled = false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled) {
            return false;
        }
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);
        textBetaCode.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            if (textBetaCode.isFocused()) {
                textBetaCode.setText("");
            }
        }
        return clicked;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonCheckBetaCode) {

            if (textBetaCode.getText().length() == 36) {
                try {
                    joining = true;
                    joinFailMessage = "";
                    UUID uuid = UUID.fromString(textBetaCode.getText());
                    new GlobalTaskBetaJoin(uuid).createTaskAndRun(new FutureCallback<GlobalTaskBetaJoin.BetaJoinResult>() {

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
                                        joinedBetaFailed(result.getMessage());
                                        break;
                                    case CODE_CHECK_FAILED:
                                        joinedBetaFailed(result.getMessage());
                                        break;
                                    case CODE_INVALID:
                                        joinedBetaFailed(result.getMessage());
                                        break;
                                    case JOIN_FAILED:
                                        joinedBetaFailed(result.getMessage());
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
                                    joinedBetaFailed(t.getMessage());
                                }
                            });
                        }
                    });
                } catch (IllegalArgumentException e) {
                    joinedBetaFailed(JoinResult.CODE_INVALID.toString().toLowerCase());
                }
            }
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.draw(mouseX, mouseY, partialTickTime);
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "name"), x + 5, y + 5, 0xFFFFFF);

        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "betaCode"), x + 5, y + 25, 0xFFFFFF);
        textBetaCode.drawTextBox();

        fontRenderer.drawSplitString(GuiHelper.getLocalizedControlName(guiName, "closedBeta"), x + 5, y + 75, 230, 0xEEEEEE);

        if (joinState != null && !StringUtils.isNullOrEmpty(joinState)) {
            fontRenderer.drawSplitString(GuiHelper.getLocalizedControlName(guiName, joinState), x + 5, y + 115, 230, 0xEEEEEE);
        }

        if (joinFailMessage != null) {
            fontRenderer.drawSplitString(joinFailMessage, x + 5, y + 140, 230, 0xFF8888);
        }

        int[] javaVersion = GlobalSkinLibraryUtils.getJavaVersion();
        if (!GlobalSkinLibraryUtils.isValidJavaVersion(javaVersion)) {
            fontRenderer.drawSplitString(TranslateUtils.translate("inventory.armourers_workshop:global-skin-library.invalidJava", javaVersion[0], javaVersion[1]), x + 5, y + 160, 230, 0xFF8888);
        }
    }
}
