package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIMenuPopoverView;
import com.apple.library.uikit.UIScrollView;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PartPickerView extends UIControl {

    protected SkinDescriptor selectedPart = SkinDescriptor.EMPTY;
    protected Collection<SkinDescriptor> historySkins;
    protected Consumer<SkinDescriptor> itemSelector;
    protected Runnable itemImporter;

    protected Predicate<SkinDescriptor> filter;
    protected final UIScrollView scrollView = new UIScrollView(CGRect.ZERO);
    protected final ArrayList<PartItemList> itemLists = new ArrayList<>();

    public PartPickerView(CGRect frame) {
        super(frame);
        this.scrollView.setFrame(bounds());
        this.addSubview(this.scrollView);
    }

    public void showInView(UIView fromView) {
        UIWindow window = fromView.window();
        if (window == null) {
            return;
        }
        setupData();
        // reset the location.
        CGRect bounds = bounds().applying(transform());
        CGRect rect = fromView.convertRectToView(fromView.bounds(), window).copy();
        if (rect.x + bounds.width > window.bounds().getMaxX()) {
            rect.x -= bounds.width;
        }
        rect.x += bounds.width / 2;
        rect.y += bounds.height / 2;
        setCenter(new CGPoint(rect.x, rect.y));
        // create and show popover container.
        UIMenuPopoverView popoverView = new UIMenuPopoverView();
        popoverView.setBackgroundColor(null);
        popoverView.setContentView(this);
        popoverView.showInView(fromView);
    }

    public void dismiss() {
        UIMenuPopoverView popoverView = ObjectUtils.safeCast(window(), UIMenuPopoverView.class);
        if (popoverView != null) {
            popoverView.dismiss();
        }
    }

    public SkinDescriptor getSelectedPart() {
        return selectedPart;
    }

    public void setSelectedPart(SkinDescriptor selectedItem) {
        this.selectedPart = selectedItem;
        this.itemLists.forEach(it -> it.setSelectedItem(selectedItem));
        if (itemSelector != null) {
            itemSelector.accept(selectedItem);
        }
        this.dismiss();
    }

    private void importPartAction() {
        dismiss();
        if (itemImporter != null) {
            itemImporter.run();
        }
    }

    private void selectPartAction(PartItem selectedItem) {
        if (selectedItem == PartItem.IMPORT) {
            importPartAction();
            return;
        }
        setSelectedPart(selectedItem.getDescriptor());
    }


    public Consumer<SkinDescriptor> getItemSelector() {
        return itemSelector;
    }

    public void setChangeListener(Consumer<SkinDescriptor> itemSelector) {
        this.itemSelector = itemSelector;
    }

    public void setHistorySkins(Collection<SkinDescriptor> historySkins) {
        this.historySkins = historySkins;
    }

    public Collection<SkinDescriptor> getHistorySkins() {
        return historySkins;
    }

    public void setImporter(Runnable itemImporter) {
        this.itemImporter = itemImporter;
    }

    public Runnable getImporter() {
        return itemImporter;
    }

    public void setFilter(Predicate<SkinDescriptor> filter) {
        this.filter = filter;
    }

    public Predicate<SkinDescriptor> getFilter() {
        return filter;
    }

    private void setupData() {
        List<List<PartItem>> items = new ArrayList<>();
        items.add(Lists.newArrayList(PartItem.IMPORT, PartItem.CLEAR));
        items.add(getInventorySkins().stream().filter(this::isValid).collect(Collectors.toList()));
        items.add(getImportedSkins().stream().filter(this::isValid).collect(Collectors.toList()));
        buildSections(items);
    }

    private void buildSections(List<List<PartItem>> sections) {
        float x = 4;
        float y = 4;
        float width = bounds().getWidth() - 8;
        float height = bounds().getHeight() - 8;
        for (int i = 0; i < sections.size(); ++i) {
            List<PartItem> items = sections.get(i);
            UIView contentView;
            UILabel titleView = new UILabel(new CGRect(x, y, width, 10));
            titleView.setTextColor(UIColor.WHITE);
            titleView.setText(NSString.localizedString("advanced-skin-builder.picker.section" + (i + 1)));
            if (items.isEmpty()) {
                UILabel emptyView = new UILabel(new CGRect(x, y + 12, width, 65));
                emptyView.setText(NSString.localizedString("advanced-skin-builder.picker.section" + (i + 1) + ".empty"));
                emptyView.setTextColor(UIColor.LIGHT_GRAY);
                emptyView.setNumberOfLines(0);
                emptyView.setTextHorizontalAlignment(NSTextAlignment.Horizontal.CENTER);
                emptyView.setBackgroundColor(UIColor.rgba(0x22AAAAAA));
                contentView = emptyView;
            } else {
                PartItemList sectionView = buildSectionView(x, y + 12, width, height);
                sectionView.setEntries(new ArrayList<>(items));
                CGSize size = sectionView.sizeThatFits(new CGSize(width, height));
                sectionView.setFrame(new CGRect(x, y + 12, size.getWidth(), size.getHeight()));
                sectionView.reloadData();
                contentView = sectionView;
            }
            scrollView.addSubview(titleView);
            scrollView.addSubview(contentView);
            y = contentView.frame().getMaxY() + 10;
        }
        scrollView.setContentSize(new CGSize(width, y));
    }

    private PartItemList buildSectionView(float x, float y, float width, float height) {
        PartItemList fileList = new PartItemList(new CGRect(x, y, width, height));
        fileList.setItemSize(new CGSize(32, 32));
        fileList.setBackgroundColor(0);
        fileList.setShowsName(false);
        fileList.setSelectedItem(selectedPart);
        fileList.setItemSelector(this::selectPartAction);
        itemLists.add(fileList);
        return fileList;
    }

    private boolean isValid(PartItem item) {
        return filter == null || filter.test(item.getDescriptor());
    }

    private ArrayList<PartItem> getInventorySkins() {
        ArrayList<PartItem> allSkins = new ArrayList<>();
        Player player = EnvironmentManager.getPlayer();
        if (player == null) {
            return allSkins;
        }
        Inventory inventory = player.getInventory();
        int containerSize = inventory.getContainerSize();
        for (int i = 0; i < containerSize; ++i) {
            ItemStack itemStack = inventory.getItem(i);
            SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
            if (descriptor.isEmpty()) {
                continue;
            }
            allSkins.add(new PartItem(descriptor, itemStack));
        }
        return allSkins;
    }

    private ArrayList<PartItem> getImportedSkins() {
        ArrayList<PartItem> allSkins = new ArrayList<>();
        if (historySkins != null) {
            historySkins.forEach(it -> allSkins.add(new PartItem(it)));
            return allSkins;
        }
        return allSkins;
    }
}
