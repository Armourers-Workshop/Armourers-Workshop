package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiControlSkinPanel;
import riskyken.armourersWorkshop.client.gui.controls.GuiControlSkinPanel.SkinIcon;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.global.DownloadUtils.DownloadJsonMultipartForm;
import riskyken.armourersWorkshop.common.library.global.GlobalSkinLibraryUtils;
import riskyken.armourersWorkshop.common.library.global.MultipartForm;
import riskyken.armourersWorkshop.common.library.global.PlushieUser;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class GuiGlobalLibraryPanelUserSkins extends GuiGlobalLibraryPanelSearchResults {

    private static final String USER_URL = BASE_URL + "user-skins-page.php";
    private int userId;

    public GuiGlobalLibraryPanelUserSkins(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    public void switchToUser(int userId) {
        clearResults();
        if (userId != 0) {
            this.userId = userId;
            fetchPage(0);
        }
    }

    @Override
    protected void resize() {
        int thisUserId = userId;
        clearResults();
        switchToUser(thisUserId);
    }

    @Override
    protected void fetchPage(int pageIndex) {
        if (!downloadedPageList.contains(pageIndex)) {
            downloadedPageList.add(pageIndex);
        } else {
            return;
        }

        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        String searchTypes = "";
        for (int i = 0; i < skinTypes.size(); i++) {
            searchTypes += (skinTypes.get(i).getRegistryName());
            if (i < skinTypes.size() - 1) {
                searchTypes += ";";
            }
        }

        String searchUrl = USER_URL;
        searchUrl += "?userId=" + String.valueOf(userId);
        searchUrl += "&maxFileVersion=" + String.valueOf(Skin.FILE_VERSION);
        searchUrl += "&pageIndex=" + String.valueOf(pageIndex);
        searchUrl += "&pageSize=" + String.valueOf(skinPanelResults.getIconCount());

        MultipartForm multipartFormSearch = new MultipartForm(searchUrl);
        multipartFormSearch.addText("searchTypes", searchTypes);
        pageCompletion.submit(new DownloadJsonMultipartForm(multipartFormSearch));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            changePage(currentPageIndex - 1);
        }
        if (button.id == 2) {
            changePage(currentPageIndex + 1);
            if (currentPageIndex + 1 < totalPages) {
                fetchPage(currentPageIndex + 1);
            }
        }
        if (button == iconButtonSmall) {
            iconScale = 50;
            skinPanelResults.setIconSize(iconScale);
            resize();
        }
        if (button == iconButtonMedium) {
            iconScale = 80;
            skinPanelResults.setIconSize(iconScale);
            resize();
        }
        if (button == iconButtonLarge) {
            iconScale = 110;
            skinPanelResults.setIconSize(iconScale);
            resize();
        }
        if (button == skinPanelResults) {
            SkinIcon skinIcon = ((GuiControlSkinPanel) button).getLastPressedSkinIcon();
            if (skinIcon != null) {
                ((GuiGlobalLibrary) parent).panelSkinInfo.displaySkinInfo(skinIcon.getSkinJson(), Screen.USER_SKINS);
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

        int maxPages = totalPages;
        int totalSkins = totalResults;

        String guiName = ((GuiGlobalLibrary) parent).getGuiName();
        String unlocalizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + "." + "userSkins.results";

        String resultsText = TranslateUtils.translate(unlocalizedName, username, currentPageIndex + 1, maxPages, totalSkins);
        if (jsonCurrentPage == null) {
            resultsText = GuiHelper.getLocalizedControlName(guiName, "searchResults.label.searching");
        }
        fontRenderer.drawString(resultsText, x + 5, y + 6, 0xFFEEEEEE);
    }
}
