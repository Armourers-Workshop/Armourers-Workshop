package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomLabel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTextFieldCustom;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.SkinUploader;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelUpload extends GuiPanel {

    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibGuiResources.GUI_GLOBAL_LIBRARY);

    private final String guiName;
    private GuiLabeledTextField textName;
    private GuiLabeledTextField textTags;
    private GuiTextFieldCustom textDescription;
    private GuiButtonExt buttonUpload;
    private GuiCustomLabel statsText;

    private FutureTask<JsonObject> taskSkinUpload;
    private String error = null;

    public GuiGlobalLibraryPanelUpload(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        guiName = ((GuiGlobalLibrary) parent).getGuiName() + ".upload";
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

        buttonUpload = new GuiButtonExt(0, x + 23, y + height - 28, 96, 18, GuiHelper.getLocalizedControlName(guiName, "buttonUpload"));
        buttonUpload.enabled = false;

        statsText = new GuiCustomLabel(fontRenderer, x + width - 162 - 5, y + 5, 162, height - 90);

        buttonList.add(buttonUpload);
        if (visible) {
            updatePlayerSlots();
        }
    }

    @Override
    public GuiPanel setVisible(boolean visible) {
        if (visible) {
            updatePlayerSlots();
        }
        return super.setVisible(visible);
    }

    private void updatePlayerSlots() {
        ((GuiGlobalLibrary) parent).setPlayerSlotLocation(x + width - 18 * 9 - 4, y + height - 81);
        ((GuiGlobalLibrary) parent).setInputSlotLocation(x + 3, y + height - 27);
        ((GuiGlobalLibrary) parent).setOutputSlotLocation(x + 127, y + height - 27);
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
        buttonUpload.enabled = false;
        if (!StringUtils.isNullOrEmpty(textName.getText())) {
            SlotHidable slot = ((GuiGlobalLibrary) parent).getInputSlot();
            if (SkinNBTHelper.stackHasSkinData(slot.getStack())) {
                buttonUpload.enabled = true;
            }
        }
        if (taskSkinUpload != null && taskSkinUpload.isDone()) {
            try {
                JsonObject json = taskSkinUpload.get();
                taskSkinUpload = null;
                if (json != null) {
                    if (json.has("valid") & json.has("action")) {
                        String action = json.get("action").getAsString();
                        boolean valid = json.get("valid").getAsBoolean();
                        if (valid & action.equals("skin-upload")) {
                            ((GuiGlobalLibrary) parent).panelHome.updateSkinPanels();
                            ((GuiGlobalLibrary) parent).switchScreen(Screen.HOME);
                        }
                    } else {
                        if (json.has("reason")) {
                            String reason = json.get("reason").getAsString();
                            error = reason;
                        }
                    }
                } else {
                    // TODO handle upload failure
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
        if (button == buttonUpload) {
            GameProfile gameProfile = mc.player.getGameProfile();
            PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
            if (!plushieSession.isAuthenticated()) {
                JsonObject jsonObject = PlushieAuth.authenticateUser(gameProfile.getName(), gameProfile.getId().toString());
                plushieSession.authenticate(jsonObject);
            }

            if (!plushieSession.isAuthenticated()) {
                ModLogger.log(Level.ERROR, "Authentication failed.");
                return;
            }

            MessageClientGuiButton message = new MessageClientGuiButton((byte) 0);
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }

    public void uploadSkin(Skin skin) {
        GameProfile gameProfile = mc.player.getGameProfile();
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SkinIOUtils.saveSkinToStream(outputStream, skin);
        byte[] fileBytes = outputStream.toByteArray();
        IOUtils.closeQuietly(outputStream);
        taskSkinUpload = SkinUploader.uploadSkin(fileBytes, textName.getText().trim(), Integer.toString(plushieSession.getServerId()), textDescription.getText().trim(), plushieSession.getAccessToken());
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        mc.renderEngine.bindTexture(BUTTON_TEXTURES);
        // inv
        drawTexturedModalRect(x + width - 18 * 9 - 5, y + height - 82, 0, 180, 162, 76);
        // input
        drawTexturedModalRect(x + 2, y + height - 28, 0, 162, 18, 18);
        // output
        drawTexturedModalRect(x + 122, y + height - 32, 18, 154, 26, 26);

        super.draw(mouseX, mouseY, partialTickTime);
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "name"), x + 5, y + 5, 0xFFFFFF);

        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinName"), x + 5, y + 25, 0xFFFFFF);
        textName.drawTextBox();

        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinTags"), x + 5, y + 55, 0xFFFFFF);
        textTags.drawTextBox();

        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinDescription"), x + 5, y + 85, 0xFFFFFF);
        textDescription.drawButton(mc, mouseX, mouseY, partialTickTime);
        statsText.clearText();

        statsText.addText(GuiHelper.getLocalizedControlName(guiName, "closedBetaWarning"));
        statsText.addNewLine();
        statsText.addNewLine();

        if (!StringUtils.isNullOrEmpty(error)) {
            statsText.addText(TextFormatting.RED.toString());
            statsText.addText("Error: " + error);
            statsText.addText(TextFormatting.RESET.toString());
            statsText.addNewLine();
            statsText.addNewLine();
        }

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
}
