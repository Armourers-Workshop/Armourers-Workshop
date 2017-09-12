package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.util.concurrent.CompletionService;
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
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiControlSkinPanel;
import riskyken.armourersWorkshop.client.gui.controls.GuiControlSkinPanel.SkinIcon;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.library.global.DownloadUtils.DownloadJsonCallable;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelHome extends GuiPanel {
    
    private static final String BASE_URL = "http://plushie.moe/armourers_workshop/";
    private static final String RECENTLY_UPLOADED_URL = BASE_URL + "recently-uploaded.php";
    private static final String MOST_DOWNLOADED_URL = BASE_URL + "most-downloaded.php";
    
    private final GuiControlSkinPanel skinPanelRecentlyUploaded;
    private final GuiControlSkinPanel skinPanelMostDownloaded;
    
    private FutureTask<JsonArray> taskDownloadJsonRecentlyUploaded;
    private FutureTask<JsonArray> taskDownloadJsonMostDownloaded;
    private CompletionService<Skin> skinCompletion;
    
    private GuiButtonExt buttonShowAll;
    
    public GuiGlobalLibraryPanelHome(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        skinCompletion = new ExecutorCompletionService<Skin>(((GuiGlobalLibrary)parent).skinDownloadExecutor);
        skinPanelRecentlyUploaded = new GuiControlSkinPanel();
        skinPanelMostDownloaded = new GuiControlSkinPanel();
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
        skinPanelMostDownloaded.init(x + boxW + 10, y + 5 + 35, boxW, boxH);
        
        skinPanelRecentlyUploaded.setIconSize(40);
        skinPanelMostDownloaded.setIconSize(40);
        
        buttonList.add(buttonShowAll);
        buttonList.add(skinPanelRecentlyUploaded);
        buttonList.add(skinPanelMostDownloaded);
    }
    
    public void updateSkinPanels() {
        int iconCountRecentlyUploaded = (int) Math.ceil((float)skinPanelRecentlyUploaded.getIconCount() / 20) * 20;
        int iconCountMostDownloaded = (int) Math.ceil((float)skinPanelMostDownloaded.getIconCount() / 20) * 20;
        taskDownloadJsonRecentlyUploaded = new FutureTask<JsonArray>(new DownloadJsonCallable(RECENTLY_UPLOADED_URL + "?limit=" + iconCountRecentlyUploaded + "&maxFileVersion=" + String.valueOf(Skin.FILE_VERSION)));
        taskDownloadJsonMostDownloaded = new FutureTask<JsonArray>(new DownloadJsonCallable(MOST_DOWNLOADED_URL + "?limit=" + iconCountMostDownloaded + "&maxFileVersion=" + String.valueOf(Skin.FILE_VERSION)));
        
        ((GuiGlobalLibrary)parent).jsonDownloadExecutor.execute(taskDownloadJsonRecentlyUploaded);
        ((GuiGlobalLibrary)parent).jsonDownloadExecutor.execute(taskDownloadJsonMostDownloaded);
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
                    SkinDownloader.downloadSkins(skinCompletion, jsonArray);
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
                    SkinDownloader.downloadSkins(skinCompletion, jsonArray);
                }
                taskDownloadJsonMostDownloaded = null;
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
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonShowAll) {
            ((GuiGlobalLibrary)parent).panelSearchResults.clearResults();
            ((GuiGlobalLibrary)parent).switchScreen(Screen.SEARCH);
            ((GuiGlobalLibrary)parent).panelSearchResults.doSearch("");
        }
        if (button == skinPanelRecentlyUploaded | button == skinPanelMostDownloaded) {
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
        
        int boxW = (width - 15) / 2;
        
        fontRenderer.drawString(labelRecentlyUploaded, x + 5, y + 30, 0xFFEEEEEE);
        fontRenderer.drawString(labelMostDownloaded, x + boxW + 10, y + 30, 0xFFEEEEEE);
    }
}
