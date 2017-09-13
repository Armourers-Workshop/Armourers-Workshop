package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiControlSkinPanel;
import riskyken.armourersWorkshop.client.gui.controls.GuiControlSkinPanel.SkinIcon;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.global.DownloadUtils.DownloadJsonObjectCallable;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.TranslateUtils;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSearchResults extends GuiPanel {
    
    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/globalLibrary.png");
    protected static final String BASE_URL = "http://plushie.moe/armourers_workshop/";
    private static final String SEARCH_URL = BASE_URL + "skin-search-page.php";
    
    protected final CompletionService<Skin> skinCompletion;
    protected final CompletionService<JsonObject> pageCompletion;
    
    protected final GuiControlSkinPanel skinPanelResults;
    
    protected GuiIconButton iconButtonSmall;
    protected GuiIconButton iconButtonMedium;
    protected GuiIconButton iconButtonLarge;
    
    protected static int iconScale = 110;
    
    protected String search = null;
    protected JsonArray[] pageList;
    protected HashSet<Integer> downloadedPageList;
    protected JsonArray jsonCurrentPage;
    protected int currentPageIndex = 0;
    protected int totalPages = 0;
    protected int totalResults = 0;
    
    public GuiGlobalLibraryPanelSearchResults(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        skinCompletion = new ExecutorCompletionService<Skin>(((GuiGlobalLibrary)parent).skinDownloadExecutor);
        pageCompletion = new ExecutorCompletionService<JsonObject>(((GuiGlobalLibrary)parent).jsonDownloadExecutor);
        skinPanelResults = new GuiControlSkinPanel();
        pageList = null;
        downloadedPageList = new HashSet<Integer>();
    }
    
    public void doSearch(String search) {
        clearResults();
        this.search = search;
        if (this.search == null) {
            return;
        }
        fetchPage(0);
    }
    
    public void clearResults() {
        search = null;
        pageList = null;
        jsonCurrentPage = null;
        currentPageIndex = 0;
        totalPages = 0;
        totalResults = 0;
        skinPanelResults.clearIcons();
        downloadedPageList.clear();
    }

    protected void resize() {
        String thisSearch = search;
        int thisPage = currentPageIndex;
        clearResults();
        doSearch(thisSearch);
    }
    
    protected void fetchPage(int pageIndex) {
        if (!downloadedPageList.contains(pageIndex)) {
            downloadedPageList.add(pageIndex);
        } else {
            return;
        }
        try {
            String searchUrl = SEARCH_URL;
            searchUrl += "?search=" + URLEncoder.encode(search, "UTF-8");
            searchUrl += "&maxFileVersion=" + String.valueOf(Skin.FILE_VERSION);
            searchUrl += "&pageIndex=" + String.valueOf(pageIndex);
            searchUrl += "&pageSize=" + String.valueOf(skinPanelResults.getIconCount());
            pageCompletion.submit(new DownloadJsonObjectCallable(searchUrl));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update() {
        Future<JsonObject> futureJson = pageCompletion.poll();
        if (futureJson!= null) {
            try {
                JsonObject pageJson = futureJson.get();
                if (pageJson != null) {
                    onPageJsonDownload(pageJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Future<Skin> futureSkin = skinCompletion.poll();
        if (futureSkin != null) {
            try {
                Skin skin = futureSkin.get();
                if (skin != null) {
                    SkinPointer skinPointer = new SkinPointer(skin);
                    if (skin != null && !ClientSkinCache.INSTANCE.isSkinInCache(skinPointer)) {
                        ModelBakery.INSTANCE.receivedUnbakedModel(skin);
                    } else {
                        if (skin != null) {
                            ClientSkinCache.INSTANCE.addServerIdMap(skin);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void onPageJsonDownload(JsonObject pageJson) {
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
            pageList[pageIndex] = pageResults;
            updateSkinForPage();
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
        
        String guiName = ((GuiGlobalLibrary)parent).getGuiName();
        buttonList.clear();
        
        skinPanelResults.init(x + 5, y + 24, width - 10, height - 52);
        skinPanelResults.setIconSize(iconScale);
        skinPanelResults.setPanelPadding(0);
        skinPanelResults.setShowName(true);
        
        iconButtonSmall = new GuiIconButton(parent, 0, x + width - 21 * 3, y + 5, 16, 16, GuiHelper.getLocalizedControlName(guiName, "searchResults.small"), BUTTON_TEXTURES);
        iconButtonSmall.setIconLocation(51, 0, 16, 16);
        
        iconButtonMedium = new GuiIconButton(parent, 0, x + width - 21 * 2, y + 5, 16, 16, GuiHelper.getLocalizedControlName(guiName, "searchResults.medium"), BUTTON_TEXTURES);
        iconButtonMedium.setIconLocation(51, 17, 16, 16);
        
        iconButtonLarge = new GuiIconButton(parent, 0, x + width - 21, y + 5, 16, 16, GuiHelper.getLocalizedControlName(guiName, "searchResults.large"), BUTTON_TEXTURES);
        iconButtonLarge.setIconLocation(51, 34, 16, 16);
        
        buttonList.add(iconButtonSmall);
        buttonList.add(iconButtonMedium);
        buttonList.add(iconButtonLarge);
        buttonList.add(skinPanelResults);
        
        buttonList.add(new GuiButtonExt(1, x + 5, y + height - 25, 80, 20, "<<"));
        buttonList.add(new GuiButtonExt(2, x + width - 85, y + height - 25, 80, 20, ">>"));
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
            SkinIcon skinIcon = ((GuiControlSkinPanel)button).getLastPressedSkinIcon();
            if (skinIcon != null) {
                ((GuiGlobalLibrary)parent).panelSkinInfo.displaySkinInfo(skinIcon.getSkinJson(), Screen.SEARCH);
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
            SkinDownloader.downloadSkins(skinCompletion, downloadArray);
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
            
            String guiName = ((GuiGlobalLibrary)parent).getGuiName();
            String unlocalizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + "." + "searchResults.results";
            
            String resultsText = TranslateUtils.translate(unlocalizedName, currentPageIndex + 1, maxPages, totalSkins);
            if (jsonCurrentPage == null) {
                resultsText = GuiHelper.getLocalizedControlName(guiName, "searchResults.label.searching");
            }
            fontRenderer.drawString(resultsText, x + 5, y + 6, 0xFFEEEEEE);
        } else {
            super.draw(mouseX, mouseY, partialTickTime);
        }
    }
}
