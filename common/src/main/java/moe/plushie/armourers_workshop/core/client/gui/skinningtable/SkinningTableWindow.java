package moe.plushie.armourers_workshop.core.client.gui.skinningtable;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.menu.SkinningTableMenu;
import moe.plushie.armourers_workshop.core.network.UpdateSkinningTablePacket;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class SkinningTableWindow extends MenuWindow<SkinningTableMenu> {

    public SkinningTableWindow(SkinningTableMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setFrame(new CGRect(0, 0, 176, 176));
        this.setBackgroundView(UIImage.of(ModTextures.SKINNING_TABLE).build());
        this.setup();
    }

    private void setup() {
        float x = bounds().getWidth() - 24 - 4;
        UIButton settingView = new UIButton(new CGRect(x, 4, 24, 16));
        settingView.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleBottomMargin);
        settingView.setContents(UIImage.of(ModTextures.SKINNING_TABLE).uv(228, 0).fixed(24, 16).build());
        settingView.addTarget(this, UIControl.Event.MOUSE_LEFT_UP, (self, sender) -> {
            var alert = new SkinningTableSettingWindow(menu.getBlockEntity().getOptions());
            alert.setTitle(NSString.localizedString("skinning-table.setting.title"));
            alert.sizeToFit();
            alert.showInView(this, () -> {
                if (!alert.isCancelled()) {
                    NetworkManager.sendToServer(new UpdateSkinningTablePacket(menu.getBlockEntity(), alert.getOptions()));
                }
            });
        });
        addSubview(settingView);
    }

    @Override
    public boolean shouldRenderExtendScreen() {
        return true;
    }
}
