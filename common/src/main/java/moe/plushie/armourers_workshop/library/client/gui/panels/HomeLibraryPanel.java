package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWExtendedButton;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinFileList;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchColumnType;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchOrderType;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.math.Size2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

@SuppressWarnings("NullableProblems")
@Environment(value = EnvType.CLIENT)
public class HomeLibraryPanel extends AbstractLibraryPanel implements GlobalSkinLibraryScreen.ISkinListListener {

    //    private final GuiScrollbar scrollbar;
    private final SkinFileList skinPanelRecentlyUploaded = buildFileList(0, 0, 300, 307);
    private final SkinFileList skinPanelMostDownloaded = buildFileList(0, 0, 300, 307);
    private final SkinFileList skinPanelTopRated = buildFileList(0, 0, 300, 307);
    private final SkinFileList skinPanelNeedRated = buildFileList(0, 0, 300, 307);

    private int contentHeight = 0;
    private int scrollAmount = 20;
    private int lastContentOffset = 0;
    private int lastRequestSize = 0;

    public HomeLibraryPanel() {
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
        for (GuiEventListener item : children) {
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

        this.addButton(new AWExtendedButton(leftPos + 4, topPos + 6, 80, 16, getDisplayText("showAllSkins"), this::showAll));

        this.lastContentOffset = 0;
        this.contentHeight = listTop - topPos + 4;

        int pageSize = this.skinPanelRecentlyUploaded.getTotalCount();
        if (lastRequestSize > 0 && lastRequestSize < pageSize) {
            this.reloadData();
        }
    }

    public void reloadData() {
        int requestSize = skinPanelRecentlyUploaded.getTotalCount();
        lastRequestSize = requestSize;
        ModLog.debug("refresh home skin list, page size: {}", lastRequestSize);
        String searchTypes = GlobalSkinLibraryUtils.allSearchTypes();

        GlobalTaskSkinSearch taskGetRecentlyUploaded = new GlobalTaskSkinSearch("", searchTypes, 0, requestSize);
        taskGetRecentlyUploaded.setSearchOrderColumn(SearchColumnType.DATE_CREATED);
        taskGetRecentlyUploaded.setSearchOrder(SearchOrderType.DESC);
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

        GlobalTaskSkinSearch taskGetMostDownloaded = new GlobalTaskSkinSearch("", searchTypes, 0, requestSize);
        taskGetMostDownloaded.setSearchOrderColumn(SearchColumnType.DOWNLOADS);
        taskGetMostDownloaded.setSearchOrder(SearchOrderType.DESC);
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

        GlobalTaskSkinSearch taskGetTopRated = new GlobalTaskSkinSearch("", searchTypes, 0, requestSize);
        taskGetTopRated.setSearchOrderColumn(SearchColumnType.RATING);
        taskGetTopRated.setSearchOrder(SearchOrderType.DESC);
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

        GlobalTaskSkinSearch taskNeedRated = new GlobalTaskSkinSearch("", searchTypes, 0, requestSize);
        taskNeedRated.setSearchOrderColumn(SearchColumnType.RATING_COUNT);
        taskNeedRated.setSearchOrder(SearchOrderType.ASC);
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

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public void renderBackgroundLayer(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // all content will render on the background layer
    }

    @Override
    public void skinDidChange(int skinId, @Nullable SkinFileList.Entry newValue) {
        // only update for remove
        if (newValue != null) {
            return;
        }
        for (GuiEventListener listener : children) {
            if (listener instanceof SkinFileList) {
                if (indexOf((SkinFileList) listener, skinId) != -1) {
                    // removed skin in here
                    reloadData();
                    return;
                }
            }
        }
    }

    public int getMaxScroll() {
        return Math.max(contentHeight - height, 0);
    }

    public int getScrollAmount() {
        return scrollAmount;
    }

    public void setScrollAmount(int scrollAmount) {
        this.scrollAmount = MathUtils.clamp(scrollAmount, 0, this.getMaxScroll());
    }

    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
        this.setScrollAmount(this.getScrollAmount() - (int) (p_231043_5_ * height / 4));
        return true;
    }

    private void apply(Consumer<AbstractWidget> consumer) {
        for (GuiEventListener item : children) {
            if (item instanceof AbstractWidget) {
                consumer.accept((AbstractWidget) item);
            }
        }
    }

    private void showAll(Button sender) {
        router.showSkinList("", SkinTypes.UNKNOWN, SearchColumnType.DATE_CREATED, SearchOrderType.DESC);
    }

    private void showSkinInfo(SkinFileList.Entry sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryScreen.Page.HOME);
    }

    private AWLabel addTitle(SkinFileList list, String titleKey) {
        return addLabel(list.x + 1, list.y - 11, list.getWidth(), 16, getDisplayText(titleKey));
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

    private SkinFileList buildFileList(int x, int y, int width, int height) {
        SkinFileList fileList = new SkinFileList(x, y, width, height);
        fileList.setItemSize(new Size2i(50, 50));
        fileList.setBackgroundColor(0);
        fileList.setShowsName(false);
        fileList.setItemSelector(this::showSkinInfo);
        return fileList;
    }

    private int indexOf(SkinFileList list, int skinId) {
        return Iterables.indexOf(list.getEntries(), e -> e.id == skinId);
    }
}
