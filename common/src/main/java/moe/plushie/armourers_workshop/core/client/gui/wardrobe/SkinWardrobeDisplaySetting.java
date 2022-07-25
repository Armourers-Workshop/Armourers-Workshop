package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeDisplaySetting extends AWTabPanel {

    private final SkinWardrobe wardrobe;

    public SkinWardrobeDisplaySetting(SkinWardrobeMenu container) {
        super("inventory.armourers_workshop.wardrobe.display_settings");
        this.wardrobe = container.getWardrobe();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        addOption(leftPos + 83, topPos + 27, UpdateWardrobePacket.Field.WARDROBE_ARMOUR_HEAD, "renderHeadArmour");
        addOption(leftPos + 83, topPos + 47, UpdateWardrobePacket.Field.WARDROBE_ARMOUR_CHEST, "renderChestArmour");
        addOption(leftPos + 83, topPos + 67, UpdateWardrobePacket.Field.WARDROBE_ARMOUR_LEGS, "renderLegArmour");
        addOption(leftPos + 83, topPos + 87, UpdateWardrobePacket.Field.WARDROBE_ARMOUR_FEET, "renderFootArmour");
    }

    private void addOption(int x, int y, UpdateWardrobePacket.Field option, String key) {
        addButton(new AWCheckBox(x, y, 9, 9, getDisplayText(key), option.get(wardrobe, true), button -> {
            if (button instanceof AWCheckBox) {
                boolean newValue = ((AWCheckBox) button).isSelected();
                NetworkManager.sendToServer(UpdateWardrobePacket.field(wardrobe, option, newValue));
            }
        }));
    }
}
