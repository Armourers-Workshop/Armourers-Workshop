package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.util.ArrayList;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiControlSkinPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiControlSkinPanel.SkinIcon;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.common.library.global.DownloadUtils.DownloadJsonArrayMultipartForm;
import moe.plushie.armourers_workshop.common.library.global.MultipartForm;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelHome extends GuiPanel {
    
    private static final String BASE_URL = "http://plushie.moe/armourers_workshop/";
    private static final String RECENTLY_UPLOADED_URL = BASE_URL + "recently-uploaded.php";
    private static final String MOST_DOWNLOADED_URL = BASE_URL + "most-downloaded.php";
    private static final String MOST_LIKED_URL = BASE_URL + "most-liked.php";
    
    private final GuiControlSkinPanel skinPanelRecentlyUploaded;
    private final GuiControlSkinPanel skinPanelMostDownloaded;
    private final GuiControlSkinPanel skinPanelMostLiked;
    
    private FutureTask<JsonArray> taskDownloadJsonRecentlyUploaded;
    private FutureTask<JsonArray> taskDownloadJsonMostDownloaded;
    private FutureTask<JsonArray> taskDownloadJsonMostLiked;
    
    private GuiButtonExt buttonShowAll;
    
    public GuiGlobalLibraryPanelHome(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        skinPanelRecentlyUploaded = new GuiControlSkinPanel();
        skinPanelMostDownloaded = new GuiControlSkinPanel();
        skinPanelMostLiked = new GuiControlSkinPanel();
    }
    
    
    @Override
    public void initGui() {
        super.initGui();
        String guiName = ((GuiGlobalLibrary)parent).getGuiName();
        
        buttonList.clear();
        
        buttonShowAll = new GuiButtonExt(-1, x + 5, y + 5, 80, 20, GuiHelper.getLocalizedControlName(guiName, "home.showAllSkins"));
        
        int boxW = (width - 15) / 2;
        int boxH = height - 10 - 35;
        skinPanelRecentlyUploaded.init(x + 5, y + 5 + 35, boxW, boxH);
        skinPanelMostDownloaded.init(x + boxW + 10, y + 5 + 35, boxW, boxH / 2 - 10);
        skinPanelMostLiked.init(x + boxW + 10, y + 5 + 35 + boxH / 2 + 5, boxW, boxH / 2 - 5);
        
        skinPanelRecentlyUploaded.setIconSize(40);
        skinPanelMostDownloaded.setIconSize(40);
        skinPanelMostLiked.setIconSize(40);
        
        buttonList.add(buttonShowAll);
        buttonList.add(skinPanelRecentlyUploaded);
        buttonList.add(skinPanelMostDownloaded);
        buttonList.add(skinPanelMostLiked);
    }
    
    public void updateSkinPanels() {
        int iconCountRecentlyUploaded = skinPanelRecentlyUploaded.getIconCount();
        int iconCountMostDownloaded = skinPanelMostDownloaded.getIconCount();
        int iconCountMostLiked = skinPanelMostLiked.getIconCount();
        
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        String searchTypes = "";
        for (int i = 0; i < skinTypes.size(); i++) {
            searchTypes += (skinTypes.get(i).getRegistryName());
            if (i < skinTypes.size() - 1) {
                searchTypes += ";";
            }
        }
        MultipartForm multipartFormRecently = new MultipartForm(RECENTLY_UPLOADED_URL + "?limit=" + iconCountRecentlyUploaded + "&maxFileVersion=" + String.valueOf(SkinSerializer.MAX_FILE_VERSION));
        MultipartForm multipartFormMostDownloaded = new MultipartForm(MOST_DOWNLOADED_URL + "?limit=" + iconCountRecentlyUploaded + "&maxFileVersion=" + String.valueOf(SkinSerializer.MAX_FILE_VERSION));
        MultipartForm multipartFormMostLiked = new MultipartForm(MOST_LIKED_URL + "?limit=" + iconCountRecentlyUploaded + "&maxFileVersion=" + String.valueOf(SkinSerializer.MAX_FILE_VERSION));
        multipartFormRecently.addText("searchTypes", searchTypes);
        multipartFormMostDownloaded.addText("searchTypes", searchTypes);
        multipartFormMostLiked.addText("searchTypes", searchTypes);
        
        taskDownloadJsonRecentlyUploaded = new FutureTask<JsonArray>(new DownloadJsonArrayMultipartForm(multipartFormRecently));
        taskDownloadJsonMostDownloaded = new FutureTask<JsonArray>(new DownloadJsonArrayMultipartForm(multipartFormMostDownloaded));
        taskDownloadJsonMostLiked = new FutureTask<JsonArray>(new DownloadJsonArrayMultipartForm(multipartFormMostLiked));
        
        ((GuiGlobalLibrary)parent).jsonDownloadExecutor.execute(taskDownloadJsonRecentlyUploaded);
        ((GuiGlobalLibrary)parent).jsonDownloadExecutor.execute(taskDownloadJsonMostDownloaded);
        ((GuiGlobalLibrary)parent).jsonDownloadExecutor.execute(taskDownloadJsonMostLiked);
    }
    
    @Override
    public void update() {
        if (taskDownloadJsonRecentlyUploaded != null && taskDownloadJsonRecentlyUploaded.isDone()) {
            try {
                JsonArray jsonArray = taskDownloadJsonRecentlyUploaded.get();
                skinPanelRecentlyUploaded.clearIcons();
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject skinJson = jsonArray.get(i).getAsJsonObject();
                        skinPanelRecentlyUploaded.addIcon(skinJson);
                    }
                } else {
                	ModLogger.log(Level.WARN, "Failed to download.");
                }
                taskDownloadJsonRecentlyUploaded = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (taskDownloadJsonMostDownloaded != null && taskDownloadJsonMostDownloaded.isDone()) {
            try {
                JsonArray jsonArray = taskDownloadJsonMostDownloaded.get();
                skinPanelMostDownloaded.clearIcons();
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject skinJson = jsonArray.get(i).getAsJsonObject();
                        skinPanelMostDownloaded.addIcon(skinJson);
                    }
                } else {
                	ModLogger.log(Level.WARN, "Failed to download.");
                }
                taskDownloadJsonMostDownloaded = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (taskDownloadJsonMostLiked != null && taskDownloadJsonMostLiked.isDone()) {
            try {
                JsonArray jsonArray = taskDownloadJsonMostLiked.get();
                skinPanelMostLiked.clearIcons();
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject skinJson = jsonArray.get(i).getAsJsonObject();
                        skinPanelMostLiked.addIcon(skinJson);
                    }
                } else {
                	ModLogger.log(Level.WARN, "Failed to download.");
                }
                taskDownloadJsonMostLiked = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonShowAll) {
            ((GuiGlobalLibrary)parent).panelSearchResults.clearResults();
            ((GuiGlobalLibrary)parent).switchScreen(Screen.SEARCH);
            ((GuiGlobalLibrary)parent).panelSearchBox.selectedSkinType = null;
            ((GuiGlobalLibrary)parent).panelSearchBox.initGui();
            ((GuiGlobalLibrary)parent).panelSearchResults.doSearch("", null);
        }
        if (button == skinPanelRecentlyUploaded | button == skinPanelMostDownloaded | button == skinPanelMostLiked) {
            SkinIcon skinIcon = ((GuiControlSkinPanel)button).getLastPressedSkinIcon();
            if (skinIcon != null) {
                ((GuiGlobalLibrary)parent).panelSkinInfo.displaySkinInfo(skinIcon.getSkinJson(), Screen.HOME);
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
        
        String guiName = ((GuiGlobalLibrary)parent).getGuiName();
        String labelRecentlyUploaded = GuiHelper.getLocalizedControlName(guiName, "home.recentlyUploaded");
        String labelMostDownloaded = GuiHelper.getLocalizedControlName(guiName, "home.mostDownloaded");
        String labelMostLikes = GuiHelper.getLocalizedControlName(guiName, "home.mostLikes");
        
        int boxW = (width - 15) / 2;
        int boxH = height - 10 - 35;
        
        fontRenderer.drawString(labelRecentlyUploaded, x + 5, y + 30, 0xFFEEEEEE);
        fontRenderer.drawString(labelMostDownloaded, x + boxW + 10, y + 30, 0xFFEEEEEE);
        fontRenderer.drawString(labelMostLikes, x + boxW + 10, y + 30 + boxH / 2 + 5, 0xFFEEEEEE);
    }
}
