package moe.plushie.armourers_workshop.core.client.gui.hologramprojector;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.*;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.PlayerInventoryView;
import moe.plushie.armourers_workshop.core.client.gui.widget.TabView;
import moe.plushie.armourers_workshop.core.menu.HologramProjectorMenu;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class HologramProjectorWindow extends MenuWindow<HologramProjectorMenu> {

    private final TabView tabView = new TabView(CGRect.ZERO);

    private final HologramProjectorBlockEntity tileEntity;

    public HologramProjectorWindow(HologramProjectorMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.titleView.setFrame(titleView.bounds().offset(0, 5));
        this.inventoryView.setStyle(PlayerInventoryView.Style.NORMAL);
        this.tileEntity = container.getTileEntity();
    }

    @Override
    public void init() {
        super.init();
        setupTabView();
        // adjust the title and inventory to top.
        bringSubviewToFront(titleView);
        bringSubviewToFront(inventoryView);
    }

    @Override
    public void screenWillResize(CGSize size) {
        setFrame(new CGRect(0, 0, size.width, size.height));
        tabView.setFrame(bounds());
        CGRect inv = inventoryView.frame();
        menu.reload(inv.x, inv.y, size.width, size.height);
    }

    private void setupTabView() {
        addTab(new HologramProjectorInventorySetting(tileEntity))
                .setIcon(tabIcon(64, 0, 8, 150))
                .setTarget(1);

        addTab(new HologramProjectorOffsetSetting(tileEntity))
                .setIcon(tabIcon(96, 0, 8, 150));

        addTab(new HologramProjectorAngleSetting(tileEntity))
                .setIcon(tabIcon(176, 0, 8, 150));

        addTab(new HologramProjectorRotationOffsetSetting(tileEntity))
                .setIcon(tabIcon(80, 0, 8, 150));

        addTab(new HologramProjectorRotationSpeedSetting(tileEntity))
                .setIcon(tabIcon(160, 0, 4, 150));

        addTab(new HologramProjectorExtraSetting(tileEntity))
                .setIcon(tabIcon(144, 0, 8, 150));

        tabView.addTarget(this, HologramProjectorWindow::switchTab);
        tabView.setSelectedTab(tabView.firstActiveTab()); // active the first tab
        tabView.setFullscreenMode(true);
        addSubview(tabView);
    }

    private void switchTab(TabView.Entry entry) {
        int group = 0;
        Object value = entry.tag();
        if (value != null) {
            group = (int)value;
        }
        menu.setGroup(group);
        inventoryView.setHidden(!menu.shouldRenderInventory());
    }

    private TabView.EntryBuilder addTab(HologramProjectorBaseSetting contentView) {
        WrappedContainer containerView = new WrappedContainer(contentView);
        return tabView.addContentView(containerView).setTooltip(contentView.getTitle());
    }

    private UIImage tabIcon(int u, int v, int frame, int speed) {
        return UIImage.of(ModTextures.TAB_ICONS).uv(u, v).size(16, 16).animation(frame, speed).build();
    }

    @Override
    public boolean shouldRenderBackground() {
        return false;
    }

    public static class WrappedContainer extends UIView {

        private final HologramProjectorBaseSetting setting;

        public WrappedContainer(HologramProjectorBaseSetting setting) {
            super(CGRect.ZERO);
            this.setting = setting;
            this.addSubview(setting);
        }

        @Override
        public void layoutSubviews() {
            super.layoutSubviews();
            setting.setOrigin(new CGPoint(bounds().getWidth() / 2, setting.bounds().getHeight() / 2));
        }

        @Nullable
        @Override
        public UIView hitTest(CGPoint point, UIEvent event) {
            UIView target = super.hitTest(point, event);
            if (target != this) {
                return target;
            }
            return null;
        }
    }
}
