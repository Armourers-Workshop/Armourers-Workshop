package moe.plushie.armourers_workshop.library.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.PlushieUser;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinListUser;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.gui.widget.SkinFileList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BiConsumer;


@OnlyIn(Dist.CLIENT)
public class GlobalLibraryUserSkinsPanel extends GlobalLibrarySearchResultsPanel {

    private int userId;

    public GlobalLibraryUserSkinsPanel() {
        super("inventory.armourers_workshop.skin-library-global.searchResults", GlobalSkinLibraryScreen.Page.USER_SKINS::equals);
    }

    public void reloadData(int userId) {
        this.clearResults();
        this.userId = userId;
        this.fetchPage(0);
    }

    protected void showSkinInfo(SkinFileList.Entry sender) {
        router.showSkinDetail(sender, GlobalSkinLibraryScreen.Page.USER_SKINS);
    }

    @Override
    protected void doSearch(int pageIndex, String searchTypes, BiConsumer<JsonObject, Throwable> handler) {
        GlobalTaskSkinListUser taskSkinListUser = new GlobalTaskSkinListUser(userId, searchTypes, pageIndex, skinPanelResults.getTotalCount());
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
    public ITextComponent getTitle() {
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
