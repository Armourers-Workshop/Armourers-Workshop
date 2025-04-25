package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkinWardrobeExtraSetting extends SkinWardrobeBaseSetting {

    private final SkinWardrobe wardrobe;

    public SkinWardrobeExtraSetting(SkinWardrobe wardrobe) {
        super("wardrobe.man_extras");
        this.wardrobe = wardrobe;
        this.setup();
    }

    private void setup() {
        setupOptionView(0, UpdateWardrobePacket.Field.MANNEQUIN_IS_CHILD, "label.isChild");
        setupOptionView(1, UpdateWardrobePacket.Field.MANNEQUIN_EXTRA_RENDER, "label.isExtraRenders");
        setupOptionView(2, UpdateWardrobePacket.Field.MANNEQUIN_IS_FLYING, "label.isFlying");
        setupOptionView(3, UpdateWardrobePacket.Field.MANNEQUIN_IS_VISIBLE, "label.isVisible");
        setupOptionView(4, UpdateWardrobePacket.Field.MANNEQUIN_IS_GHOST, "label.noClip");
        setupOptionView(5, UpdateWardrobePacket.Field.MANNEQUIN_NO_GRAVITY, "label.noGravity");
    }

    private void setupOptionView(int index, UpdateWardrobePacket.Field<Boolean> option, String key) {
        var checkBox = new UICheckBox(new CGRect(83, 27 + 14 * index, 185, 10));
        checkBox.setTitle(getDisplayText(key));
        checkBox.setSelected(option.getOrDefault(wardrobe, true));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = Objects.unsafeCast(c);
            NetworkManager.sendToServer(option.buildPacket(self.wardrobe, checkBox1.isSelected()));
        });
        addSubview(checkBox);
    }
}
