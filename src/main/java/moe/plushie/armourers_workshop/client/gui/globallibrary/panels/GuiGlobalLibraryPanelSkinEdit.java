package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import org.apache.logging.log4j.Level;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomLabel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTextFieldCustom;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.client.gui.globallibrary.dialog.GuiGlobalLibraryDialogDelete;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskSkinDelete;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskSkinEdit;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiGlobalLibraryPanelSkinEdit extends GuiPanel implements IDialogCallback {

    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibGuiResources.GUI_GLOBAL_LIBRARY);

    private final String guiName;
    private boolean moderator = false;
    private GuiLabeledTextField textName;
    private GuiLabeledTextField textTags;
    private GuiTextFieldCustom textDescription;
    private GuiButtonExt buttonUpdate;
    private GuiButtonExt buttonDelete;
    private GuiCustomLabel statsText;

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
        textName = new GuiLabeledTextField(fontRenderer, x + 5, y + 35, width - 15 - 162, 12);
        textName.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterName"));
        textName.setMaxStringLength(80);

        textTags = new GuiLabeledTextField(fontRenderer, x + 5, y + 65, width - 15 - 162, 12);
        textTags.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterTags"));

        textDescription = new GuiTextFieldCustom(x + 5, y + 95, width - 15 - 162, height - 95 - 40);
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

        statsText = new GuiCustomLabel(fontRenderer, x + width - 162 - 5, y + 5, 162, height - 90);

        buttonList.add(buttonUpdate);
        buttonList.add(buttonDelete);
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
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
        if (textDescription.keyTyped(c, keycode)) {
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
            clicked = textTags.mouseClicked(mouseX, mouseY, button);
            clicked = textDescription.mouseClicked(mouseX, mouseY, button);
        }
        if (!clicked) {
            clicked = statsText.mouseClick(mouseX, mouseY, button);
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
                updateSkin();
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

    public void updateSkin() {
        int skinID = skinJson.get("id").getAsInt();
        new GlobalTaskSkinEdit(skinID, textName.getText().trim(), textDescription.getText().trim(), moderator).createTaskAndRun(new FutureCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        if (result.has("valid") & result.has("action")) {
                            String action = result.get("action").getAsString();
                            boolean valid = result.get("valid").getAsBoolean();
                            if (action.equals("user-skin-edit")) {
                                ((GuiGlobalLibrary) parent).panelHome.updateSkinPanels();
                                ((GuiGlobalLibrary) parent).switchScreen(returnScreen);
                            } else {
                                ModLogger.log(Level.WARN, "Server send unknown action: " + action);
                            }
                        } else {
                            ModLogger.log(Level.ERROR, "Server returned invalid responce.");
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void deleteSkin() {
        int skinID = skinJson.get("id").getAsInt();
        new GlobalTaskSkinDelete(skinID, moderator).createTaskAndRun(new FutureCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        if (result.has("valid") & result.has("action")) {
                            String action = result.get("action").getAsString();
                            boolean valid = result.get("valid").getAsBoolean();
                            if (action.equals("user-skin-delete")) {
                                if (returnScreen == Screen.HOME) {
                                    ((GuiGlobalLibrary) parent).panelHome.updateSkinPanels();
                                }
                                if (returnScreen == Screen.SEARCH) {
                                    ((GuiGlobalLibrary) parent).panelSearchResults.refresh();
                                }
                                if (returnScreen == Screen.USER_SKINS) {
                                    ((GuiGlobalLibrary) parent).panelUserSkins.refresh();
                                }
                                ((GuiGlobalLibrary) parent).switchScreen(returnScreen);
                            } else {
                                ModLogger.log(Level.WARN, "Server send unknown action: " + action);
                            }
                        } else {
                            ModLogger.log(Level.ERROR, "Server returned invalid responce.");
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
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
        textDescription.drawButton(mc, mouseX, mouseY, partialTickTime);
        // fontRenderer.drawString(textDescription.getText().length() + " / " + "255", x
        // + 5, y + 115, 0xFFFFFF);

        statsText.clearText();
        int[] javaVersion = GlobalSkinLibraryUtils.getJavaVersion();
        if (!GlobalSkinLibraryUtils.isValidJavaVersion(javaVersion)) {
            statsText.addText(TextFormatting.RED.toString());
            statsText.addText(TranslateUtils.translate("inventory.armourers_workshop:global-skin-library.invalidJava", javaVersion[0], javaVersion[1]));
            statsText.addText(TextFormatting.RESET.toString());
            statsText.addNewLine();
            statsText.addNewLine();
        }

        statsText.draw(mouseX, mouseY);
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.OK) {
            if (skinJson != null && skinJson.has("id")) {
                deleteSkin();
            }
        }
        ((GuiGlobalLibrary) parent).closeDialog();
    }
}
