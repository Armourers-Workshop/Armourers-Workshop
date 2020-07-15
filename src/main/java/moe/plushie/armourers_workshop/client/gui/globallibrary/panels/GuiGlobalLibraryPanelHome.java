package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.util.ArrayList;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFutureTask;
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
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskGetMostDownloaded;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskGetMostLiked;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskGetRecentlyUploaded;
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
    private final GuiControlSkinPanel skinPanelMostLiked;

    private GuiButtonExt buttonShowAll;

    public GuiGlobalLibraryPanelHome(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        scrollbar = new GuiScrollbar(-1, width - 11, y + 1, 10, height - 2, "", false);
        scrollbar.setStyleFlat(true);
        //scrollbar.setSliderMaxValue(600);
        scrollbar.setAmount(14);
        skinPanelRecentlyUploaded = new GuiControlSkinPanel();
        skinPanelMostDownloaded = new GuiControlSkinPanel();
        skinPanelMostLiked = new GuiControlSkinPanel();
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
        
        skinPanelRecentlyUploaded.init(x + 2, y + 2 + 28, width - 20, 206);
        skinPanelMostDownloaded.init(x + 2, y + 2 + 28 + 206, width - 20, 206);
        skinPanelMostLiked.init(x + 2, y + 2 + 28 + 600, width - 20, 206);

        skinPanelRecentlyUploaded.setIconSize(40);
        skinPanelMostDownloaded.setIconSize(40);
        skinPanelMostLiked.setIconSize(40);

        int totalHeight = (206 + 14) * 3 + 28 + 2 * 2;
        totalHeight -= height;
        
        scrollbar.setSliderMaxValue(totalHeight);
        
        buttonList.add(scrollbar);
        buttonList.add(buttonShowAll);
        buttonList.add(skinPanelRecentlyUploaded);
        buttonList.add(skinPanelMostDownloaded);
        buttonList.add(skinPanelMostLiked);
    }

    public void updateSkinPanels() {
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

        GlobalTaskGetRecentlyUploaded taskGetRecentlyUploaded = new GlobalTaskGetRecentlyUploaded(skinPanelRecentlyUploaded.getIconCount(), searchTypes);
        ListenableFutureTask<JsonArray> futureGetRecentlyUploaded = taskGetRecentlyUploaded.createTaskAndRun(new FutureCallback<JsonArray>() {

            @Override
            public void onSuccess(JsonArray result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        skinPanelRecentlyUploaded.clearIcons();
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject skinJson = result.get(i).getAsJsonObject();
                            skinPanelRecentlyUploaded.addIcon(skinJson);
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

        GlobalTaskGetMostDownloaded taskGetMostDownloaded = new GlobalTaskGetMostDownloaded(skinPanelMostDownloaded.getIconCount(), searchTypes);
        ListenableFutureTask<JsonArray> futureGetMostDownloaded = taskGetMostDownloaded.createTaskAndRun(new FutureCallback<JsonArray>() {

            @Override
            public void onSuccess(JsonArray result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        skinPanelMostDownloaded.clearIcons();
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject skinJson = result.get(i).getAsJsonObject();
                            skinPanelMostDownloaded.addIcon(skinJson);
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

        GlobalTaskGetMostLiked taskGetMostLiked = new GlobalTaskGetMostLiked(skinPanelMostLiked.getIconCount(), searchTypes);
        ListenableFutureTask<JsonArray> futureGetMostLiked = taskGetMostLiked.createTaskAndRun(new FutureCallback<JsonArray>() {

            @Override
            public void onSuccess(JsonArray result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        skinPanelMostLiked.clearIcons();
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject skinJson = result.get(i).getAsJsonObject();
                            skinPanelMostLiked.addIcon(skinJson);
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
            ((GuiGlobalLibrary) parent).panelSearchResults.doSearch("", null);
        }
        if (button == skinPanelRecentlyUploaded | button == skinPanelMostDownloaded | button == skinPanelMostLiked) {
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
        ModRenderHelper.enableScissorScaled(x, y, width, height);
        
        int amount = scrollbar.getValue();
        buttonShowAll.y = y + 2 - amount;
        skinPanelRecentlyUploaded.y = y + 2 + 28 - amount;
        skinPanelMostDownloaded.y = y + 2 + 28 + 206 + 14 - amount;
        skinPanelMostLiked.y = y + 2 + 28  + 206 * 2 + 14 * 2 - amount;
        
        
        super.draw(mouseX, mouseY, partialTickTime);
        

        
        String guiName = ((GuiGlobalLibrary) parent).getGuiName();
        String labelRecentlyUploaded = GuiHelper.getLocalizedControlName(guiName, "home.recentlyUploaded");
        String labelMostDownloaded = GuiHelper.getLocalizedControlName(guiName, "home.mostDownloaded");
        String labelMostLikes = GuiHelper.getLocalizedControlName(guiName, "home.mostLikes");

        int boxW = (width - 15) / 2;
        int boxH = height - 10 - 35;

        fontRenderer.drawString(labelRecentlyUploaded, x + 5, y + 20 - amount, 0xFFEEEEEE);
        fontRenderer.drawString(labelMostDownloaded, x + 5, y + 20 + 220 - amount, 0xFFEEEEEE);
        fontRenderer.drawString(labelMostLikes, x + 5, y + 20 + 220 * 2 - amount, 0xFFEEEEEE);
        ModRenderHelper.disableScissor();
    }
}
