package moe.plushie.armourers_workshop.core.gui.wardrobe;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.container.SkinWardrobeContainer;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class SkinWardrobeDisplaySetting extends AWTabPanel {

    private final SkinWardrobe wardrobe;

    public SkinWardrobeDisplaySetting(SkinWardrobeContainer container) {
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
                NetworkHandler.getInstance().sendToServer(UpdateWardrobePacket.field(wardrobe, option, newValue));
            }
        }));
    }
}
