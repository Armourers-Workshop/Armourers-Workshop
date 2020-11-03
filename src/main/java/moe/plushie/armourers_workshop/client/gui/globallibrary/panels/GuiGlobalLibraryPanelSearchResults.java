package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiControlSkinPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiControlSkinPanel.SkinIcon;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskSkinSearch;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskSkinSearch.SearchColumnType;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskSkinSearch.SearchOrderType;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSearchResults extends GuiPanel {

    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibGuiResources.GUI_GLOBAL_LIBRARY);
    private static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.CONTROL_BUTTONS);

    protected final GuiControlSkinPanel skinPanelResults;

    protected GuiIconButton iconButtonSmall;
    protected GuiIconButton iconButtonMedium;
    protected GuiIconButton iconButtonLarge;

    protected static int iconScale = 60;

    protected String search = null;
    protected ISkinType skinType = null;
    protected SearchColumnType searchOrderColumn = null;
    protected SearchOrderType searchOrder = null;

    protected JsonArray[] pageList;
    protected HashSet<Integer> downloadedPageList;
    protected JsonArray jsonCurrentPage;
    protected int currentPageIndex = 0;
    protected int totalPages = -1;
    protected int totalResults = 0;

    public GuiGlobalLibraryPanelSearchResults(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        skinPanelResults = new GuiControlSkinPanel();
        pageList = null;
        downloadedPageList = new HashSet<Integer>();
    }

    public void doSearch(String search, ISkinType skinType, SearchColumnType searchOrderColumn, SearchOrderType searchOrder) {
        clearResults();
        this.search = search;
        this.skinType = skinType;
        this.searchOrderColumn = searchOrderColumn;
        this.searchOrder = searchOrder;
        if (this.search == null) {
            return;
        }
        ((GuiGlobalLibrary) parent).panelSearchBox.updateDropDowns(searchOrderColumn, searchOrder);
        fetchPage(0);
    }

    public void clearResults() {
        search = null;
        skinType = null;
        pageList = null;
        jsonCurrentPage = null;
        currentPageIndex = 0;
        totalPages = -1;
        totalResults = 0;
        skinPanelResults.clearIcons();
        downloadedPageList.clear();
    }

    protected void resize() {
        refresh();
    }

    public void refresh() {
        String thisSearch = search;
        ISkinType thisSkinType = skinType;
        SearchColumnType thisSearchOrderColumn = searchOrderColumn;
        SearchOrderType thisSearchOrder = searchOrder;
        int thisPage = currentPageIndex;
        clearResults();
        doSearch(thisSearch, thisSkinType, thisSearchOrderColumn, thisSearchOrder);
    }

    protected void fetchPage(int pageIndex) {
        if (!downloadedPageList.contains(pageIndex)) {
            downloadedPageList.add(pageIndex);
        } else {
            return;
        }
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        String searchTypes = "";
        if (skinType == null) {
            for (int i = 0; i < skinTypes.size(); i++) {
                searchTypes += (skinTypes.get(i).getRegistryName());
                if (i < skinTypes.size() - 1) {
                    searchTypes += ";";
                }
            }
        } else {
            searchTypes = skinType.getRegistryName();
        }
        GlobalTaskSkinSearch taskSkinSearch = new GlobalTaskSkinSearch(search, searchTypes, pageIndex, skinPanelResults.getIconCount());
        taskSkinSearch.setSearchOrderColumn(searchOrderColumn);
        taskSkinSearch.setSearchOrder(searchOrder);
        taskSkinSearch.createTaskAndRun(new FutureCallback<JsonObject>() {

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

    protected void onPageJsonDownload(JsonObject pageJson) {
        if (pageJson.has("totalPages")) {
            totalPages = pageJson.get("totalPages").getAsInt();
        }
        if (pageJson.has("totalResults")) {
            totalResults = pageJson.get("totalResults").getAsInt();
        }
        int pageIndex = 0;
        if (pageJson.has("currentPageIndex")) {

            pageIndex = pageJson.get("currentPageIndex").getAsInt();
            if (pageIndex == 0 & totalPages > 1) {
                fetchPage(1);
            }
        }
        if (pageJson.has("results")) {
            JsonArray pageResults = pageJson.get("results").getAsJsonArray();
            if (pageList == null) {
                pageList = new JsonArray[totalPages];
            }
            if (totalPages != 0) {
                pageList[pageIndex] = pageResults;
                updateSkinForPage();
            }
        }
    }

    @Override
    public GuiPanel setSize(int width, int height) {
        boolean resized = width != this.width | height != this.height;
        super.setSize(width, height);
        initGui();
        if (resized) {
            resize();
        }
        return this;
    }

    @Override
    public void initGui() {
        super.initGui();

        String guiName = ((GuiGlobalLibrary) parent).getGuiName();
        buttonList.clear();

        skinPanelResults.init(x + 2, this.y + 20, width - 4, height - 40);
        skinPanelResults.setIconSize(iconScale);
        skinPanelResults.setPanelPadding(0);
        skinPanelResults.setShowName(true);

        iconButtonSmall = new GuiIconButton(parent, 0, x + width - 21 * 3, y + 2, 16, 16, GuiHelper.getLocalizedControlName(guiName, "searchResults.small"), BUTTON_TEXTURES);
        iconButtonSmall.setIconLocation(48, 0, 16, 16);

        iconButtonMedium = new GuiIconButton(parent, 0, x + width - 21 * 2, y + 2, 16, 16, GuiHelper.getLocalizedControlName(guiName, "searchResults.medium"), BUTTON_TEXTURES);
        iconButtonMedium.setIconLocation(48, 17, 16, 16);

        iconButtonLarge = new GuiIconButton(parent, 0, x + width - 21, y + 2, 16, 16, GuiHelper.getLocalizedControlName(guiName, "searchResults.large"), BUTTON_TEXTURES);
        iconButtonLarge.setIconLocation(48, 34, 16, 16);

        buttonList.add(iconButtonSmall);
        buttonList.add(iconButtonMedium);
        buttonList.add(iconButtonLarge);
        buttonList.add(skinPanelResults);

        GuiIconButton buttonPrevious = new GuiIconButton(parent, 1, x + 2, y + height - 18, 16, 16, I18n.format(LibGuiResources.Controls.BUTTON_PREVIOUS), TEXTURE_BUTTONS);
        buttonPrevious.setIconLocation(208, 80, 16, 16);
        buttonPrevious.setDrawButtonBackground(false);

        GuiIconButton buttonNext = new GuiIconButton(parent, 2, x + width - 18, y + height - 18, 16, 16, I18n.format(LibGuiResources.Controls.BUTTON_NEXT), TEXTURE_BUTTONS);
        buttonNext.setIconLocation(208, 96, 16, 16);
        buttonNext.setDrawButtonBackground(false);

        buttonList.add(buttonPrevious);
        buttonList.add(buttonNext);
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
            iconScale = 30;
            skinPanelResults.setIconSize(iconScale);
            resize();
        }
        if (button == iconButtonMedium) {
            iconScale = 60;
            skinPanelResults.setIconSize(iconScale);
            resize();
        }
        if (button == iconButtonLarge) {
            iconScale = 90;
            skinPanelResults.setIconSize(iconScale);
            resize();
        }
        if (button == skinPanelResults) {
            SkinIcon skinIcon = ((GuiControlSkinPanel) button).getLastPressedSkinIcon();
            if (skinIcon != null) {
                ((GuiGlobalLibrary) parent).panelSkinInfo.displaySkinInfo(skinIcon.getSkinJson(), Screen.SEARCH);
            }
        }
    }

    protected void changePage(int pageIndex) {
        if (pageIndex < totalPages & pageIndex >= 0) {
            this.currentPageIndex = pageIndex;
        }
        if (this.currentPageIndex > totalPages) {
            this.currentPageIndex = totalPages - 1;
        }
        if (this.currentPageIndex < 0) {
            this.currentPageIndex = 0;
        }
        updateSkinForPage();
    }

    protected void updateSkinForPage() {
        jsonCurrentPage = getJsonForPage(currentPageIndex);
        if (jsonCurrentPage != null) {
            int skinsPerPage = skinPanelResults.getIconCount();
            int pageOffset = skinsPerPage * currentPageIndex;
            skinPanelResults.clearIcons();
            JsonArray downloadArray = new JsonArray();
            for (int i = 0; i < skinsPerPage; i++) {
                if (i < jsonCurrentPage.size()) {
                    JsonObject skinJson = jsonCurrentPage.get(i).getAsJsonObject();
                    downloadArray.add(skinJson);
                    skinPanelResults.addIcon(skinJson);
                }
            }
        }
    }

    private JsonArray getJsonForPage(int pageIndex) {
        if (pageList != null) {
            if (pageIndex >= 0 & pageIndex < pageList.length) {
                return pageList[pageIndex];
            }
        }
        return null;

    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (this.getClass().equals(GuiGlobalLibraryPanelSearchResults.class)) {
            if (!visible) {
                return;
            }
            drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
            super.draw(mouseX, mouseY, partialTickTime);

            int maxPages = totalPages;
            int totalSkins = totalResults;

            String guiName = ((GuiGlobalLibrary) parent).getGuiName();
            String unlocalizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + "." + "searchResults.results";

            String resultsText = TranslateUtils.translate(unlocalizedName, currentPageIndex + 1, maxPages, totalSkins);
            if (jsonCurrentPage == null & totalPages == -1) {
                resultsText = GuiHelper.getLocalizedControlName(guiName, "searchResults.label.searching");
            }
            if (totalPages == 0) {
                resultsText = GuiHelper.getLocalizedControlName(guiName, "searchResults.label.no_results");
            }
            fontRenderer.drawString(resultsText, x + 5, y + 6, 0xFFEEEEEE);
        } else {
            super.draw(mouseX, mouseY, partialTickTime);
        }
    }
}
