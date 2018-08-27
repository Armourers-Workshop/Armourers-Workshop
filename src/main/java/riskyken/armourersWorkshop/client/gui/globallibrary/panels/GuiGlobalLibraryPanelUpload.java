package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiLabeledTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.global.GlobalSkinLibraryUtils;
import riskyken.armourersWorkshop.common.library.global.SkinUploader;
import riskyken.armourersWorkshop.common.library.global.auth.PlushieAuth;
import riskyken.armourersWorkshop.common.library.global.auth.PlushieSession;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import riskyken.armourersWorkshop.utils.TranslateUtils;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelUpload extends GuiPanel {

    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/globalLibrary.png");
    
    private final String guiName;
    private GuiLabeledTextField textName;
    private GuiLabeledTextField textTags;
    private GuiLabeledTextField textDescription;
    private GuiButtonExt buttonUpload;
    
    private FutureTask<JsonObject> taskSkinUpload;
    private String error = null;
    
    public GuiGlobalLibraryPanelUpload(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        guiName = ((GuiGlobalLibrary)parent).getGuiName() + ".upload";
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
        
        buttonUpload = new GuiButtonExt(0, x + 5, y + 110, 100, 20, GuiHelper.getLocalizedControlName(guiName, "buttonUpload"));
        buttonUpload.enabled = false;
        
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
        ((GuiGlobalLibrary)parent).setPlayerSlotLocation(x + width / 2 - 18 * 9 / 2, y + height - 81);
        ((GuiGlobalLibrary)parent).setInputSlotLocation(x + 6, y + 140);
        ((GuiGlobalLibrary)parent).setOutputSlotLocation(x + 83, y + 140);
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
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
        textName.mouseClicked(mouseX, mouseY, button);
        textTags.mouseClicked(mouseX, mouseY, button);
        textDescription.mouseClicked(mouseX, mouseY, button);
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
    }
    
    @Override
    public void update() {
        super.update();
        buttonUpload.enabled = false;
        if (!StringUtils.isNullOrEmpty(textName.getText())) {
            SlotHidable slot = ((GuiGlobalLibrary)parent).getInputSlot();
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
                            ((GuiGlobalLibrary)parent).panelHome.updateSkinPanels();
                            ((GuiGlobalLibrary)parent).switchScreen(Screen.HOME);
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
            GameProfile gameProfile = mc.thePlayer.getGameProfile();
            PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
            if (!plushieSession.isAuthenticated()) {
                JsonObject jsonObject = PlushieAuth.updateAccessToken(gameProfile.getName(), gameProfile.getId().toString());
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
        GameProfile gameProfile = mc.thePlayer.getGameProfile();
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
        //inv
        drawTexturedModalRect(x + width / 2 - 162 / 2 - 1, y + height - 82, 0, 180, 162, 76);
        //input
        drawTexturedModalRect(x + 5, y + 139, 0, 162, 18, 18);
        //output
        drawTexturedModalRect(x + 78, y + 135, 18, 154, 26, 26);
        
        super.draw(mouseX, mouseY, partialTickTime);
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "name"), x + 5, y + 5, 0xFFFFFF);
        
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinName"), x + 5, y + 25, 0xFFFFFF);
        textName.drawTextBox();
        
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinTags"), x + 5, y + 55, 0xFFFFFF);
        textTags.drawTextBox();
        
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinDescription"), x + 5, y + 85, 0xFFFFFF);
        textDescription.drawTextBox();
        
        fontRenderer.drawSplitString(GuiHelper.getLocalizedControlName(guiName, "closedBetaWarning"), x + 195, y + 35, width - 200, 0xFF8888);
        
        if (!StringUtils.isNullOrEmpty(error)) {
            fontRenderer.drawSplitString("Error: " + error, x + 195, y + 115, width - 200, 0xFF8888);
        }
        
        int[] javaVersion = GlobalSkinLibraryUtils.getJavaVersion();
        if (!GlobalSkinLibraryUtils.isValidJavaVersion(javaVersion)) {
            fontRenderer.drawSplitString(TranslateUtils.translate("inventory.armourersworkshop:globalSkinLibrary.invalidJava", javaVersion[0], javaVersion[1]), x + 135, y + 65, width - 140, 0xFF8888);
        }
    }
}
