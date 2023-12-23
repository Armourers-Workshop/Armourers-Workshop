package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.foundation.NSString;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.impl.SearchResult;
import moe.plushie.armourers_workshop.library.data.impl.ServerSkin;
import moe.plushie.armourers_workshop.library.data.impl.ServerUser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UserSkinsLibraryPanel extends SearchResultsLibraryPanel {

    private ServerUser user;

    public UserSkinsLibraryPanel() {
        super("skin-library-global.searchResults", GlobalSkinLibraryWindow.Page.LIST_USER_SKINS::equals);
    }

    public void reloadData(ServerUser user) {
        this.clearResults();
        this.user = user;
        this.fetchPage(0);
    }

    protected void showSkinInfo(ServerSkin sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryWindow.Page.LIST_USER_SKINS);
    }

    @Override
    protected void doSearch(int pageIndex, int pageSize, ISkinType searchType, IResultHandler<SearchResult> handler) {
        GlobalSkinLibrary.getInstance().getUserSkinList(user.getId(), pageIndex, pageSize, searchType, handler);
    }

    @Override
    protected NSString getResultsTitle() {
        if (totalPages < 0) {
            return getDisplayText("label.searching");
        }
        if (totalPages == 0) {
            return getDisplayText("label.no_results");
        }
        String username = "unknown";
        if (user != null && !user.getName().isEmpty()) {
            username = user.getName();
        }
        return getDisplayText("user_results", username, currentPage + 1, totalPages, totalResults);
    }
}
