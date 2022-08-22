package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeLocationSetting extends SkinWardrobeBaseSetting {

    private final float[] steps = {1.0f, 1.0f / 8.0f, 1.0f / 16.0f};
    private final SkinWardrobe wardrobe;
    private final Entity entity;

    public SkinWardrobeLocationSetting(SkinWardrobe wardrobe, Entity entity) {
        super("inventory.armourers_workshop.wardrobe.man_offsets");
        this.wardrobe = wardrobe;
        this.entity = entity;
        this.setup();
    }

    private void setup() {
        setupLabel(146, 29, "X");
        setupLabel(146, 49, "Y");
        setupLabel(146, 69, "Z");
        int x = 83;
        int y = 25;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                setupButton(x + i * 20, y + j * 20, 208, 80, j, -steps[i], "button.sub." + -(i - 3));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                setupButton(x + 77 + i * 20, y + j * 20, 208, 96, j, steps[3 - i - 1], "button.add." + (i + 1));
            }
        }
    }

    private void setupLabel(int x, int y, String text) {
        UILabel label = new UILabel(new CGRect(x, y, 10, 9));
        label.setText(new NSString(text));
        label.setTextColor(new UIColor(0x333333));
        addSubview(label);
    }

    private void setupButton(int x, int y, int u, int v, int axis, float step, String key) {
        UIButton button = new UIButton(new CGRect(x, y, 16, 16));
        button.setBackgroundImage(ModTextures.defaultButtonImage(u, v), UIControl.State.ALL);
        button.setTooltip(getDisplayText(key));
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, (self, e) -> {
            self.updateValue(axis, step);
        });
        addSubview(button);
    }

    private void updateValue(int axis, float step) {
        MannequinEntity entity = ObjectUtils.safeCast(this.entity, MannequinEntity.class);
        if (entity == null) {
            return;
        }
        Vec3 pos = entity.position();
        double[] xyz = {pos.x(), pos.y(), pos.z()};
        xyz[axis] += step;
        pos = new Vec3(xyz[0], xyz[1], xyz[2]);
        UpdateWardrobePacket packet = UpdateWardrobePacket.field(wardrobe, UpdateWardrobePacket.Field.MANNEQUIN_POSITION, pos);
        NetworkManager.sendToServer(packet);
    }
}
