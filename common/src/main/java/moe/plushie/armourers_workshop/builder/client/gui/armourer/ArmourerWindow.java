package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.PlayerInventoryView;
import moe.plushie.armourers_workshop.core.client.gui.widget.TabView;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class ArmourerWindow extends MenuWindow<ArmourerMenu> {

    private final ArmourerBlockEntity blockEntity;

    private final TabView tabView = new TabView(new CGRect(0, 0, 176, 224));

    private int lastVersion = 0;

    public ArmourerWindow(ArmourerMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setFrame(new CGRect(0, 0, 176, 224));
        this.setBackgroundView(ModTextures.defaultWindowImage());
        this.inventoryView.setStyle(PlayerInventoryView.Style.COMPACT);
        this.inventoryView.setUserInteractionEnabled(false);
        this.blockEntity = container.getBlockEntity();
    }

    @Override
    public void init() {
        super.init();
        setupTabView();
        // adjust the title and inventory to top.
        bringSubviewToFront(titleView);
        bringSubviewToFront(inventoryView);
    }

    private void setupTabView() {
        addTab(new ArmourerMainSetting(menu))
                .setTarget(ArmourerMenu.Group.MAIN)
                .setIcon(tabIcon(0, 0, 8, 150));

        addTab(new ArmourerDisplaySetting(menu))
                .setTarget(ArmourerMenu.Group.DISPLAY)
                .setIcon(tabIcon(16, 0, 8, 150));

        addTab(new ArmourerSkinSetting(menu))
                .setTarget(ArmourerMenu.Group.SKIN)
                .setIcon(tabIcon(32, 0, 8, 150));

        addTab(new ArmourerBlockSetting(menu))
                .setTarget(ArmourerMenu.Group.BLOCK)
                .setIcon(tabIcon(48, 0, 8, 150));

        tabView.addTarget(this, ArmourerWindow::switchTab);
        tabView.setSelectedTab(tabView.firstActiveTab()); // active the first tab
        addSubview(tabView);
    }

    @Override
    public void screenWillTick() {
        super.screenWillTick();
        int lastVersion = blockEntity.getVersion();
        if (this.lastVersion != lastVersion) {
            tabView.tabs().forEach(tab -> {
                ArmourerBaseSetting setting = ObjectUtils.safeCast(tab.contentView(), ArmourerBaseSetting.class);
                if (setting != null) {
                    setting.reloadData();
                }
            });
            this.lastVersion = lastVersion;
        }
    }

    private void switchTab(TabView.Entry entry) {
        ArmourerMenu.Group group = ArmourerMenu.Group.MAIN;
        Object value = entry.target();
        if (value != null) {
            group = (ArmourerMenu.Group)value;
        }
        menu.setGroup(group);
        inventoryView.setHidden(!menu.shouldRenderInventory());
        ArmourerBaseSetting setting = ObjectUtils.safeCast(entry.contentView(), ArmourerBaseSetting.class);
        if (setting != null) {
            setting.reloadData();
        }
    }

    private TabView.EntryBuilder addTab(ArmourerBaseSetting contentView) {
        contentView.init();
        return tabView.addContentView(contentView).setTooltip(contentView.getTitle());
    }

    private UIImage tabIcon(int u, int v, int frame, int speed) {
        return UIImage.of(ModTextures.TAB_ICONS).uv(u, v).fixed(16, 16).animation(frame, speed).build();
    }
}
