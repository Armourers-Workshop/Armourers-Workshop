package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinFileList;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.PlushieUser;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinListUser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

@Environment(value = EnvType.CLIENT)
public class UserSkinsLibraryPanel extends SearchResultsLibraryPanel {

    private int userId;

    public UserSkinsLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.searchResults", GlobalSkinLibraryScreen.Page.LIST_USER_SKINS::equals);
    }

    public void reloadData(int userId) {
        this.clearResults();
        this.userId = userId;
        this.fetchPage(0);
    }

    protected void showSkinInfo(SkinFileList.Entry sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryScreen.Page.LIST_USER_SKINS);
    }

    @Override
    protected void doSearch(int pageIndex, int pageSize, String searchTypes, BiConsumer<JsonObject, Throwable> handler) {
        GlobalTaskSkinListUser taskSkinListUser = new GlobalTaskSkinListUser(userId, searchTypes, pageIndex, pageSize);
        taskSkinListUser.createTaskAndRun(new FutureCallback<JsonObject>() {

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

    @Override
    public Component getTitle() {
        if (totalPages < 0) {
            return getDisplayText("label.searching");
        }
        if (totalPages == 0) {
            return getDisplayText("label.no_results");
        }
        PlushieUser plushieUser = GlobalSkinLibraryUtils.getUserInfo(userId);
        String username = "unknown";
        if (plushieUser != null) {
            username = plushieUser.getUsername();
        }
        return getDisplayText("user_results", username, currentPage + 1, totalPages, totalResults);
    }
}
