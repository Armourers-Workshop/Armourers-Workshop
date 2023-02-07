package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.gui.widget.EntityPreviewView;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.PlayerInventoryView;
import moe.plushie.armourers_workshop.core.client.gui.widget.TabView;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModContributors;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeWindow<M extends SkinWardrobeMenu> extends MenuWindow<M> {

    private final Entity entity;
    private final Player operator;
    private final SkinWardrobe wardrobe;

    private final TabView tabView = new TabView(new CGRect(0, 0, 278, 151));

    public SkinWardrobeWindow(M menu, Inventory inventory, NSString title) {
        super(menu, inventory, title);
        this.setFrame(new CGRect(0, 0, 278, 250));
        this.inventoryView.setStyle(PlayerInventoryView.Style.NORMAL);

        this.operator = inventory.player;
        this.operator.containerMenu = menu;

        this.entity = menu.getEntity();
        this.wardrobe = menu.getWardrobe();
    }

    @Override
    public void init() {
        setupBackgroundView();
        super.init();
        setupTabView();
        setupForegroundView();
        // adjust the title and inventory to top.
        bringSubviewToFront(titleView);
        bringSubviewToFront(inventoryView);
    }

    private void setupTabView() {
        boolean isPlayer = entity instanceof Player;
        boolean isMannequin = entity instanceof MannequinEntity;

        addTab(new SkinWardrobeInventorySetting(menu))
                .setIcon(tabIcon(192, 0))
                .setTarget(SkinWardrobeMenu.Group.SKINS)
                .setActive(!isPlayer || ModConfig.Common.showWardrobeSkins || operator.isCreative());

        if (wardrobe.getUnlockedSize(SkinSlotType.OUTFIT) != 0) {
            addTab(new SkinWardrobeOutfitSetting(menu))
                    .setIcon(tabIcon(0, 128))
                    .setTarget(SkinWardrobeMenu.Group.OUTFITS)
                    .setActive(!isPlayer || ModConfig.Common.showWardrobeOutfits || operator.isCreative());
        }

        addTab(new SkinWardrobeDisplaySetting(wardrobe))
                .setIcon(tabIcon(208, 0))
                .setActive(!isPlayer || ModConfig.Common.showWardrobeDisplaySettings || operator.isCreative());

        addTab(new SkinWardrobeColorSetting(wardrobe))
                .setIcon(tabIcon(224, 0))
                .setTarget(SkinWardrobeMenu.Group.COLORS)
                .setActive(!isPlayer || ModConfig.Common.showWardrobeColorSettings || operator.isCreative());

        addTab(new SkinWardrobeDyeSetting(menu))
                .setIcon(tabIcon(240, 0))
                .setTarget(SkinWardrobeMenu.Group.DYES)
                .setActive(!isPlayer || ModConfig.Common.showWardrobeDyeSetting || operator.isCreative());

        if (isPlayer && ModContributors.getCurrentContributor() != null) {
            addTab(new SkinWardrobeContributorSetting(wardrobe))
                    .setIcon(tabIcon(32, 128))
                    .setActive(ModConfig.Common.showWardrobeContributorSetting || operator.isCreative());
        }

        if (isMannequin) {
            addTab(new SkinWardrobeRotationSetting(wardrobe, entity))
                    .setIcon(tabIcon(80, 0, 8, 150))
                    .setAlignment(1);

            addTab(new SkinWardrobeTextureSetting(wardrobe))
                    .setIcon(tabIcon(128, 0, 8, 150))
                    .setAlignment(1);

            addTab(new SkinWardrobeExtraSetting(wardrobe))
                    .setIcon(tabIcon(144, 0, 8, 150))
                    .setAlignment(1);

            addTab(new SkinWardrobeLocationSetting(wardrobe, entity))
                    .setIcon(tabIcon(96, 0, 8, 150))
                    .setAlignment(1);
        }

        tabView.addTarget(this, SkinWardrobeWindow::switchTab);
        tabView.setSelectedTab(tabView.firstActiveTab()); // active the first tab
        addSubview(tabView);
    }

    private void setupBackgroundView() {
        UIView bg1 = new UIView(new CGRect(0, 0, 256, 151));
        UIView bg2 = new UIView(new CGRect(256, 0, 22, 151));
        bg1.setContents(UIImage.of(ModTextures.WARDROBE_1).build());
        bg2.setContents(UIImage.of(ModTextures.WARDROBE_2).build());
        tabView.insertViewAtIndex(bg2, 0);
        tabView.insertViewAtIndex(bg1, 0);
    }

    private void setupForegroundView() {
        tabView.addSubview(inventoryView);

        EntityPreviewView entityView = new EntityPreviewView(new CGRect(8, 27, 71, 111));
        entityView.setContents(entity);
        tabView.addSubview(entityView);
    }

    private void switchTab(TabView.Entry entry) {
        menu.setGroup(ObjectUtils.safeCast(entry.tag(), SkinWardrobeMenu.Group.class));
        inventoryView.setHidden(!menu.shouldRenderInventory());
    }

    private TabView.EntryBuilder addTab(SkinWardrobeBaseSetting contentView) {
        return tabView.addContentView(contentView).setTooltip(contentView.getTitle());
    }

    private UIImage tabIcon(int u, int v) {
        return UIImage.of(ModTextures.TAB_ICONS).uv(u, v).size(16, 16).build();
    }

    private UIImage tabIcon(int u, int v, int frame, int speed) {
        return UIImage.of(ModTextures.TAB_ICONS).uv(u, v).size(16, 16).animation(frame, speed).build();
    }
}
