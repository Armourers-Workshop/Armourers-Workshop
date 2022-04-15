package moe.plushie.armourers_workshop.library.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.gui.widget.SkinFileList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.ArrayList;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class GlobalLibraryHomePanel extends GlobalLibraryAbstractPanel {

    //    private final GuiScrollbar scrollbar;
    private final SkinFileList skinPanelRecentlyUploaded = buildFileList(0, 0, 300, 307);
    private final SkinFileList skinPanelMostDownloaded = buildFileList(0, 0, 300, 307);
    private final SkinFileList skinPanelTopRated = buildFileList(0, 0, 300, 307);
    private final SkinFileList skinPanelNeedRated = buildFileList(0, 0, 300, 307);

    private int contentHeight = 0;
    private int lastContentOffset = 0;
    private int scrollAmount = 20;

    public GlobalLibraryHomePanel() {
        super("inventory.armourers_workshop.skin-library-global.home", GlobalSkinLibraryScreen.Page.HOME::equals);
    }

    @Override
    protected void init() {
        super.init();

        this.addButton(skinPanelRecentlyUploaded);
        this.addButton(skinPanelTopRated);
        this.addButton(skinPanelNeedRated);
        this.addButton(skinPanelMostDownloaded);

        int listTop = topPos + 20;
        int listLeft = leftPos + 4;
        for (IGuiEventListener item : children) {
            if (item instanceof SkinFileList) {
                SkinFileList fileList = (SkinFileList) item;
                fileList.setFrame(listLeft, listTop + 20, width, 305);
                fileList.setItemSize(new Size2i(50, 50));
                fileList.setBackgroundColor(0);
                fileList.setShowsName(false);
                fileList.reloadData();
                listTop = fileList.y + fileList.getHeight();
            }
        }

        this.addTitle(skinPanelRecentlyUploaded, "recentlyUploaded");
        this.addTitle(skinPanelMostDownloaded, "mostDownloaded");
        this.addTitle(skinPanelTopRated, "topRated");
        this.addTitle(skinPanelNeedRated, "needRated");

        this.addButton(new ExtendedButton(leftPos + 4, topPos + 6, 80, 16, getDisplayText("showAllSkins"), this::showAll));

        this.lastContentOffset = 0;
        this.contentHeight = listTop - topPos + 4;
    }

    public void reloadData() {
        int requestSize = skinPanelRecentlyUploaded.getTotalCount() + 1;
        StringBuilder searchTypes = new StringBuilder();
        for (ISkinType skinType : SkinTypes.values()) {
            ResourceLocation registryName = skinType.getRegistryName();
            if (skinType != SkinTypes.UNKNOWN && registryName != null) {
                if (searchTypes.length() != 0) {
                    searchTypes.append(";");
                }
                searchTypes.append(registryName);
            }
        }

        GlobalTaskSkinSearch taskGetRecentlyUploaded = new GlobalTaskSkinSearch("", searchTypes.toString(), 0, requestSize);
        taskGetRecentlyUploaded.setSearchOrderColumn(GlobalTaskSkinSearch.SearchColumnType.DATE_CREATED);
        taskGetRecentlyUploaded.setSearchOrder(GlobalTaskSkinSearch.SearchOrderType.DESC);
        taskGetRecentlyUploaded.createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                ArrayList<SkinFileList.Entry> entries = buildEntries(result);
                Minecraft.getInstance().execute(() -> {
                    skinPanelRecentlyUploaded.setEntries(entries);
                    skinPanelRecentlyUploaded.reloadData();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                // NO-OP
            }
        });

        GlobalTaskSkinSearch taskGetMostDownloaded = new GlobalTaskSkinSearch("", searchTypes.toString(), 0, requestSize);
        taskGetMostDownloaded.setSearchOrderColumn(GlobalTaskSkinSearch.SearchColumnType.DOWNLOADS);
        taskGetMostDownloaded.setSearchOrder(GlobalTaskSkinSearch.SearchOrderType.DESC);
        taskGetMostDownloaded.createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                ArrayList<SkinFileList.Entry> entries = buildEntries(result);
                Minecraft.getInstance().execute(() -> {
                    skinPanelMostDownloaded.setEntries(entries);
                    skinPanelMostDownloaded.reloadData();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                // NO-OP
            }
        });

        GlobalTaskSkinSearch taskGetTopRated = new GlobalTaskSkinSearch("", searchTypes.toString(), 0, requestSize);
        taskGetTopRated.setSearchOrderColumn(GlobalTaskSkinSearch.SearchColumnType.RATING);
        taskGetTopRated.setSearchOrder(GlobalTaskSkinSearch.SearchOrderType.DESC);
        taskGetTopRated.createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                ArrayList<SkinFileList.Entry> entries = buildEntries(result);
                Minecraft.getInstance().execute(() -> {
                    skinPanelTopRated.setEntries(entries);
                    skinPanelTopRated.reloadData();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                // NO-OP
            }
        });

        GlobalTaskSkinSearch taskNeedRated = new GlobalTaskSkinSearch("", searchTypes.toString(), 0, requestSize);
        taskNeedRated.setSearchOrderColumn(GlobalTaskSkinSearch.SearchColumnType.RATING_COUNT);
        taskNeedRated.setSearchOrder(GlobalTaskSkinSearch.SearchOrderType.ASC);
        taskNeedRated.createTaskAndRun(new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                ArrayList<SkinFileList.Entry> entries = buildEntries(result);
                Minecraft.getInstance().execute(() -> {
                    skinPanelNeedRated.setEntries(entries);
                    skinPanelNeedRated.reloadData();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                // NO-OP
            }
        });
    }

    private AWLabel addTitle(SkinFileList list, String titleKey) {
        return addLabel(list.x + 1, list.y - 11, list.getWidth(), 16, getDisplayText(titleKey));
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public void renderBackgroundLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int offset = scrollAmount - lastContentOffset;
        if (offset != 0) {
            this.lastContentOffset = scrollAmount;
            this.apply(w -> w.y -= offset);
        }
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        RenderUtils.enableScissor(leftPos, topPos, width, height);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderUtils.disableScissor();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // all content will render on the background layer
    }

    public int getMaxScroll() {
        return Math.max(contentHeight - height, 0);
    }

    public int getScrollAmount() {
        return scrollAmount;
    }

    public void setScrollAmount(int scrollAmount) {
        this.scrollAmount = MathHelper.clamp(scrollAmount, 0, this.getMaxScroll());
    }

    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
        this.setScrollAmount(this.getScrollAmount() - (int) (p_231043_5_ * height / 4));
        return true;
    }

    private void apply(Consumer<Widget> consumer) {
        for (IGuiEventListener item : children) {
            if (item instanceof Widget) {
                consumer.accept((Widget) item);
            }
        }
    }

    private void showAll(Button sender) {
        router.showSkinList("", SkinTypes.UNKNOWN, GlobalTaskSkinSearch.SearchColumnType.DATE_CREATED, GlobalTaskSkinSearch.SearchOrderType.DESC);
    }

    private void showSkinInfo(SkinFileList.Entry sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryScreen.Page.HOME);
    }

    private ArrayList<SkinFileList.Entry> buildEntries(JsonObject result) {
        ArrayList<SkinFileList.Entry> entries = new ArrayList<>();
        if (result.has("results")) {
            JsonArray pageResults = result.get("results").getAsJsonArray();
            for (int i = 0; i < pageResults.size(); i++) {
                JsonObject skinJson = pageResults.get(i).getAsJsonObject();
                entries.add(new SkinFileList.Entry(skinJson));
            }
        }
        return entries;
    }

    public SkinFileList buildFileList(int x, int y, int width, int height) {
        SkinFileList fileList = new SkinFileList(x, y, width, height);
        fileList.setItemSize(new Size2i(50, 50));
        fileList.setBackgroundColor(0);
        fileList.setShowsName(false);
        fileList.setItemSelector(this::showSkinInfo);
        return fileList;
    }
}
