package moe.plushie.armourers_workshop.library.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageExtendedButton;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.ResultHandler;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.gui.widget.SkinFileList;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Size2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class GlobalLibrarySearchResultsPanel extends GlobalLibraryAbstractPanel {

    protected SkinFileList skinPanelResults;

    protected AWImageButton iconButtonSmall;
    protected AWImageButton iconButtonMedium;
    protected AWImageButton iconButtonLarge;

    protected AWImageButton iconButtonPrevious;
    protected AWImageButton iconButtonNext;

    protected final HashSet<Integer> downloadingPages = new HashSet<>();
    protected final HashMap<Integer, ArrayList<SkinFileList.Entry>> downloadedPageList = new HashMap<>();

    protected int itemSize = 48;

    protected int currentPage = 0;
    protected int totalPages = -1;
    protected int totalResults = 0;

    private String keyword = "";
    private ISkinType skinType = SkinTypes.UNKNOWN;
    private GlobalTaskSkinSearch.SearchColumnType columnType = GlobalTaskSkinSearch.SearchColumnType.DATE_CREATED;
    private GlobalTaskSkinSearch.SearchOrderType orderType = GlobalTaskSkinSearch.SearchOrderType.DESC;

    public GlobalLibrarySearchResultsPanel() {
        this("inventory.armourers_workshop.skin-library-global.searchResults", GlobalSkinLibraryScreen.Page.RESULTS::equals);
    }

    public GlobalLibrarySearchResultsPanel(String titleKey, Predicate<GlobalSkinLibraryScreen.Page> predicate) {
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

        this.iconButtonPrevious = addCommonButton(leftPos + 4, topPos + height - 20, 208, 80, 16, 16, "button.previous", buildPageUpdater(-1));
        this.iconButtonNext = addCommonButton(leftPos + width - 20, topPos + height - 20, 208, 96, 16, 16, "button.next", buildPageUpdater(1));
    }

    public void reloadData(String keyword, ISkinType skinType, GlobalTaskSkinSearch.SearchColumnType columnType, GlobalTaskSkinSearch.SearchOrderType orderType) {
        this.clearResults();
        this.keyword = keyword;
        this.skinType = skinType;
        this.columnType = columnType;
        this.orderType = orderType;
        this.fetchPage(0);
    }

    @Override
    public void renderBackgroundLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // all content will render on the background layer
    }

    protected void showSkinInfo(SkinFileList.Entry sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryScreen.Page.RESULTS);
    }

    @Override
    public ITextComponent getTitle() {
        if (totalPages < 0) {
            return getDisplayText("label.searching");
        }
        if (totalPages == 0) {
            return getDisplayText("label.no_results");
        }
        return getDisplayText("results", currentPage + 1, totalPages, totalResults);
    }

    private AWImageButton addIconButton(int x, int y, int u, int v, int width, int height, String key, Button.IPressable handler) {
        ITextComponent tooltip = getDisplayText(key);
        AWImageButton button = new AWImageExtendedButton(x, y, width, height, u, v, RenderUtils.TEX_GLOBAL_SKIN_LIBRARY, handler, this::addHoveredButton, tooltip);
        addButton(button);
        return button;
    }

    private AWImageButton addCommonButton(int x, int y, int u, int v, int width, int height, String key, Button.IPressable handler) {
        ITextComponent tooltip = getCommonDisplayText(key);
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

    private Button.IPressable buildPageUpdater(int step) {
        return button -> {
            currentPage = MathHelper.clamp(currentPage + step, 0, totalPages - 1);
            fetchPage(currentPage);
            onPageDidChange();
        };
    }

    private Button.IPressable buildItemSizeUpdater(int size) {
        return button -> {
            clearResults();
            setItemSize(size);
            fetchPage(currentPage);
        };
    }
//
//    protected void resize() {
//        refresh();
//    }
//

    public void clearResults() {
        downloadingPages.clear();
        downloadedPageList.clear();
        currentPage = 0;
        totalPages = -1;
        totalResults = 0;
        skinPanelResults.setEntries(new ArrayList<>());
    }

    protected void fetchPage(int pageIndex) {
        if (downloadingPages.contains(pageIndex) || downloadedPageList.containsKey(pageIndex)) {
            return; // downloading or downloaded, ignore
        }
        downloadingPages.add(pageIndex);
        String searchTypes = "";
        if (skinType != null && skinType != SkinTypes.UNKNOWN) {
            searchTypes = skinType.getRegistryName().toString();
        } else {
            StringBuilder searchTypesBuilder = new StringBuilder();
            for (ISkinType skinType : SkinTypes.values()) {
                ResourceLocation registryName = skinType.getRegistryName();
                if (skinType != SkinTypes.UNKNOWN && registryName != null) {
                    if (searchTypesBuilder.length() != 0) {
                        searchTypesBuilder.append(";");
                    }
                    searchTypesBuilder.append(registryName);
                }
            }
            searchTypes = searchTypesBuilder.toString();
        }
        doSearch(pageIndex, searchTypes, (result, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
                downloadingPages.remove(pageIndex);
            } else {
                downloadingPages.remove(pageIndex);
                onPageJsonDownload(pageIndex, result);
            }
        });
    }

    protected void doSearch(int pageIndex, String searchTypes, BiConsumer<JsonObject, Throwable> handler) {
        GlobalTaskSkinSearch taskSkinSearch = new GlobalTaskSkinSearch(keyword, searchTypes, pageIndex, skinPanelResults.getTotalCount());
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
//        int pageIndex = 0;
//        if (result.has("currentPageIndex")) {
//            pageIndex = result.get("currentPageIndex").getAsInt();
//            if (pageIndex == 0 & totalPages > 1) {
//                fetchPage(1);
//            }
//        }
        downloadedPageList.put(pageIndex, entries);
        RenderSystem.recordRenderCall(this::onPageDidChange);
    }

    private void onPageDidChange() {
        ArrayList<SkinFileList.Entry> entries = downloadedPageList.getOrDefault(currentPage, new ArrayList<>());
        skinPanelResults.setEntries(entries);
        skinPanelResults.reloadData();
    }
}
