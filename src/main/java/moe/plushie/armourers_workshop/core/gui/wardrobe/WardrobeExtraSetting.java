package moe.plushie.armourers_workshop.core.gui.wardrobe;

import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.wardrobe.Wardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.WardrobeContainer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class WardrobeExtraSetting extends AWTabPanel {

    private final Wardrobe wardrobe;

    public WardrobeExtraSetting(WardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.man_extras");
        this.wardrobe = container.getWardrobe();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        addOption(leftPos + 83, topPos + 27, UpdateWardrobePacket.Field.MANNEQUIN_IS_CHILD, "label.isChild");
        addOption(leftPos + 83, topPos + 47, UpdateWardrobePacket.Field.MANNEQUIN_EXTRA_RENDER, "label.isExtraRenders");
        addOption(leftPos + 83, topPos + 67, UpdateWardrobePacket.Field.MANNEQUIN_IS_FLYING, "label.isFlying");
        addOption(leftPos + 83, topPos + 87, UpdateWardrobePacket.Field.MANNEQUIN_IS_VISIBLE, "label.isVisible");
        addOption(leftPos + 83, topPos + 107, UpdateWardrobePacket.Field.MANNEQUIN_IS_GHOST, "label.noclip");
    }

    private void addOption(int x, int y, UpdateWardrobePacket.Field option, String key) {
        addButton(new AWCheckBox(x, y, 9, 9, getDisplayText(key), option.get(wardrobe, false), button -> {
            if (button instanceof AWCheckBox) {
                boolean newValue = ((AWCheckBox) button).isSelected();
                NetworkHandler.getInstance().sendToServer(UpdateWardrobePacket.field(wardrobe, option, newValue));
            }
        }));
    }
}
