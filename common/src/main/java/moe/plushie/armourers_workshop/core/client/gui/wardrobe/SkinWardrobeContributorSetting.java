package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkinWardrobeContributorSetting extends SkinWardrobeBaseSetting {

    private final UILabel label = new UILabel(new CGRect(85, 26, 185, 100));
    private final SkinWardrobe wardrobe;

    public SkinWardrobeContributorSetting(SkinWardrobe wardrobe) {
        super("wardrobe.contributor");
        this.wardrobe = wardrobe;
        this.setup();
    }

    private void setup() {
        NSMutableString thanks = new NSMutableString("");
        thanks.append(getDisplayText("label.contributor"));
        thanks.append("\n\n\nOptions coming here soon!");
        this.label.setText(thanks);
        this.label.setNumberOfLines(0);
        this.label.setLineSpacing(1);
        this.label.setTextHorizontalAlignment(NSTextAlignment.Horizontal.LEFT);
        this.label.setTextVerticalAlignment(NSTextAlignment.Vertical.TOP);
        this.addSubview(label);

        var option = UpdateWardrobePacket.Field.WARDROBE_EXTRA_RENDER;
        var checkBox = new UICheckBox(new CGRect(85, 128, 185, 10));
        checkBox.setTitle(getDisplayText("label.enableContributorMagic"));
        checkBox.setSelected(option.getOrDefault(wardrobe, true));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = ObjectUtils.unsafeCast(c);
            NetworkManager.sendToServer(option.buildPacket(self.wardrobe, checkBox1.isSelected()));
        });
        this.addSubview(checkBox);
    }
}
