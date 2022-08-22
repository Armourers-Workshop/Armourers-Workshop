package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinItemList;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchColumnType;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchOrderType;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Environment(value = EnvType.CLIENT)
public class SearchResultsLibraryPanel extends AbstractLibraryPanel implements GlobalSkinLibraryWindow.ISkinListListener {

    private final HashSet<Integer> downloadingPages = new HashSet<>();
    private final HashMap<Integer, ArrayList<SkinItemList.Entry>> downloadedPageList = new HashMap<>();
    private final UILabel resultTitle = new UILabel(CGRect.ZERO);
    private final SkinItemList skinPanelResults = new SkinItemList(CGRect.ZERO);

    private int itemSize = 48;
    private int lastRequestSize = 0;

    protected int currentPage = 0;
    protected int totalPages = -1;
    protected int totalResults = 0;

    private String keyword = "";
    private ISkinType skinType = SkinTypes.UNKNOWN;
    private SearchColumnType columnType = SearchColumnType.DATE_CREATED;
    private SearchOrderType orderType = SearchOrderType.DESC;

    public SearchResultsLibraryPanel() {
        this("inventory.armourers_workshop.skin-library-global.searchResults", GlobalSkinLibraryWindow.Page.LIST_SEARCH::equals);
    }

    public SearchResultsLibraryPanel(String titleKey, Predicate<GlobalSkinLibraryWindow.Page> predicate) {
        super(titleKey, predicate);
        this.setup();
    }

    private void setup() {
        CGRect rect = bounds();

        resultTitle.setFrame(new CGRect(4, 2, rect.width - 64, 16));
        resultTitle.setTextColor(UIColor.WHITE);
        resultTitle.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        addSubview(resultTitle);

        skinPanelResults.setFrame(new CGRect(4, 20, rect.width - 8, rect.height - 40 - 3));
        skinPanelResults.setItemSelector(this::showSkinInfo);
        skinPanelResults.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        addSubview(skinPanelResults);
        setItemSize(itemSize);

        int iconX = rect.width - 4;

        addIconButton(iconX - 52, 2, 48, 0, 16, 16, "small", buildItemSizeUpdater(32));
        addIconButton(iconX - 34, 2, 48, 17, 16, 16, "medium", buildItemSizeUpdater(48));
        addIconButton(iconX - 16, 2, 48, 34, 16, 16, "large", buildItemSizeUpdater(80));

        UIButton btn1 = addCommonButton(4, rect.height - 20, 208, 80, 16, 16, "button.previousPage", buildPageUpdater(-1));
        UIButton btn2 = addCommonButton(rect.width - 20, rect.height - 20, 208, 96, 16, 16, "button.nextPage", buildPageUpdater(1));
        btn1.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleTopMargin);
        btn2.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        int pageSize = skinPanelResults.getTotalCount();
        if (lastRequestSize > 0 && this.lastRequestSize != pageSize) {
            resize();
        } else {
            onPageDidChange();
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
    public void skinDidChange(int skinId, @Nullable SkinItemList.Entry newValue) {
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

    protected void showSkinInfo(SkinItemList.Entry sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryWindow.Page.LIST_SEARCH);
    }

    protected NSString getResultsTitle() {
        if (totalPages < 0) {
            return getDisplayText("label.searching");
        }
        if (totalPages == 0) {
            return getDisplayText("label.no_results");
        }
        return getDisplayText("results", currentPage + 1, totalPages, totalResults);
    }

    private void addIconButton(int x, int y, int u, int v, int width, int height, String key, BiConsumer<SearchResultsLibraryPanel, UIControl> handler) {
        UIButton button = new UIButton(new CGRect(x, y, width, height));
        button.setImage(ModTextures.iconImage(u, v, width, height, ModTextures.GLOBAL_SKIN_LIBRARY), UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.setTooltip(getDisplayText(key));
        button.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, handler);
        addSubview(button);
    }

    private UIButton addCommonButton(int x, int y, int u, int v, int width, int height, String key, BiConsumer<SearchResultsLibraryPanel, UIControl> handler) {
        UIButton button = new UIButton(new CGRect(x, y, width, height));
        button.setImage(ModTextures.iconImage(u, v, width, height, ModTextures.BUTTONS), UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.setTooltip(getCommonDisplayText(key));
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, handler);
        addSubview(button);
        return button;
    }

    private void setItemSize(int itemSize) {
        this.itemSize = itemSize;
        this.skinPanelResults.setItemSize(new CGSize(itemSize, itemSize));
        this.skinPanelResults.setShowsName(itemSize >= 72);
        this.skinPanelResults.reloadData();
    }

    private BiConsumer<SearchResultsLibraryPanel, UIControl> buildPageUpdater(int step) {
        return (self, sender) -> {
            self.currentPage = MathUtils.clamp(self.currentPage + step, 0, self.totalPages - 1);
            fetchPage(self.currentPage);
            onPageDidChange();
            // auto request skin data for the previous/next page
            int pageIndex = self.currentPage + step;
            if (pageIndex > 0 && pageIndex < self.totalPages) {
                RenderSystem.recordRenderCall(() -> fetchPage(pageIndex));
            }
        };
    }

    private BiConsumer<SearchResultsLibraryPanel, UIControl> buildItemSizeUpdater(int size) {
        return (self, sender) -> {
            self.setItemSize(size);
            self.resize();
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
        resultTitle.setText(getResultsTitle());
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
        ArrayList<SkinItemList.Entry> entries = new ArrayList<>();
        if (result.has("results")) {
            JsonArray pageResults = result.get("results").getAsJsonArray();
            for (int i = 0; i < pageResults.size(); i++) {
                JsonObject skinJson = pageResults.get(i).getAsJsonObject();
                entries.add(new SkinItemList.Entry(skinJson));
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
            onPageDidChange();
        });
        // auto request skin data for the seconds page
        if (pageIndex == 0 && totalPages > 1) {
            RenderSystem.recordRenderCall(() -> fetchPage(1));
        }
    }

    private void onPageDidChange() {
        ArrayList<SkinItemList.Entry> entries = downloadedPageList.getOrDefault(currentPage, new ArrayList<>());
        skinPanelResults.setEntries(entries);
        skinPanelResults.reloadData();
        resultTitle.setText(getResultsTitle());
    }

    private Pair<Integer, Integer> getPageBySkin(int skinId) {
        for (Map.Entry<Integer, ArrayList<SkinItemList.Entry>> entry : downloadedPageList.entrySet()) {
            int index = Iterables.indexOf(entry.getValue(), e -> e.id == skinId);
            if (index != -1) {
                return Pair.of(entry.getKey(), index);
            }
        }
        return null;
    }
}
