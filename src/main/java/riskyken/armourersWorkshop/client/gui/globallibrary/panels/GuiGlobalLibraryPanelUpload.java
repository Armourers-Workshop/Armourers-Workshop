package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.util.UUID;
import java.util.concurrent.FutureTask;

import com.google.gson.JsonArray;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiLabeledTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.common.library.global.auth.PlushieAuth;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelUpload extends GuiPanel {

    private final String guiName;
    private GuiLabeledTextField textName;
    private GuiLabeledTextField textTags;
    //private GuiLabeledTextField textDescription;
    private GuiButtonExt buttonUpload;
    
    //Beta
    private GuiLabeledTextField textBetaCode;
    private GuiButtonExt buttonCheckBetaCode;
    
    private boolean haveChecked = false;
    private JsonArray jsonArray = null;
    private FutureTask<JsonArray> jsonDownload = null;
    
    public GuiGlobalLibraryPanelUpload(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        guiName = ((GuiGlobalLibrary)parent).getGuiName() + ".upload";
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        textName = new GuiLabeledTextField(fontRenderer, x + 5, y + 35, 120, 12);
        textName.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterName"));
        
        textTags = new GuiLabeledTextField(fontRenderer, x + 5, y + 65, 120, 12);
        textTags.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterTags"));
        
        //textDescription = new GuiLabeledTextField(fontRenderer, x + 5, y + 95, 120, 12 * 7);
        //textDescription.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterDescription"));
        
        textBetaCode = new GuiLabeledTextField(fontRenderer, x + width - 185, y + 35, 180, 12);
        textBetaCode.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "enterBetaCode"));
        textBetaCode.setMaxStringLength(36);
        
        buttonUpload = new GuiButtonExt(0, x + 5, y + height - 25, 100, 20, GuiHelper.getLocalizedControlName(guiName, "buttonUpload"));
        buttonUpload.enabled = false;
        
        buttonCheckBetaCode = new GuiButtonExt(0, x + width - 125, y + 50, 120, 20, GuiHelper.getLocalizedControlName(guiName, "buttonCheckBetaCode"));
        buttonCheckBetaCode.enabled = false;
        
        buttonList.add(buttonUpload);
        buttonList.add(buttonCheckBetaCode);
        
        if (visible) {
            updatePlayerSlots();
        }
    }
    
    @Override
    public void update() {
        super.update();
        if (jsonDownload != null && jsonDownload.isDone()) {
            try {
                jsonArray = jsonDownload.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        ((GuiGlobalLibrary)parent).setPlayerSlotLocation(x + width / 2 - 18 * 9 / 2, height - 42);
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled | haveOpenDialog()) {
            return false;
        }
        if (textName.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (textTags.textboxKeyTyped(c, keycode)) {
            return true;
        }
        //if (textDescription.textboxKeyTyped(c, keycode)) {
        //    return true;
        //}
        if (textBetaCode.textboxKeyTyped(c, keycode)) {
            buttonCheckBetaCode.enabled = textBetaCode.getText().length() == 36;
            return true;
        }
        return false;
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled | haveOpenDialog()) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
        textName.mouseClicked(mouseX, mouseY, button);
        textTags.mouseClicked(mouseX, mouseY, button);
        //textDescription.mouseClicked(mouseX, mouseY, button);
        textBetaCode.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            if (textName.isFocused()) {
                textName.setText("");
            }
            if (textTags.isFocused()) {
                textTags.setText("");
            }
            //if (textDescription.isFocused()) {
            //    textDescription.setText("");
            //}
            if (textBetaCode.isFocused()) {
                textBetaCode.setText("");
            }
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonCheckBetaCode) {
            if (textBetaCode.getText().length() == 36) {
                try {
                    UUID uuid = UUID.fromString(textBetaCode.getText());
                    jsonDownload = PlushieAuth.isPlayerInBeta(uuid);
                } catch (IllegalArgumentException e) {
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
        
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinName"), x + 5, y + 25, 0xFFFFFF);
        textName.drawTextBox();
        
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinTags"), x + 5, y + 55, 0xFFFFFF);
        textTags.drawTextBox();
        
        //fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "skinDescription"), x + 5, y + 85, 0xFFFFFF);
        //textDescription.drawTextBox();
        
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "betaCode"), x + width - 185, y + 25, 0xFFFFFF);
        textBetaCode.drawTextBox();
        
        fontRenderer.drawSplitString(GuiHelper.getLocalizedControlName(guiName, "closedBeta"), x + width - 185, y + 75, 180, 0xFF8888);
        
        fontRenderer.drawSplitString(GuiHelper.getLocalizedControlName(guiName, "closedBetaWarning"), x + 5, y + 85, 180, 0xFF8888);
        
        if (jsonArray != null) {
            fontRenderer.drawSplitString(jsonArray.toString(), x + width - 185, y + 115, 180, 0xFF8888);
        }
    }
}
