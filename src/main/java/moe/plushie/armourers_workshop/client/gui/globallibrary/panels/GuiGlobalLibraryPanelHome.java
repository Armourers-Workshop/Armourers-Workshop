package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.util.ArrayList;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiControlSkinPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiControlSkinPanel.SkinIcon;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiScrollbar;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskSkinSearch;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskSkinSearch.SearchColumnType;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskSkinSearch.SearchOrderType;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelHome extends GuiPanel {

    private final GuiScrollbar scrollbar;
    private final GuiControlSkinPanel skinPanelRecentlyUploaded;
    private final GuiControlSkinPanel skinPanelMostDownloaded;
    private final GuiControlSkinPanel skinPanelTopRated;
    private final GuiControlSkinPanel skinPanelNeedRated;

    private GuiButtonExt buttonShowAll;

    public GuiGlobalLibraryPanelHome(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        scrollbar = new GuiScrollbar(-1, width - 11, y + 1, 10, height - 2, "", false);
        scrollbar.setStyleFlat(true);
        scrollbar.setAmount(20);
        skinPanelRecentlyUploaded = new GuiControlSkinPanel();
        skinPanelMostDownloaded = new GuiControlSkinPanel();
        skinPanelTopRated = new GuiControlSkinPanel();
        skinPanelNeedRated = new GuiControlSkinPanel();
        insideCheck = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        String guiName = ((GuiGlobalLibrary) parent).getGuiName();

        buttonList.clear();

        scrollbar.y = y + 1;
        scrollbar.x = x + width - 11;
        scrollbar.height = height - 2;

        buttonShowAll = new GuiButtonExt(-1, x + 2, y + 2, 80, 16, GuiHelper.getLocalizedControlName(guiName, "home.showAllSkins"));

        skinPanelRecentlyUploaded.init(x + 2, y + 2 + 28, width - 20, 307);
        skinPanelMostDownloaded.init(x + 2, y + 2 + 28 + 206, width - 20, 307);
        skinPanelTopRated.init(x + 2, y + 2 + 28 + 600, width - 20, 307);
        skinPanelNeedRated.init(x + 2, y + 2 + 28 + 600, width - 20, 307);

        skinPanelRecentlyUploaded.setIconSize(50);
        skinPanelMostDownloaded.setIconSize(50);
        skinPanelTopRated.setIconSize(50);
        skinPanelNeedRated.setIconSize(50);

        int totalHeight = (307 + 14) * 4 + 28 + 2 * 2;
        totalHeight -= height;

        scrollbar.setSliderMaxValue(totalHeight);

        buttonList.add(scrollbar);
        buttonList.add(buttonShowAll);
        buttonList.add(skinPanelRecentlyUploaded);
        buttonList.add(skinPanelMostDownloaded);
        buttonList.add(skinPanelTopRated);
        buttonList.add(skinPanelNeedRated);
    }

    public void updateSkinPanels() {
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        String searchTypes = "";
        for (int i = 0; i < skinTypes.size(); i++) {
            searchTypes += (skinTypes.get(i).getRegistryName());
            if (i < skinTypes.size() - 1) {
                searchTypes += ";";
            }
        }

        GlobalTaskSkinSearch taskGetRecentlyUploaded = new GlobalTaskSkinSearch("", searchTypes, 0, skinPanelRecentlyUploaded.getIconCount());
        taskGetRecentlyUploaded.setSearchOrderColumn(SearchColumnType.DATE_CREATED);
        taskGetRecentlyUploaded.setSearchOrder(SearchOrderType.DESC);
        taskGetRecentlyUploaded.createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        skinPanelRecentlyUploaded.clearIcons();
                        if (result.has("results")) {
                            JsonArray pageResults = result.get("results").getAsJsonArray();
                            for (int i = 0; i < pageResults.size(); i++) {
                                JsonObject skinJson = pageResults.get(i).getAsJsonObject();
                                skinPanelRecentlyUploaded.addIcon(skinJson);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                // NO-OP
            }
        });

        GlobalTaskSkinSearch taskGetMostDownloaded = new GlobalTaskSkinSearch("", searchTypes, 0, skinPanelMostDownloaded.getIconCount());
        taskGetMostDownloaded.setSearchOrderColumn(SearchColumnType.DOWNLOADS);
        taskGetMostDownloaded.setSearchOrder(SearchOrderType.DESC);
        taskGetMostDownloaded.createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        skinPanelMostDownloaded.clearIcons();
                        if (result.has("results")) {
                            JsonArray pageResults = result.get("results").getAsJsonArray();
                            for (int i = 0; i < pageResults.size(); i++) {
                                JsonObject skinJson = pageResults.get(i).getAsJsonObject();
                                skinPanelMostDownloaded.addIcon(skinJson);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                // NO-OP
            }
        });

        GlobalTaskSkinSearch taskGetTopRated = new GlobalTaskSkinSearch("", searchTypes, 0, skinPanelTopRated.getIconCount());
        taskGetTopRated.setSearchOrderColumn(SearchColumnType.RATING);
        taskGetTopRated.setSearchOrder(SearchOrderType.DESC);
        taskGetTopRated.createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        skinPanelTopRated.clearIcons();
                        if (result.has("results")) {
                            JsonArray pageResults = result.get("results").getAsJsonArray();
                            for (int i = 0; i < pageResults.size(); i++) {
                                JsonObject skinJson = pageResults.get(i).getAsJsonObject();
                                skinPanelTopRated.addIcon(skinJson);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                // NO-OP
            }
        });

        GlobalTaskSkinSearch taskNeedRated = new GlobalTaskSkinSearch("", searchTypes, 0, skinPanelNeedRated.getIconCount());
        taskNeedRated.setSearchOrderColumn(SearchColumnType.RATING_COUNT);
        taskNeedRated.setSearchOrder(SearchOrderType.ASC);
        taskNeedRated.createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        skinPanelNeedRated.clearIcons();
                        if (result.has("results")) {
                            JsonArray pageResults = result.get("results").getAsJsonArray();
                            for (int i = 0; i < pageResults.size(); i++) {
                                JsonObject skinJson = pageResults.get(i).getAsJsonObject();
                                skinPanelNeedRated.addIcon(skinJson);
                            }
                        }
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
        if (button == buttonShowAll) {
            ((GuiGlobalLibrary) parent).panelSearchResults.clearResults();
            ((GuiGlobalLibrary) parent).switchScreen(Screen.SEARCH);
            ((GuiGlobalLibrary) parent).panelSearchBox.selectedSkinType = null;
            ((GuiGlobalLibrary) parent).panelSearchBox.initGui();
            ((GuiGlobalLibrary) parent).panelSearchResults.doSearch("", null, SearchColumnType.DATE_CREATED, SearchOrderType.DESC);
        }
        if (button == skinPanelRecentlyUploaded | button == skinPanelMostDownloaded | button == skinPanelTopRated | button == skinPanelNeedRated) {
            SkinIcon skinIcon = ((GuiControlSkinPanel) button).getLastPressedSkinIcon();
            if (skinIcon != null) {
                ((GuiGlobalLibrary) parent).panelSkinInfo.displaySkinInfo(skinIcon.getSkinJson(), Screen.HOME);
            }
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        ModRenderHelper.enableScissor(x, y, width, height, true);

        int amount = scrollbar.getValue();
        buttonShowAll.y = y + 2 - amount;
        skinPanelRecentlyUploaded.y = y + 2 + 28 - amount;
        skinPanelTopRated.y = y + 2 + 28 + 307 * 1 + 14 * 1 - amount;
        skinPanelNeedRated.y = y + 2 + 28 + 307 * 2 + 14 * 2 - amount;
        skinPanelMostDownloaded.y = y + 2 + 28 + 307 * 3 + 14 * 3 - amount;

        super.draw(mouseX, mouseY, partialTickTime);

        String guiName = ((GuiGlobalLibrary) parent).getGuiName();
        String labelRecentlyUploaded = GuiHelper.getLocalizedControlName(guiName, "home.recentlyUploaded");
        String labelMostDownloaded = GuiHelper.getLocalizedControlName(guiName, "home.mostDownloaded");
        String labelTopRated = GuiHelper.getLocalizedControlName(guiName, "home.topRated");
        String labelNeedRated = GuiHelper.getLocalizedControlName(guiName, "home.needRated");

        int boxW = (width - 15) / 2;
        int boxH = height - 10 - 35;

        fontRenderer.drawString(labelRecentlyUploaded, skinPanelRecentlyUploaded.x + 2, skinPanelRecentlyUploaded.y - 9, 0xFFEEEEEE);
        fontRenderer.drawString(labelMostDownloaded, skinPanelMostDownloaded.x + 2, skinPanelMostDownloaded.y - 9, 0xFFEEEEEE);
        fontRenderer.drawString(labelTopRated, skinPanelTopRated.x + 2, skinPanelTopRated.y - 9, 0xFFEEEEEE);
        fontRenderer.drawString(labelNeedRated, skinPanelNeedRated.x + 2, skinPanelNeedRated.y - 9, 0xFFEEEEEE);

        ModRenderHelper.disableScissor();
    }
}
