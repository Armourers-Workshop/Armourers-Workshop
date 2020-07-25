package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.util.ArrayList;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiControlSkinPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiControlSkinPanel.SkinIcon;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.PlushieUser;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskSkinListUser;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiGlobalLibraryPanelUserSkins extends GuiGlobalLibraryPanelSearchResults {

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
        refresh();
    }
    
    @Override
    public void refresh() {
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

        GlobalTaskSkinListUser taskSkinListUser = new GlobalTaskSkinListUser(userId, searchTypes, pageIndex, skinPanelResults.getIconCount());
        taskSkinListUser.createTaskAndRun(new FutureCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        onPageJsonDownload(result);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                // NO-OP
            }
        });
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
