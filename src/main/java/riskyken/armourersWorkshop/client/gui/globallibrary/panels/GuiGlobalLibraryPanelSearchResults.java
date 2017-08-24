package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
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
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.TranslateUtils;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSearchResults extends GuiPanel {
    
    private static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/globalLibrary.png");
    protected static int iconScale = 110;
    
    protected final CompletionService<Skin> skinCompletion;
    protected final GuiControlSkinPanel skinPanelResults;
    
    protected GuiIconButton iconButtonSmall;
    protected GuiIconButton iconButtonMedium;
    protected GuiIconButton iconButtonLarge;
    
    private FutureTask<JsonArray> downloadSearchResultsTask;
    protected JsonArray json = null;
    protected int page = 0;
    
    public GuiGlobalLibraryPanelSearchResults(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        skinCompletion = new ExecutorCompletionService<Skin>(((GuiGlobalLibrary)parent).skinDownloadExecutor);
        skinPanelResults = new GuiControlSkinPanel();
    }
    
    public void setDownloadSearchResultsTask(FutureTask<JsonArray> downloadSearchResultsTask) {
        this.downloadSearchResultsTask = downloadSearchResultsTask;
        clearResults();
        skinPanelResults.clearIcons();
    }
    
    @Override
    public void update() {
        if (downloadSearchResultsTask != null && downloadSearchResultsTask.isDone()) {
            try {
                json = null;
                json = downloadSearchResultsTask.get();
                changePage(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            downloadSearchResultsTask = null;
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
    
    public void clearResults() {
        json = null;
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
        iconButtonSmall.setIconLocation(34, 0, 16, 16);
        
        iconButtonMedium = new GuiIconButton(parent, 0, x + width - 21 * 2, y + 5, 16, 16, GuiHelper.getLocalizedControlName(guiName, "searchResults.medium"), BUTTON_TEXTURES);
        iconButtonMedium.setIconLocation(34, 17, 16, 16);
        
        iconButtonLarge = new GuiIconButton(parent, 0, x + width - 21, y + 5, 16, 16, GuiHelper.getLocalizedControlName(guiName, "searchResults.large"), BUTTON_TEXTURES);
        iconButtonLarge.setIconLocation(34, 34, 16, 16);
        
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
                ((GuiGlobalLibrary)parent).panelSkinInfo.displaySkinInfo(skinIcon.getSkinJson(), Screen.SEARCH);
            }
        }
    }
    
    protected void changePage(int page) {
        if (page < getMaxPages() & page >= 0) {
            this.page = page;
        }
        if (this.page > getMaxPages()) {
            this.page = getMaxPages() - 1;
        }
        if (this.page < 0) {
            this.page = 0;
        }
        updateSkinForPage();
    }
    
    protected void updateSkinForPage() {
        if (json != null) {
            int skinsPerPage = skinPanelResults.getIconCount();
            int pageOffset = skinsPerPage * page;
            skinPanelResults.clearIcons();
            JsonArray downloadArray = new JsonArray();
            for (int i = 0; i < skinsPerPage; i++) {
                int index = i + pageOffset;
                if (index < json.size()) {
                    JsonObject skinJson = json.get(index).getAsJsonObject();
                    downloadArray.add(skinJson);
                    skinPanelResults.addIcon(skinJson);
                }
            }
            SkinDownloader.downloadSkins(skinCompletion, downloadArray);
        }
    }
    
    protected int getMaxPages() {
        return MathHelper.ceiling_float_int((float)getNumberOfSkin() / (float)skinPanelResults.getIconCount());
    }
    
    protected int getNumberOfSkin() {
        if (json == null) {
            return 0;
        }
        return json.size();
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (this.getClass().equals(GuiGlobalLibraryPanelSearchResults.class)) {
            if (!visible) {
                return;
            }
            drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
            super.draw(mouseX, mouseY, partialTickTime);
            
            int maxPages = getMaxPages();
            int totalSkins = getNumberOfSkin();
            
            String guiName = ((GuiGlobalLibrary)parent).getGuiName();
            String unlocalizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + "." + "searchResults.results";
            
            String resultsText = TranslateUtils.translate(unlocalizedName, page + 1, maxPages, totalSkins);
            if (json == null) {
                resultsText = GuiHelper.getLocalizedControlName(guiName, "searchResults.label.searching");
            }
            fontRenderer.drawString(resultsText, x + 5, y + 6, 0xFFEEEEEE);
        } else {
            super.draw(mouseX, mouseY, partialTickTime);
        }
    }
}
