package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeExtraSetting extends SkinWardrobeBaseSetting {

    private final SkinWardrobe wardrobe;

    public SkinWardrobeExtraSetting(SkinWardrobe wardrobe) {
        super("inventory.armourers_workshop.wardrobe.man_extras");
        this.wardrobe = wardrobe;
        this.setup();
    }

    private void setup() {
        setupOptionView(83, 27, UpdateWardrobePacket.Field.MANNEQUIN_IS_CHILD, "label.isChild");
        setupOptionView(83, 47, UpdateWardrobePacket.Field.MANNEQUIN_EXTRA_RENDER, "label.isExtraRenders");
        setupOptionView(83, 67, UpdateWardrobePacket.Field.MANNEQUIN_IS_FLYING, "label.isFlying");
        setupOptionView(83, 87, UpdateWardrobePacket.Field.MANNEQUIN_IS_VISIBLE, "label.isVisible");
        setupOptionView(83, 107, UpdateWardrobePacket.Field.MANNEQUIN_IS_GHOST, "label.noclip");
    }

    private void setupOptionView(int x, int y, UpdateWardrobePacket.Field option, String key) {
        UICheckBox checkBox = new UICheckBox(new CGRect(x, y, 185, 10));
        checkBox.setTitle(getDisplayText(key));
        checkBox.setSelected(option.get(wardrobe, true));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = ObjectUtils.unsafeCast(c);
            NetworkManager.sendToServer(UpdateWardrobePacket.field(self.wardrobe, option, checkBox1.isSelected()));
        });
        addSubview(checkBox);
    }
}
