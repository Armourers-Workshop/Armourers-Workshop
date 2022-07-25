package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWImageExtendedButton;
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
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Environment(value = EnvType.CLIENT)
public class SearchResultsLibraryPanel extends AbstractLibraryPanel implements GlobalSkinLibraryScreen.ISkinListListener {

    protected final HashSet<Integer> downloadingPages = new HashSet<>();
    protected final HashMap<Integer, ArrayList<SkinFileList.Entry>> downloadedPageList = new HashMap<>();
    protected SkinFileList skinPanelResults;
    protected AWImageButton iconButtonSmall;
    protected AWImageButton iconButtonMedium;
    protected AWImageButton iconButtonLarge;
    protected AWImageButton iconButtonPrevious;
    protected AWImageButton iconButtonNext;
    protected int itemSize = 48;
    protected int lastRequestSize = 0;

    protected int currentPage = 0;
    protected int totalPages = -1;
    protected int totalResults = 0;

    private String keyword = "";
    private ISkinType skinType = SkinTypes.UNKNOWN;
    private SearchColumnType columnType = SearchColumnType.DATE_CREATED;
    private SearchOrderType orderType = SearchOrderType.DESC;

    public SearchResultsLibraryPanel() {
        this("inventory.armourers_workshop.skin-library-global.searchResults", GlobalSkinLibraryScreen.Page.LIST_SEARCH::equals);
    }

    public SearchResultsLibraryPanel(String titleKey, Predicate<GlobalSkinLibraryScreen.Page> predicate) {
        super(titleKey, predicate);
    }

    @Override
    protected void init() {
        super.init();
        int iconX = leftPos + width - 4;

        this.iconButtonSmall = addIconButton(iconX - 52, topPos + 2, 48, 0, 16, 16, "small", buildItemSizeUpdater(32));
        this.iconButtonMedium = addIconButton(iconX - 34, topPos + 2, 48, 17, 16, 16, "medium", buildItemSizeUpdater(48));
        this.iconButtonLarge = addIconButton(iconX - 16, topPos + 2, 48, 34, 16, 16, "large", buildItemSizeUpdater(80));

        this.skinPanelResults = new SkinFileList(leftPos + 4, topPos + 20, width - 8, height - 40 - 3);
        this.skinPanelResults.setItemSelector(this::showSkinInfo);
        this.setItemSize(itemSize);
        this.addButton(skinPanelResults);

        this.iconButtonPrevious = addCommonButton(leftPos + 4, topPos + height - 20, 208, 80, 16, 16, "button.previousPage", buildPageUpdater(-1));
        this.iconButtonNext = addCommonButton(leftPos + width - 20, topPos + height - 20, 208, 96, 16, 16, "button.nextPage", buildPageUpdater(1));

        int pageSize = skinPanelResults.getTotalCount();
        if (this.lastRequestSize > 0 && this.lastRequestSize != pageSize) {
            this.resize();
        } else {
            this.onPageDidChange();
        }
    }

    public void reloadData(String keyword, ISkinType skinType, SearchColumnType columnType, SearchOrderType orderType) {
        this.clearResults();
        this.keyword = keyword;
        this.skinType = skinType;
        this.columnType = columnType;
        this.orderType = orderType;
        this.fetchPage(0);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        RenderUtils.enableScissor(titleLabelX, titleLabelY, width - 64, 16);
        super.renderLabels(matrixStack, mouseX, mouseY);
        RenderUtils.disableScissor();
    }

    @Override
    public void renderBackgroundLayer(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
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
        Pair<Integer, Integer> page = getPageBySkin(skinId);
        if (page != null) {
            // removed skin in here
            for (int key : downloadedPageList.keySet()) {
                if (key >= page.getFirst()) {
                    downloadedPageList.remove(key);
                }
            }
            if (currentPage >= page.getFirst()) {
                fetchPage(currentPage);
                onPageDidChange();
            }
        }
    }

    protected void showSkinInfo(SkinFileList.Entry sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryScreen.Page.LIST_SEARCH);
    }

    @Override
    public Component getTitle() {
        if (totalPages < 0) {
            return getDisplayText("label.searching");
        }
        if (totalPages == 0) {
            return getDisplayText("label.no_results");
        }
        return getDisplayText("results", currentPage + 1, totalPages, totalResults);
    }

    private AWImageButton addIconButton(int x, int y, int u, int v, int width, int height, String key, Button.OnPress handler) {
        Component tooltip = getDisplayText(key);
        AWImageButton button = new AWImageExtendedButton(x, y, width, height, u, v, RenderUtils.TEX_GLOBAL_SKIN_LIBRARY, handler, this::addHoveredButton, tooltip);
        addButton(button);
        return button;
    }

    private AWImageButton addCommonButton(int x, int y, int u, int v, int width, int height, String key, Button.OnPress handler) {
        Component tooltip = getCommonDisplayText(key);
        AWImageButton button = new AWImageButton(x, y, width, height, u, v, RenderUtils.TEX_BUTTONS, handler, this::addHoveredButton, tooltip);
        addButton(button);
        return button;
    }

    private void setItemSize(int itemSize) {
        this.itemSize = itemSize;
        this.skinPanelResults.setItemSize(new Size2i(itemSize, itemSize));
        this.skinPanelResults.setShowsName(itemSize >= 72);
        this.skinPanelResults.reloadData();
    }

    private Button.OnPress buildPageUpdater(int step) {
        return button -> {
            currentPage = MathUtils.clamp(currentPage + step, 0, totalPages - 1);
            fetchPage(currentPage);
            onPageDidChange();
            // auto request skin data for the previous/next page
            int pageIndex = currentPage + step;
            if (pageIndex > 0 && pageIndex < totalPages) {
                RenderSystem.recordRenderCall(() -> fetchPage(pageIndex));
            }
        };
    }

    private Button.OnPress buildItemSizeUpdater(int size) {
        return button -> {
            setItemSize(size);
            resize();
        };
    }

    protected void resize() {
        clearResults();
        fetchPage(currentPage);
    }

    public void clearResults() {
        downloadingPages.clear();
        downloadedPageList.clear();
        currentPage = 0;
        totalPages = -1;
        totalResults = 0;
        skinPanelResults.setEntries(new ArrayList<>());
        lastRequestSize = 0;
    }

    protected void fetchPage(int pageIndex) {
        if (downloadingPages.contains(pageIndex) || downloadedPageList.containsKey(pageIndex)) {
            return; // downloading or downloaded, ignore
        }
        lastRequestSize = skinPanelResults.getTotalCount();
        downloadingPages.add(pageIndex);
        String searchTypes = "";
        if (skinType != null && skinType != SkinTypes.UNKNOWN) {
            searchTypes = skinType.getRegistryName().toString();
        } else {
            searchTypes = GlobalSkinLibraryUtils.allSearchTypes();
        }
        ModLog.debug("request skin list {} of {}, page size: {}", pageIndex, totalPages, lastRequestSize);
        doSearch(pageIndex, lastRequestSize, searchTypes, (result, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
                downloadingPages.remove(pageIndex);
            } else {
                downloadingPages.remove(pageIndex);
                onPageJsonDownload(pageIndex, result);
            }
        });
    }

    protected void doSearch(int pageIndex, int pageSize, String searchTypes, BiConsumer<JsonObject, Throwable> handler) {
        GlobalTaskSkinSearch taskSkinSearch = new GlobalTaskSkinSearch(keyword, searchTypes, pageIndex, pageSize);
        taskSkinSearch.setSearchOrderColumn(columnType);
        taskSkinSearch.setSearchOrder(orderType);
        taskSkinSearch.createTaskAndRun(new FutureCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject result) {
                handler.accept(result, null);
            }

            @Override
            public void onFailure(Throwable t) {
                handler.accept(null, t);
            }
        });
    }

    protected void onPageJsonDownload(int pageIndex, JsonObject result) {
        ArrayList<SkinFileList.Entry> entries = new ArrayList<>();
        if (result.has("results")) {
            JsonArray pageResults = result.get("results").getAsJsonArray();
            for (int i = 0; i < pageResults.size(); i++) {
                JsonObject skinJson = pageResults.get(i).getAsJsonObject();
                entries.add(new SkinFileList.Entry(skinJson));
            }
        }
        if (result.has("totalPages")) {
            totalPages = result.get("totalPages").getAsInt();
        }
        if (result.has("totalResults")) {
            totalResults = result.get("totalResults").getAsInt();
        }
        downloadedPageList.put(pageIndex, entries);
        RenderSystem.recordRenderCall(() -> {
            ModLog.debug("receive skin list {} of {}", pageIndex, totalPages);
            this.onPageDidChange();
        });
        // auto request skin data for the seconds page
        if (pageIndex == 0 && totalPages > 1) {
            RenderSystem.recordRenderCall(() -> fetchPage(1));
        }
    }

    private void onPageDidChange() {
        ArrayList<SkinFileList.Entry> entries = downloadedPageList.getOrDefault(currentPage, new ArrayList<>());
        skinPanelResults.setEntries(entries);
        skinPanelResults.reloadData();
    }

    private Pair<Integer, Integer> getPageBySkin(int skinId) {
        for (Map.Entry<Integer, ArrayList<SkinFileList.Entry>> entry : downloadedPageList.entrySet()) {
            int index = Iterables.indexOf(entry.getValue(), e -> e.id == skinId);
            if (index != -1) {
                return Pair.of(entry.getKey(), index);
            }
        }
        return null;
    }
}
