package moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary;

import com.apple.library.coregraphics.CGGradient;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UILabelDelegate;
import moe.plushie.armourers_workshop.compat.core.AbstractOpenURLEvent;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.AbstractLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.HeaderLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.HomeLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.InfoLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.JoinLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.ModerationLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.SearchBoxLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.SearchResultsLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.SkinDetailLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.SkinEditLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.UploadLibraryPanel;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels.UserSkinsLibraryPanel;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.impl.SearchColumnType;
import moe.plushie.armourers_workshop.library.data.impl.SearchOrderType;
import moe.plushie.armourers_workshop.library.data.impl.ServerSkin;
import moe.plushie.armourers_workshop.library.data.impl.ServerUser;
import moe.plushie.armourers_workshop.library.menu.GlobalSkinLibraryMenu;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

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
    private boolean didInit = false;

    public GlobalSkinLibraryWindow(GlobalSkinLibraryMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setContents(new CGGradient(UIColor.of(0xc0101010), UIColor.of(0xd0101010)));
        this.setFrame(new CGRect(0, 0, 640, 480));
        this.titleView.setTextColor(UIColor.of(0xffcccccc));
        this.inventoryView.setHidden(true);
    }

    private void setupLibrary() {
        // welcome to global library :p
        GlobalSkinLibrary.getInstance().executor(Minecraft.getInstance());
        GlobalSkinLibrary.getInstance().connect(Minecraft.getInstance().getUser().getGameProfile(), null);
    }

    private void setupUI() {
        float width = bounds().width();
        float height = bounds().height();

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
        if (didInit) {
            layoutIfNeeded();
            homePanel.reloadData();
        }
    }

    @Override
    public void init() {
        super.init();
        setupLibrary();
        setupUI();
        didInit = true;
    }

    @Override
    public void deinit() {
        super.deinit();
        panels.forEach(panel -> panel.setRouter(null));
    }

    @Override
    public void screenWillResize(CGSize size) {
        setFrame(new CGRect(0, 0, size.width, size.height));
        menu.reload(0, 0, (int) size.width, (int) size.height);
        setupUI();
    }

    @Override
    public void screenWillTick() {
        super.screenWillTick();
        if (!didInit) {
            return;
        }
        GlobalSkinLibrary.getInstance().auth2();
        panels.forEach(AbstractLibraryPanel::tick);
    }

    @Override
    public boolean shouldRenderBackground() {
        return false;
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
        void skinDidChange(String skinId, @Nullable ServerSkin newValue);
    }

    public class Router implements ISkinListListener, UILabelDelegate {

        public void showPage(Page page) {
            setPage(page);
        }

        public void showNewHome() {
            headerPanel.reloadData();
            homePanel.reloadData();
            setPage(Page.HOME);
        }

        public void showSkinList(String keyword, SkinType skinType, SearchColumnType columnType, SearchOrderType orderType) {
            ModLog.debug("select * from global_library where keyword = '{}' and skinType = {} order by {} {}", keyword, skinType, columnType, orderType);
            searchBoxPanel.reloadData(keyword, skinType, columnType, orderType);
            searchResultsPanel.reloadData(keyword, skinType, columnType, orderType);
            setPage(Page.LIST_SEARCH);
        }

        public void showSkinList(ServerUser user) {
            if (user == null || !user.isMember()) {
                return;
            }
            searchBoxPanel.reloadData("", SkinTypes.UNKNOWN, SearchColumnType.DATE_CREATED, SearchOrderType.DESC);
            searchUserResultsPanel.reloadData(user);
            setPage(Page.LIST_USER_SKINS);
        }

        public void showSkinDetail(ServerSkin entry, Page returnPage) {
            skinDetailPanel.reloadData(entry, returnPage);
            setPage(Page.SKIN_DETAIL);
        }

        public void showSkinEdit(ServerSkin entry, Page returnPage) {
            skinEditPanel.reloadData(entry, returnPage);
            setPage(Page.SKIN_EDIT);
        }

        @Override
        public void labelWillClickAttributes(UILabel label, Map<String, ?> attributes) {
            // process open url event.
            if (attributes.get("ClickEvent") instanceof AbstractOpenURLEvent event) {
                Util.getPlatform().openUri(event.uri());
            }
        }

        public GlobalSkinLibraryMenu menu() {
            return menu;
        }

        @Override
        public void skinDidChange(String skinId, @Nullable ServerSkin newValue) {
            for (AbstractLibraryPanel panel : panels) {
                if (panel instanceof ISkinListListener listener) {
                    listener.skinDidChange(skinId, newValue);
                }
            }
        }
    }
}
