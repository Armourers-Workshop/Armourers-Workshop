package moe.plushie.armourers_workshop.core.client.gui.hologramprojector;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.*;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.network.UpdateHologramProjectorPacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class HologramProjectorExtraSetting extends HologramProjectorBaseSetting {

    private final HologramProjectorBlockEntity entity;
    private final UpdateHologramProjectorPacket.Field field = UpdateHologramProjectorPacket.Field.POWER_MODE;
    private final UpdateHologramProjectorPacket.Field field2 = UpdateHologramProjectorPacket.Field.IS_GLOWING;

    public HologramProjectorExtraSetting(HologramProjectorBlockEntity entity) {
        super("inventory.armourers_workshop.hologram-projector.extra");
        this.entity = entity;
        this.setFrame(new CGRect(0, 0, 200, 78));
        this.setup();
    }

    private void setup() {
        setupOption(11, 30, field2, "glowing");
        setupComboList(11, 55, field);

        UILabel label = new UILabel(new CGRect(11, 45, 178, 9));
        label.setText(getDisplayText("powerMode"));
        addSubview(label);
    }

    private void setupOption(int x, int y, UpdateHologramProjectorPacket.Field field, String key) {
        UICheckBox checkBox = new UICheckBox(new CGRect(x, y, 178, 10));
        checkBox.setTitle(getDisplayText(key));
        checkBox.setSelected(field.get(entity));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = ObjectUtils.unsafeCast(c);
            field.set(entity, checkBox1.isSelected());
            UpdateHologramProjectorPacket packet = new UpdateHologramProjectorPacket(entity, field, checkBox1.isSelected());
            NetworkManager.sendToServer(packet);
        });
        addSubview(checkBox);
    }

    private void setupComboList(int x, int y, UpdateHologramProjectorPacket.Field field) {
        ArrayList<UIComboItem> items = new ArrayList<>();
        items.add(new UIComboItem(getDisplayText("powerMode.ignored")));
        items.add(new UIComboItem(getDisplayText("powerMode.high")));
        items.add(new UIComboItem(getDisplayText("powerMode.low")));
        UIComboBox comboBox = new UIComboBox(new CGRect(x, y, 80, 14));
        comboBox.setSelectedIndex(field.get(entity));
        comboBox.reloadData(items);
        comboBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, e) -> {
            UIComboBox comboBox1 = ObjectUtils.unsafeCast(e);
            field.set(entity, comboBox1.selectedIndex());
            UpdateHologramProjectorPacket packet = new UpdateHologramProjectorPacket(entity, field, comboBox1.selectedIndex());
            NetworkManager.sendToServer(packet);
        });
        addSubview(comboBox);
    }
}
