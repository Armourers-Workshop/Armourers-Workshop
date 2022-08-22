package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinItemList;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchColumnType;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchOrderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

@Environment(value = EnvType.CLIENT)
public class HomeLibraryPanel extends AbstractLibraryPanel implements GlobalSkinLibraryWindow.ISkinListListener {

    private final UIScrollView scrollView = new UIScrollView(CGRect.ZERO);

    //    private final GuiScrollbar scrollbar;
    private final SkinItemList skinPanelRecentlyUploaded = buildFileList(0, 0, 300, 307);
    private final SkinItemList skinPanelMostDownloaded = buildFileList(0, 0, 300, 307);
    private final SkinItemList skinPanelTopRated = buildFileList(0, 0, 300, 307);
    private final SkinItemList skinPanelNeedRated = buildFileList(0, 0, 300, 307);

    private int lastRequestSize = 0;

    public HomeLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.home", GlobalSkinLibraryWindow.Page.HOME::equals);
        this.setup();
    }

    private void setup() {
        scrollView.setFrame(bounds());
        scrollView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        scrollView.setContentSize(new CGSize(0, 1000));
        scrollView.setContentOffset(new CGPoint(0, 20));
        addSubview(scrollView);

        scrollView.addSubview(skinPanelRecentlyUploaded);
        scrollView.addSubview(skinPanelTopRated);
        scrollView.addSubview(skinPanelNeedRated);
        scrollView.addSubview(skinPanelMostDownloaded);

        buildTitle(skinPanelRecentlyUploaded, "recentlyUploaded");
        buildTitle(skinPanelMostDownloaded, "mostDownloaded");
        buildTitle(skinPanelTopRated, "topRated");
        buildTitle(skinPanelNeedRated, "needRated");

        UIButton button = new UIButton(new CGRect(4, 6, 80, 16));
        button.setTitle(getDisplayText("showAllSkins"), UIControl.State.NORMAL);
        button.setTitleColor(UIColor.WHITE, UIControl.State.NORMAL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, HomeLibraryPanel::showAll);
        scrollView.addSubview(button);

        setNeedsLayout();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        int listTop = 20;
        int listLeft = 4;
        int width = bounds().getWidth();
        for (SkinItemList fileList : lists()) {
            fileList.setFrame(new CGRect(listLeft, listTop + 20, width, 307));
            fileList.setItemSize(new CGSize(50, 50));
            fileList.setBackgroundColor(0);
            fileList.setShowsName(false);
            fileList.reloadData();
            listTop = fileList.frame().getMaxY();
        }
        scrollView.setContentSize(new CGSize(0, listTop + 4));
        scrollView.setContentOffset(scrollView.contentOffset());

        int pageSize = skinPanelRecentlyUploaded.getTotalCount();
        if (lastRequestSize > 0 && lastRequestSize < pageSize) {
            reloadData();
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
                ArrayList<SkinItemList.Entry> entries = buildEntries(result);
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
                ArrayList<SkinItemList.Entry> entries = buildEntries(result);
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
                ArrayList<SkinItemList.Entry> entries = buildEntries(result);
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
                ArrayList<SkinItemList.Entry> entries = buildEntries(result);
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
    public void skinDidChange(int skinId, @Nullable SkinItemList.Entry newValue) {
        // only update for remove
        if (newValue != null) {
            return;
        }
        for (SkinItemList fileList : lists()) {
            if (indexOf(fileList, skinId) != -1) {
                // removed skin in here
                reloadData();
                return;
            }
        }
    }

    private void showAll(UIControl sender) {
        router.showSkinList("", SkinTypes.UNKNOWN, SearchColumnType.DATE_CREATED, SearchOrderType.DESC);
    }

    private void showSkinInfo(SkinItemList.Entry sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryWindow.Page.HOME);
    }

    private void buildTitle(SkinItemList list, String titleKey) {
        UILabel label = new UILabel(new CGRect(1, -16, list.frame().width - 2, 16));
        label.setText(getDisplayText(titleKey));
        label.setTextColor(UIColor.WHITE);
        label.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        list.addSubview(label);
    }

    private ArrayList<SkinItemList.Entry> buildEntries(JsonObject result) {
        ArrayList<SkinItemList.Entry> entries = new ArrayList<>();
        if (result.has("results")) {
            JsonArray pageResults = result.get("results").getAsJsonArray();
            for (int i = 0; i < pageResults.size(); i++) {
                JsonObject skinJson = pageResults.get(i).getAsJsonObject();
                entries.add(new SkinItemList.Entry(skinJson));
            }
        }
        return entries;
    }

    private SkinItemList buildFileList(int x, int y, int width, int height) {
        SkinItemList fileList = new SkinItemList(new CGRect(x, y, width ,height));
        fileList.setItemSize(new CGSize(50, 50));
        fileList.setBackgroundColor(0);
        fileList.setShowsName(false);
        fileList.setItemSelector(this::showSkinInfo);
        return fileList;
    }

    private int indexOf(SkinItemList list, int skinId) {
        return Iterables.indexOf(list.getEntries(), e -> e.id == skinId);
    }

    private Iterable<SkinItemList> lists() {
        return () -> Iterators.filter(scrollView.subviews().listIterator(), SkinItemList.class);
    }
}
