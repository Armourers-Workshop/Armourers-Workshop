package moe.plushie.armourers_workshop.core.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.PlayerInventoryView;
import moe.plushie.armourers_workshop.core.client.gui.widget.SlotGridView;
import moe.plushie.armourers_workshop.core.menu.SkinnableMenu;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class SkinnableWindow extends MenuWindow<SkinnableMenu> {

    public SkinnableWindow(SkinnableMenu container, Inventory inventory, NSString title) {
        super(container, inventory, new NSString(container.getInventoryName()));
        this.setFrame(new CGRect(0, 0, 176, container.getRow() * 18 + 124));
        this.inventoryView.setStyle(PlayerInventoryView.Style.COMPACT);
        this.setup();
    }

    private void setup() {
        SlotGridView gridView = new SlotGridView(bounds());
        gridView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        gridView.reloadData(menu.slots, 36, menu.slots.size());
        gridView.setContents(ModTextures.defaultWindowImage());
        insertViewAtIndex(gridView, 0);
    }

    @Override
    public boolean shouldRenderExtendScreen() {
        return true;
    }
}
