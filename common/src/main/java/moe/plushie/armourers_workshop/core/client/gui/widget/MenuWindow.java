package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.api.common.IMenuWindow;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MenuWindow<M extends AbstractContainerMenu> extends UIWindow implements IMenuWindow<M> {

    protected final M menu;
    protected final NSString title;
    protected final Inventory inventory;

    protected final UILabel titleView = new UILabel(new CGRect(0, 8, 176, 9));
    protected final PlayerInventoryView inventoryView = new PlayerInventoryView(new CGRect(0, 0, 176, 98));

    public MenuWindow(M menu, Inventory inventory, NSString title) {
        super(new CGRect(0, 0, 176, 98));
        this.menu = menu;
        this.title = title;
        this.inventory = inventory;
        this.setupDefaultView();
    }

    @Override
    public boolean pointInside(CGPoint point, UIEvent event) {
        if (super.pointInside(point, event)) {
            return true;
        }
        // a special case where we allow subviews events to be outside the window.
        return subviews().stream().anyMatch(subview -> subview.pointInside(convertPointToView(point, subview), event));
    }

    public boolean shouldDrawBackground() {
        return true;
    }

    public boolean shouldDrawPluginScreen() {
        return false;
    }

    private void setupDefaultView() {
        titleView.setText(title);
        titleView.setTextHorizontalAlignment(NSTextAlignment.Horizontal.CENTER);
        titleView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        addSubview(titleView);

        inventoryView.setName(new NSString(inventory.getDisplayName()));
        inventoryView.setStyle(PlayerInventoryView.Style.NONE);
        inventoryView.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleTopMargin);
        addSubview(inventoryView);
    }
}
