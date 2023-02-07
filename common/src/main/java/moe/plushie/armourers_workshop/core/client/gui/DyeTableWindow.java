package moe.plushie.armourers_workshop.core.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinPreviewView;
import moe.plushie.armourers_workshop.core.menu.DyeTableMenu;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

@SuppressWarnings({"unused"})
@Environment(value = EnvType.CLIENT)
public class DyeTableWindow extends MenuWindow<DyeTableMenu> {

    private final SkinPreviewView previewView = new SkinPreviewView(new CGRect(174, 23, 148, 159));

    public DyeTableWindow(DyeTableMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setFrame(new CGRect(0, 0, 330, 190));
        this.inventoryView.setFrame(inventoryView.bounds().offset(0, bounds().getHeight() - 98));
        this.menuDidChange();
    }

    @Override
    public void menuDidChange() {
        super.menuDidChange();
        previewView.setSkin(SkinDescriptor.of(menu.getOutputStack()));
    }

    @Override
    public void init() {
        UIView bg1 = new UIView(new CGRect(0, 0, 256, 190));
        UIView bg2 = new UIView(new CGRect(174 + 74, 0, 82, 190));
        bg1.setContents(UIImage.of(ModTextures.DYE_TABLE).build());
        bg2.setContents(UIImage.of(ModTextures.DYE_TABLE).uv(174, 0).build());
        insertViewAtIndex(bg2, 0);
        insertViewAtIndex(bg1, 0);
        super.init();
        addSubview(previewView);
    }
}
