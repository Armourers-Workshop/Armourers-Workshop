package moe.plushie.armourers_workshop.library.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.core.gui.widget.AWAbstractDialog;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.container.GlobalSkinLibraryContainer;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch;
import moe.plushie.armourers_workshop.library.gui.panels.*;
import moe.plushie.armourers_workshop.library.gui.widget.SkinFileList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class GlobalSkinLibraryScreen extends AWAbstractContainerScreen<GlobalSkinLibraryContainer> {

    private final Router router = new Router();
    private final ArrayList<GlobalLibraryAbstractPanel> panels = new ArrayList<>();

    private final GlobalLibraryHeaderPanel headerPanel = addPanel(GlobalLibraryHeaderPanel::new);
    private final GlobalLibrarySearchBoxPanel searchBoxPanel = addPanel(GlobalLibrarySearchBoxPanel::new);
    private final GlobalLibraryInfoPanel infoPanel = addPanel(GlobalLibraryInfoPanel::new);
    private final GlobalLibraryJoinPanel joinPanel = addPanel(GlobalLibraryJoinPanel::new);
    private final GlobalLibraryUploadPanel uploadPanel = addPanel(GlobalLibraryUploadPanel::new);
    private final GlobalLibrarySearchResultsPanel searchResultsPanel = addPanel(GlobalLibrarySearchResultsPanel::new);
    private final GlobalLibraryUserSkinsPanel searchUserResultsPanel = addPanel(GlobalLibraryUserSkinsPanel::new);
    private final GlobalLibraryHomePanel homePanel = addPanel(GlobalLibraryHomePanel::new);
    private final GlobalLibrarySkinDetailPanel skinDetailPanel = addPanel(GlobalLibrarySkinDetailPanel::new);
    private final GlobalLibrarySkinEditPanel skinEditPanel = addPanel(GlobalLibrarySkinEditPanel::new);

    private Page page = Page.HOME;

    private boolean isInited = false;

    public GlobalSkinLibraryScreen(GlobalSkinLibraryContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);

        if (!PlushieAuth.startedRemoteUserCheck()) {
            PlushieAuth.doRemoteUserCheck();
        }
    }

    @Override
    protected void init() {
        this.imageWidth = width;
        this.imageHeight = height;
        super.init();
        this.titleLabelX = imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = 8;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        this.headerPanel.init(minecraft, 0, 0, width, 26);
        this.searchBoxPanel.init(minecraft, 0, 27, width, 23);
        this.infoPanel.init(minecraft, 0, 27, width, height - 27);
        this.joinPanel.init(minecraft, 0, 27, width, height - 27);
        this.uploadPanel.init(minecraft, 0, 27, width, height - 27);
        this.homePanel.init(minecraft, 0, 27 + 24, width, height - 27 - 24);
        this.searchResultsPanel.init(minecraft, 0, 27 + 24, width, height - 27 - 24);
        this.searchUserResultsPanel.init(minecraft, 0, 27 + 24, width, height - 27 - 24);
        this.skinDetailPanel.init(minecraft, 0, 27 + 24, width, height - 27 - 24);
        this.skinEditPanel.init(minecraft, 0, 27 + 24, width, height - 27 - 24);
        this.setVisible();

        // refresh the home page the first time you enter. This will speed up the display
        if (!this.isInited) {
            this.homePanel.reloadData();
        }

        this.isInited = true;
    }

    @Override
    public void removed() {
        super.removed();
        for (GlobalLibraryAbstractPanel panel : panels) {
            panel.setRouter(null);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isInited) {
            return;
        }
        PlushieAuth.updateAccessToken();
        panels.forEach(GlobalLibraryAbstractPanel::tick);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0xcccccc);
    }

    @Override
    public void renderContentLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.doRender(matrixStack, mouseX, mouseY, partialTicks, p -> p::renderBackgroundLayer);
        this.doRender(matrixStack, mouseX, mouseY, partialTicks, p -> p);
        super.renderContentLayer(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);
        this.doRender(matrixStack, mouseX, mouseY, 0, p -> p::renderTooltipLayer);
    }

    @Override
    public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
        if (key == GLFW.GLFW_KEY_TAB) {
            boolean flag = !hasShiftDown();
            if (!this.changeFocus(flag)) {
                this.changeFocus(flag);
            }
            return true;
        }
        return super.keyPressed(key, p_231046_2_, p_231046_3_);
    }

    @Override
    public Iterable<IGuiEventListener> nextResponder() {
        return panels.stream().filter(GlobalLibraryAbstractPanel::isVisible).collect(Collectors.toList());
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener p_231035_1_) {
        for (GlobalLibraryAbstractPanel panel : panels) {
            panel.setFocused(p_231035_1_);
        }
        super.setFocused(p_231035_1_);
    }

    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        if (dialog != null) {
            return dialog.changeFocus(p_231049_1_);
        }
        IGuiEventListener eventListener = this.getFocused();
        boolean flag = eventListener != null;
        if (flag && eventListener.changeFocus(p_231049_1_)) {
            return true;
        }
        ArrayList<IGuiEventListener> list = new ArrayList<>(this.children());
        for (GlobalLibraryAbstractPanel panel : panels) {
            if (panel.isVisible()) {
                list.addAll(panel.children());
            }
        }
        int j = list.indexOf(eventListener);
        int i;
        if (flag && j >= 0) {
            i = j + (p_231049_1_ ? 1 : 0);
        } else if (p_231049_1_) {
            i = 0;
        } else {
            i = list.size();
        }

        ListIterator<? extends IGuiEventListener> listIterator = list.listIterator(i);
        BooleanSupplier hasItem = p_231049_1_ ? listIterator::hasNext : listIterator::hasPrevious;
        Supplier<? extends IGuiEventListener> supplier = p_231049_1_ ? listIterator::next : listIterator::previous;

        while (hasItem.getAsBoolean()) {
            IGuiEventListener listener = supplier.get();
            if (listener.changeFocus(p_231049_1_)) {
                this.setFocused(listener);
                return true;
            }
        }

        this.setFocused(null);
        return false;

    }

    @Override
    protected void setFocusedWithResponder(FocusableGui responder) {
        if (responder instanceof GlobalLibraryAbstractPanel) {
            if (!((GlobalLibraryAbstractPanel) responder).isVisible()) {
                this.setFocused(null);
                return;
            }
        }
        super.setFocusedWithResponder(responder);
    }



    private <T extends GlobalLibraryAbstractPanel> T addPanel(Supplier<T> provider) {
        T value = provider.get();
        value.setRouter(router);
        panels.add(value);
        return value;
    }

    private void setVisible() {
        this.panels.forEach(p -> {
            boolean visible = p.predicate.test(page);
            if (p.isVisible() != visible) {
                p.setVisible(visible);
            }
        });
    }

    private void setPage(Page page) {
        this.page = page;
        this.setVisible();
    }

    public void doRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, Function<GlobalLibraryAbstractPanel, IRenderable> supplier) {
        for (GlobalLibraryAbstractPanel panel : panels) {
            if (panel.isVisible()) {
                supplier.apply(panel).render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    public enum Page {
        HOME(true), INFO(false), JOIN(false), SKIN_UPLOAD(false), SKIN_EDIT(true), MODERATION(false), RESULTS(true), USER_SKINS(true), SKIN_DETAIL(true);
        final boolean hasSearch;

        Page(boolean hasSearch) {
            this.hasSearch = hasSearch;
        }

        public boolean hasSearch() {
            return hasSearch;
        }
    }

//    public static class SearchFilter {
//        public String keyword = "";
//        public ISkinType skinType = SkinTypes.UNKNOWN;
//        public GlobalTaskSkinSearch.SearchOrderType orderType = GlobalTaskSkinSearch.SearchOrderType.ASC;
//        public GlobalTaskSkinSearch.SearchColumnType columnType = GlobalTaskSkinSearch.SearchColumnType.DATE_CREATED;
//
//        @Override
//        public String toString() {
//            return String.format("select * from global_library where keyword = '%s' and skinType = %s order by %s %s", keyword, skinType, columnType, orderType);
//        }
//    }

    public class Router {

        public void showPage(Page page) {
            setPage(page);
        }

        public void showNewHome() {
            homePanel.reloadData();
            setPage(Page.HOME);
        }

        public void showSkinList(String keyword, ISkinType skinType, GlobalTaskSkinSearch.SearchColumnType columnType, GlobalTaskSkinSearch.SearchOrderType orderType) {
            ModLog.debug("select * from global_library where keyword = '{}' and skinType = {} order by {} {}", keyword, skinType, columnType, orderType);
            searchBoxPanel.reloadData(keyword, skinType, columnType, orderType);
            searchResultsPanel.reloadData(keyword, skinType, columnType, orderType);
            setPage(Page.RESULTS);
        }

        public void showSkinList(int userId) {
            searchUserResultsPanel.reloadData(userId);
            setPage(Page.USER_SKINS);
        }

        public void showSkinDetail(SkinFileList.Entry entry, Page returnPage) {
            skinDetailPanel.reloadData(entry, returnPage);
            setPage(Page.SKIN_DETAIL);
        }

        public void showSkinEdit(SkinFileList.Entry entry, Page returnPage) {
            skinEditPanel.reloadData(entry, returnPage);
            setPage(Page.SKIN_EDIT);
        }

        public <T extends AWAbstractDialog> void showDialog(T dialog, Consumer<T> complete) {
            present(dialog, complete);
        }
    }
}
