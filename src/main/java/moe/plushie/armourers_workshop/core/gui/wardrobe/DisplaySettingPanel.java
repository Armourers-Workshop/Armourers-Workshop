package moe.plushie.armourers_workshop.core.gui.wardrobe;

import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeOption;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class DisplaySettingPanel extends BaseSettingPanel {

    private final SkinWardrobe wardrobe;

    public DisplaySettingPanel(SkinWardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.display_settings");
        this.wardrobe = container.getWardrobe();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        addOption(leftPos + 83, topPos + 27, SkinWardrobeOption.ARMOUR_HEAD, "renderHeadArmour");
        addOption(leftPos + 83, topPos + 47, SkinWardrobeOption.ARMOUR_CHEST, "renderChestArmour");
        addOption(leftPos + 83, topPos + 67, SkinWardrobeOption.ARMOUR_LEGS, "renderLegArmour");
        addOption(leftPos + 83, topPos + 87, SkinWardrobeOption.ARMOUR_FEET, "renderFootArmour");
    }

    private void addOption(int x, int y, SkinWardrobeOption option, String key) {
        addButton(new AWCheckBox(x, y, 9, 9, getDisplayText(key), option.get(wardrobe, false), button -> {
            if (button instanceof AWCheckBox) {
                boolean newValue = ((AWCheckBox) button).isSelected();
                NetworkHandler.getInstance().sendToServer(UpdateWardrobePacket.opt(wardrobe, option, newValue));
            }
        }));
    }
}
