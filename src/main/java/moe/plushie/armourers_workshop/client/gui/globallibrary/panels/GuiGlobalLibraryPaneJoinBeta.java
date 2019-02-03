package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.util.UUID;
import java.util.concurrent.FutureTask;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
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
    
    private final String STATE_CHECK_CODE = "checkCode";
    private final String STATE_CODE_INVALID = "codeInvalid";
    private final String STATE_JOINING_BETA = "joiningBeta";
    private final String STATE_JOIN_FAILED = "joinedFailed";
    private final String STATE_JOINED_BETA = "joinedBeta";
    
    private FutureTask<JsonObject> taskBetaJoinJson = null;
    private boolean joining = false;
    private String joinState = "";
    private String joinFailMessage = null;
    
    public GuiGlobalLibraryPaneJoinBeta(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        guiName = ((GuiGlobalLibrary)parent).getGuiName() + ".joinBeta";
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
    
    @Override
    public void update() {
        super.update();
        if (taskBetaJoinJson != null && taskBetaJoinJson.isDone()) {
            try {
                JsonObject jsonObject = taskBetaJoinJson.get();
                taskBetaJoinJson = null;
                if (jsonObject.has("action") & jsonObject.has("valid")) {
                    String action = jsonObject.get("action").getAsString();
                    boolean valid = jsonObject.get("valid").getAsBoolean();
                    if (action.equals("beta-code-check")) {
                        if (valid) {
                            GameProfile gameProfile = mc.player.getGameProfile();
                            joinState = STATE_JOINING_BETA;
                            taskBetaJoinJson = PlushieAuth.joinBeta(gameProfile.getName(), gameProfile.getId().toString(), textBetaCode.getText());
                        } else {
                            invalidBetaCode();
                        }
                    } else if (action.equals("beta-join")) {
                        if (valid) {
                            joinedBeta(jsonObject);

                        } else {
                            String reason = null;
                            if (jsonObject.has("reason")) {
                                reason = jsonObject.get("reason").getAsString();
                            }
                            joinedBetaFailed(reason);
                        }
                    }
                }
            } catch (Exception e) {
                taskBetaJoinJson = null;
                e.printStackTrace();
            }
        }
    }
    
    private void invalidBetaCode() {
        joining = false;
        joinState = STATE_CODE_INVALID;
    }
    
    private void joinedBetaFailed(String reason) {
        joining = false;
        joinState = STATE_JOIN_FAILED;
        joinFailMessage = reason;
    }
    
    private void joinedBeta(JsonObject jsonObject) {
        joining = false;
        joinState = STATE_JOINED_BETA;
        PlushieAuth.doRemoteUserCheck();
        PlushieAuth.PLUSHIE_SESSION.authenticate(jsonObject);
        ((GuiGlobalLibrary)parent).switchScreen(Screen.HOME);
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
                    UUID uuid = UUID.fromString(textBetaCode.getText());
                    joining = true;
                    joinState = STATE_CHECK_CODE;
                    taskBetaJoinJson = PlushieAuth.checkBetaCode(uuid);
                } catch (IllegalArgumentException e) {
                    invalidBetaCode();
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
