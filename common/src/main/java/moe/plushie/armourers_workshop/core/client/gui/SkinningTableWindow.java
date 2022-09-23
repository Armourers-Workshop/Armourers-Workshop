package moe.plushie.armourers_workshop.core.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.menu.SkinningTableMenu;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

@Environment(value = EnvType.CLIENT)
public class SkinningTableWindow extends MenuWindow<SkinningTableMenu> {

    public SkinningTableWindow(SkinningTableMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setFrame(new CGRect(0, 0, 176, 176));
        this.setBackgroundView(UIImage.of(ModTextures.SKINNING_TABLE).build());
    }

    @Override
    public boolean shouldRenderExtendScreen() {
        return true;
    }
}
