package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.client.gui.globallibrary.dialog.GuiGlobalLibraryDialogDelete;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiGlobalLibraryPanelSkinEdit extends GuiPanel implements IDialogCallback {

    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibGuiResources.GUI_GLOBAL_LIBRARY);

    private final String guiName;
    private GuiLabeledTextField textName;
    private GuiLabeledTextField textTags;
    private GuiLabeledTextField textDescription;
    private GuiButtonExt buttonUpdate;
    private GuiButtonExt buttonDelete;
    private FutureTask<JsonObject> taskSkinEdit;
    private JsonObject skinJson = null;
    private Screen returnScreen;
    private boolean firstTick = false;

    public GuiGlobalLibraryPanelSkinEdit(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        guiName = ((GuiGlobalLibrary) parent).getGuiName() + ".edit";
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        textName = new GuiLabeledTextField(fontRenderer, x + 5, y + 35, 180, 12);
        textName.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterName"));
        textName.setMaxStringLength(80);

        textTags = new GuiLabeledTextField(fontRenderer, x + 5, y + 65, 180, 12);
        textTags.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterTags"));

        textDescription = new GuiLabeledTextField(fontRenderer, x + 5, y + 95, width - 10, 12);
        textDescription.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterDescription"));
        textDescription.setMaxStringLength(255);

        if (skinJson != null) {
            if (skinJson.has("name")) {
                textName.setText(skinJson.get("name").getAsString());
            }

            if (skinJson.has("description")) {
                textDescription.setText(skinJson.get("description").getAsString());
            }
        }

        buttonUpdate = new GuiButtonExt(0, x + 5, y + height - 25, 100, 20, GuiHelper.getLocalizedControlName(guiName, "buttonUpdate"));
        buttonDelete = new GuiButtonExt(0, x + width - 105, y + height - 25, 100, 20, GuiHelper.getLocalizedControlName(guiName, "buttonDelete"));

        buttonList.add(buttonUpdate);
        buttonList.add(buttonDelete);
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled) {
            return false;
        }
        if (textName.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (textTags.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (textDescription.textboxKeyTyped(c, keycode)) {
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
        if (!clicked) {
            clicked = textName.mouseClicked(mouseX, mouseY, button);
        }
        if (!clicked) {
            clicked = textTags.mouseClicked(mouseX, mouseY, button);
        }
        if (!clicked) {
            clicked = textDescription.mouseClicked(mouseX, mouseY, button);
        }
        if (button == 1) {
            if (textName.isFocused()) {
                textName.setText("");
            }
            if (textTags.isFocused()) {
                textTags.setText("");
            }
            if (textDescription.isFocused()) {
                textDescription.setText("");
            }
        }
        return clicked;
    }

    @Override
    public void update() {
        super.update();
        buttonUpdate.enabled = false;
        if (!StringUtils.isNullOrEmpty(textName.getText())) {
            buttonUpdate.enabled = true;
        }
        firstTick = false;
        if (taskSkinEdit != null && taskSkinEdit.isDone()) {
            try {
                JsonObject json = taskSkinEdit.get();
                taskSkinEdit = null;
                if (json != null) {
                    if (json.has("valid") & json.has("action")) {
                        String action = json.get("action").getAsString();
                        boolean valid = json.get("valid").getAsBoolean();
                        if (action.equals("user-skin-edit")) {
                            ((GuiGlobalLibrary) parent).panelHome.updateSkinPanels();
                            ((GuiGlobalLibrary) parent).switchScreen(returnScreen);
                        } else if (action.equals("user-skin-delete")) {
                            ((GuiGlobalLibrary) parent).switchScreen(returnScreen);
                        } else {
                            ModLogger.log(Level.WARN, "Server send unknown action: " + action);
                        }
                    } else {
                        ModLogger.log(Level.ERROR, "Server returned invalid responce.");
                    }
                } else {
                    ModLogger.log(Level.ERROR, "Server returned invalid responce.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!visible | !enabled) {
            return;
        }
        if (firstTick) {
            return;
        }
        if (button == buttonUpdate) {
            if (skinJson != null && skinJson.has("id")) {
                if (authenticateUser()) {
                    updateSkin();
                }
            }
        }
        if (button == buttonDelete) {
            ((GuiGlobalLibrary) parent).openDialog(new GuiGlobalLibraryDialogDelete(parent, guiName + ".dialog.delete", this, 190, 100));
        }
    }

    public void displaySkinInfo(JsonObject jsonObject, Screen returnScreen) {
        skinJson = jsonObject;
        ((GuiGlobalLibrary) parent).switchScreen(Screen.SKIN_EDIT);
        this.returnScreen = returnScreen;
        firstTick = true;
    }

    private boolean authenticateUser() {
        GameProfile gameProfile = mc.player.getGameProfile();
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        if (!plushieSession.isAuthenticated()) {
            JsonObject jsonObject = PlushieAuth.authenticateUser(gameProfile.getName(), gameProfile.getId().toString());
            plushieSession.authenticate(jsonObject);
        }

        if (!plushieSession.isAuthenticated()) {
            ModLogger.log(Level.ERROR, "Authentication failed.");
            return false;
        }
        return true;
    }

    public void updateSkin() {
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        GuiGlobalLibrary globalLibrary = (GuiGlobalLibrary) parent;
        int userId = plushieSession.getServerId();
        String accessToken = plushieSession.getAccessToken();
        int skinId = skinJson.get("id").getAsInt();
        String name = textName.getText().trim();
        String description = textDescription.getText().trim();
        taskSkinEdit = GlobalSkinLibraryUtils.editSkin(globalLibrary.uploadExecutor, userId, accessToken, skinId, name, description);
    }

    public void deleteSkin() {
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        GuiGlobalLibrary globalLibrary = (GuiGlobalLibrary) parent;
        int userId = plushieSession.getServerId();
        String accessToken = plushieSession.getAccessToken();
        int skinId = skinJson.get("id").getAsInt();
        taskSkinEdit = GlobalSkinLibraryUtils.deleteSkin(globalLibrary.jsonDownloadExecutor, userId, accessToken, skinId);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);

        super.draw(mouseX, mouseY, partialTickTime);
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "name"), x + 5, y + 5, 0xFFFFFF);

        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinName"), x + 5, y + 25, 0xFFFFFF);
        textName.drawTextBox();

        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinTags"), x + 5, y + 55, 0xFFFFFF);
        textTags.drawTextBox();

        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinDescription"), x + 5, y + 85, 0xFFFFFF);
        textDescription.drawTextBox();
        fontRenderer.drawString(textDescription.getText().length() + " / " + "255", x + 5, y + 115, 0xFFFFFF);

        int[] javaVersion = GlobalSkinLibraryUtils.getJavaVersion();
        if (!GlobalSkinLibraryUtils.isValidJavaVersion(javaVersion)) {
            fontRenderer.drawSplitString(TranslateUtils.translate("inventory.armourersworkshop:globalSkinLibrary.invalidJava", javaVersion[0], javaVersion[1]), x + 135, y + 65, width - 140, 0xFF8888);
        }
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        ModLogger.log(result);
        if (result == DialogResult.OK) {
            if (skinJson != null && skinJson.has("id")) {
                if (authenticateUser()) {
                    deleteSkin();
                }
            }
        }
        ((GuiGlobalLibrary) parent).closeDialog();
    }
}
