package moe.plushie.armourers_workshop.library.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UILabelDelegate;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.client.gui.panels.*;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinItemList;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchColumnType;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchOrderType;
import moe.plushie.armourers_workshop.library.menu.GlobalSkinLibraryMenu;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

@Environment(value = EnvType.CLIENT)
public class GlobalSkinLibraryWindow extends MenuWindow<GlobalSkinLibraryMenu> {

    private final Router router = new Router();
    private final ArrayList<AbstractLibraryPanel> panels = new ArrayList<>();

    private final HeaderLibraryPanel headerPanel = addPanel(HeaderLibraryPanel::new);
    private final SearchBoxLibraryPanel searchBoxPanel = addPanel(SearchBoxLibraryPanel::new);
    private final InfoLibraryPanel infoPanel = addPanel(InfoLibraryPanel::new);
    private final JoinLibraryPanel joinPanel = addPanel(JoinLibraryPanel::new);
    private final UploadLibraryPanel uploadPanel = addPanel(UploadLibraryPanel::new);
    private final ModerationLibraryPanel moderationPanel = addPanel(ModerationLibraryPanel::new);
    private final SearchResultsLibraryPanel searchResultsPanel = addPanel(SearchResultsLibraryPanel::new);
    private final UserSkinsLibraryPanel searchUserResultsPanel = addPanel(UserSkinsLibraryPanel::new);
    private final HomeLibraryPanel homePanel = addPanel(HomeLibraryPanel::new);
    private final SkinDetailLibraryPanel skinDetailPanel = addPanel(SkinDetailLibraryPanel::new);
    private final SkinEditLibraryPanel skinEditPanel = addPanel(SkinEditLibraryPanel::new);

    private Page page = Page.HOME;
    private boolean isInited = false;

    public GlobalSkinLibraryWindow(GlobalSkinLibraryMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setFrame(new CGRect(0, 0, 640, 480));
        this.titleView.setTextColor(new UIColor(0xcccccc));
        this.inventoryView.setHidden(true);
    }

    private void setupLibrary() {
        PlushieAuth.init();
        if (!PlushieAuth.startedRemoteUserCheck()) {
            PlushieAuth.doRemoteUserCheck();
        }
    }

    private void setupUI() {
        int width = bounds().getWidth();
        int height = bounds().getHeight();

        headerPanel.setFrame(new CGRect(0, 0, width, 26));
        searchBoxPanel.setFrame(new CGRect(0, 27, width, 23));
        infoPanel.setFrame(new CGRect(0, 27, width, height - 27));
        joinPanel.setFrame(new CGRect(0, 27, width, height - 27));
        uploadPanel.setFrame(new CGRect(0, 27, width, height - 27));
        moderationPanel.setFrame(new CGRect(0, 27, width, height - 27));
        homePanel.setFrame(new CGRect(0, 27 + 24, width, height - 27 - 24));
        searchResultsPanel.setFrame(new CGRect(0, 27 + 24, width, height - 27 - 24));
        searchUserResultsPanel.setFrame(new CGRect(0, 27 + 24, width, height - 27 - 24));
        skinDetailPanel.setFrame(new CGRect(0, 27 + 24, width, height - 27 - 24));
        skinEditPanel.setFrame(new CGRect(0, 27 + 24, width, height - 27 - 24));

        setVisible();

        // refresh the home page the first time you enter. This will speed up the display
        if (!isInited) {
            isInited = true;
            homePanel.reloadData();
        }
    }

    @Override
    public void init() {
        super.init();
        setupLibrary();
        setupUI();
    }

    @Override
    public void deinit() {
        super.deinit();
        panels.forEach(panel -> panel.setRouter(null));
    }

    @Override
    public void screenWillResize(CGSize size) {
        setFrame(new CGRect(0, 0, size.width, size.height));
        menu.reload(0, 0, size.width, size.height);
        setupUI();
    }

    @Override
    public void screenWillTick() {
        super.screenWillTick();
        if (!isInited) {
            return;
        }
        PlushieAuth.updateAccessToken();
        panels.forEach(AbstractLibraryPanel::tick);
    }

    private <T extends AbstractLibraryPanel> T addPanel(Supplier<T> provider) {
        T value = provider.get();
        value.setRouter(router);
        panels.add(value);
        return value;
    }

    private void setVisible() {
        panels.forEach(p -> {
            if (p.predicate.test(page)) {
                if (p.superview() != this) {
                    insertViewAtIndex(p, 0);
                    p.refresh();
                }
            } else {
                p.removeFromSuperview();
            }
        });
    }

    private void setPage(Page page) {
        this.page = page;
        this.setVisible();
    }

    public enum Page {
        HOME(true), LIBRARY_INFO(false), LIBRARY_JOIN(false), LIBRARY_MODERATION(false), SKIN_UPLOAD(false), SKIN_EDIT(true), SKIN_DETAIL(true), LIST_SEARCH(true), LIST_USER_SKINS(true);
        final boolean hasSearch;

        Page(boolean hasSearch) {
            this.hasSearch = hasSearch;
        }

        public boolean hasSearch() {
            return hasSearch;
        }
    }

    public interface ISkinListListener {
        void skinDidChange(int skinId, @Nullable SkinItemList.Entry newValue);
    }

    public class Router implements ISkinListListener, UILabelDelegate {

        public void showPage(Page page) {
            setPage(page);
        }

        public void showNewHome() {
            homePanel.reloadData();
            setPage(Page.HOME);
        }

        public void showSkinList(String keyword, ISkinType skinType, SearchColumnType columnType, SearchOrderType orderType) {
            ModLog.debug("select * from global_library where keyword = '{}' and skinType = {} order by {} {}", keyword, skinType, columnType, orderType);
            searchBoxPanel.reloadData(keyword, skinType, columnType, orderType);
            searchResultsPanel.reloadData(keyword, skinType, columnType, orderType);
            setPage(Page.LIST_SEARCH);
        }

        public void showSkinList(int userId) {
            searchBoxPanel.reloadData("", SkinTypes.UNKNOWN, SearchColumnType.DATE_CREATED, SearchOrderType.DESC);
            searchUserResultsPanel.reloadData(userId);
            setPage(Page.LIST_USER_SKINS);
        }

        public void showSkinDetail(SkinItemList.Entry entry, Page returnPage) {
            skinDetailPanel.reloadData(entry, returnPage);
            setPage(Page.SKIN_DETAIL);
        }

        public void showSkinEdit(SkinItemList.Entry entry, Page returnPage) {
            skinEditPanel.reloadData(entry, returnPage);
            setPage(Page.SKIN_EDIT);
        }

        @Override
        public void labelWillClickAttributes(UILabel label, Map<String, ?> attributes) {
            ClickEvent clickEvent = ObjectUtils.safeCast(attributes.get("ClickEvent"), ClickEvent.class);
            if (clickEvent == null || clickEvent.getAction() != ClickEvent.Action.OPEN_URL) {
                return;
            }
            String value = clickEvent.getValue();
            try {
                URI uri = new URI(value);
                String s = uri.getScheme();
                if (s == null) {
                    throw new URISyntaxException(value, "Missing protocol");
                }
                Util.getPlatform().openUri(uri);
            } catch (URISyntaxException urisyntaxexception) {
                ModLog.error("Can't open url for {}", value, urisyntaxexception);
            }
        }

        public GlobalSkinLibraryMenu menu() {
            return menu;
        }

        public void skinDidChange(int skinId, @Nullable SkinItemList.Entry newValue) {
            for (AbstractLibraryPanel panel : panels) {
                if (panel instanceof ISkinListListener) {
                    ((ISkinListListener) panel).skinDidChange(skinId, newValue);
                }
            }
        }
    }
}
