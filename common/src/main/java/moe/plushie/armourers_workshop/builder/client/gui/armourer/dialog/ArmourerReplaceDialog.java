package moe.plushie.armourers_workshop.builder.client.gui.armourer.dialog;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.PlayerInventoryView;
import moe.plushie.armourers_workshop.core.client.gui.widget.SlotListView;
import moe.plushie.armourers_workshop.core.menu.AbstractContainerMenu;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ArmourerReplaceDialog extends ConfirmDialog {

    private final Inventory playerInventory;
    private final Container inventory;

    private final UICheckBox keepPaintBox = new UICheckBox(CGRect.ZERO);
    private final UICheckBox keepColorBox = new UICheckBox(CGRect.ZERO);

    private final SlotListView<PickerContainer> listView;
    private final PlayerInventoryView inventoryView = new PlayerInventoryView(new CGRect(0, 0, 176, 98));

    public ArmourerReplaceDialog() {
        super();
        this.setFrame(new CGRect(0, 0, 240, 130));
        Player player = EnvironmentManager.getPlayer();
        this.playerInventory = player.getInventory();
        this.inventory = createBackup(playerInventory);
        this.listView = new SlotListView<>(new PickerContainer(inventory), playerInventory, bounds());
        this.setup();
    }

    private void setup() {
        layoutIfNeeded();
        float left = confirmButton.frame().getX() + 1;
        float centerX = cancelButton.frame().getX() + 1;
        float bottom = confirmButton.frame().getY() - 4;
        float width = bounds().width - 30;
        float height = bounds().getHeight() + 10 + 98;

        setupBackgroundView(left, centerX, height);

        keepColorBox.setFrame(new CGRect(left, bottom - 22, width, 9));
        keepColorBox.setTitle(NSString.localizedString("armourer.dialog.replace.keepColor"));
        keepColorBox.setSelected(false);
        addSubview(keepColorBox);

        keepPaintBox.setFrame(new CGRect(left, bottom - 11, width, 9));
        keepPaintBox.setTitle(NSString.localizedString("armourer.dialog.replace.keepPaint"));
        keepPaintBox.setSelected(false);
        addSubview(keepPaintBox);

        bringSubviewToFront(confirmButton);
        bringSubviewToFront(cancelButton);

        listView.setFrame(new CGRect(0, 0, bounds().getWidth(), height));
        listView.getMenu().reloadSlots(inventoryView.frame(), new CGRect(left + 32, 44, 0, 0));
        addSubview(listView);
    }

    private void setupBackgroundView(float left, float center, float height) {
        UILabel label1 = new UILabel(new CGRect(left + 8, 25, 100, 9));
        UILabel label2 = new UILabel(new CGRect(center + 8, 25, 100, 9));
        label1.setText(NSString.localizedString("armourer.dialog.replace.srcBlock"));
        label2.setText(NSString.localizedString("armourer.dialog.replace.desBlock"));
        addSubview(label1);
        addSubview(label2);

        float placeholderX = left + 32;
        float placeholderY = 44;
        UIImageView slot1 = new UIImageView(new CGRect(placeholderX - 5, placeholderY - 5, 26, 26));
        UIImageView slot2 = new UIImageView(new CGRect(placeholderX - 5 + 110, placeholderY - 5, 26, 26));
        slot1.setImage(UIImage.of(ModTextures.ARMOURER).uv(230, 18).build());
        slot2.setImage(UIImage.of(ModTextures.ARMOURER).uv(230, 18).build());
        addSubview(slot1);
        addSubview(slot2);

        inventoryView.setFrame(new CGRect(32, height - 98, 176, 98));
        inventoryView.setName(new NSString(playerInventory.getDisplayName()));
        inventoryView.setStyle(PlayerInventoryView.Style.NORMAL);
        inventoryView.setUserInteractionEnabled(false);
        addSubview(inventoryView);

        addHelpButton(left, 25, "armourer.dialog.replace.help.selector");
        addHelpButton(center, 25, "armourer.dialog.replace.help.applier");
    }

    private Container createBackup(Inventory inventory) {
        int size = inventory.getContainerSize();
        Container newInventory = new SimpleContainer(size + 2);
        for (int i = 0; i < size; ++i) {
            newInventory.setItem(i, inventory.getItem(i).copy());
        }
        return newInventory;
    }


    public boolean isKeepColor() {
        return keepColorBox.isSelected();
    }

    public boolean isKeepPaintType() {
        return keepPaintBox.isSelected();
    }

    public ItemStack getSelector() {
        return inventory.getItem(inventory.getContainerSize() - 2);
    }

    public ItemStack getApplier() {
        return inventory.getItem(inventory.getContainerSize() - 1);
    }

    @Override
    public boolean pointInside(CGPoint point, UIEvent event) {
        if (super.pointInside(point, event)) {
            return true;
        }
        return subviews().stream().anyMatch(subview -> subview.pointInside(convertPointToView(point, subview), event));
    }

    @Nullable
    @Override
    public UIView hitTest(CGPoint point, UIEvent event) {
        if (_ignoresTouchEvents(this)) {
            return null;
        }
        if (!pointInside(point, event)) {
            return null;
        }
        for (UIView subview : _invertedSubviews()) {
            if (subview != listView) {
                UIView hitView = subview.hitTest(convertPointToView(point, subview), event);
                if (hitView != null) {
                    return hitView;
                }
            }
        }
        UIView hitView = listView.hitTest(convertPointToView(point, listView), event);
        if (hitView != null) {
            return hitView;
        }
        return this;
    }

    @Override
    public void setCenter(CGPoint newValue) {
        int extendHeight = 98 + 10;
        super.setCenter(new CGPoint(newValue.x, newValue.y - extendHeight / 2));
        if (listView != null) {
            listView.setNeedsLayout();
        }
    }

    private void addHelpButton(float x, float y, String tooltipKey) {
        UIButton button = new UIButton(new CGRect(x, y, 7, 8));
        button.setBackgroundImage(ModTextures.helpButtonImage(), UIControl.State.ALL);
        button.setTooltip(NSString.localizedString(tooltipKey));
        button.setCanBecomeFocused(false);
        addSubview(button);
    }

    static class PickerContainer extends AbstractContainerMenu {

        Container inventory;

        protected PickerContainer(Container inventory) {
            super(null, 0);
            this.inventory = inventory;
        }

        @Override
        public void removed(Player player) {
            super.removed(player);
            player.getInventory().setCarried(ItemStack.EMPTY);
        }

        @Override
        public boolean stillValid(Player p_75145_1_) {
            return true;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return quickMoveStack(player, index, slots.size());
        }

        protected void reloadSlots(CGRect inventoryRect, CGRect placeholderRect) {
            slots.clear();
            addPlayerSlots(inventory, (int) inventoryRect.x + 8, (int) inventoryRect.y + 16);
            addPlaceholderSlots(inventory, inventory.getContainerSize() - 2, (int) placeholderRect.x, (int) placeholderRect.y);
        }

        protected void addPlaceholderSlots(Container inventory, int offset, int placeholderX, int placeholderY) {
            for (int i = 0; i < 2; ++i) {
                addSlot(new Slot(inventory, offset + i, placeholderX + i * 110, placeholderY) {
                    @Override
                    public boolean mayPlace(ItemStack itemStack) {
                        return itemStack.getItem() instanceof IItemColorProvider;
                    }
                });
            }
        }
    }
}
