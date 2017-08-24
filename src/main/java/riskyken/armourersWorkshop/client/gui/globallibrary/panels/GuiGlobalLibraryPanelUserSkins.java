package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.util.concurrent.FutureTask;

import com.google.gson.JsonArray;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiControlSkinPanel;
import riskyken.armourersWorkshop.client.gui.controls.GuiControlSkinPanel.SkinIcon;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.global.GlobalSkinLibraryUtils;
import riskyken.armourersWorkshop.common.library.global.PlushieUser;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class GuiGlobalLibraryPanelUserSkins extends GuiGlobalLibraryPanelSearchResults {

    private int userId;
    
    public GuiGlobalLibraryPanelUserSkins(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    public void setDownloadResultsTask(FutureTask<JsonArray> downloadsResultsTask, int userId) {
        super.setDownloadSearchResultsTask(downloadsResultsTask);
        this.userId = userId;
    }
    
    @Override
    public void initGui() {
        super.initGui();
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            changePage(page - 1);
        }
        if (button.id == 2) {
            changePage(page + 1);
        }
        if (button == iconButtonSmall) {
            iconScale = 50;
            skinPanelResults.setIconSize(iconScale);
            updateSkinForPage();
        }
        if (button == iconButtonMedium) {
            iconScale = 80;
            skinPanelResults.setIconSize(iconScale);
            updateSkinForPage();
        }
        if (button == iconButtonLarge) {
            iconScale = 110;
            skinPanelResults.setIconSize(iconScale);
            updateSkinForPage();
        }
        if (button == skinPanelResults) {
            SkinIcon skinIcon = ((GuiControlSkinPanel)button).getLastPressedSkinIcon();
            if (skinIcon != null) {
                ((GuiGlobalLibrary)parent).panelSkinInfo.displaySkinInfo(skinIcon.getSkinJson(), Screen.USER_SKINS);
            }
        }
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        PlushieUser plushieUser = GlobalSkinLibraryUtils.getUserInfo(userId);
        String username = "unknown";
        if (plushieUser != null) {
            username = plushieUser.getUsername();
        }
        
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.draw(mouseX, mouseY, partialTickTime);
        
        int maxPages = getMaxPages();
        int totalSkins = getNumberOfSkin();
        
        String guiName = ((GuiGlobalLibrary)parent).getGuiName();
        String unlocalizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + "." + "userSkins.results";
        
        String resultsText = TranslateUtils.translate(unlocalizedName, username, page + 1, maxPages, totalSkins);
        if (json == null) {
            resultsText = GuiHelper.getLocalizedControlName(guiName, "searchResults.label.searching");
        }
        fontRenderer.drawString(resultsText, x + 5, y + 6, 0xFFEEEEEE);
    }
}
