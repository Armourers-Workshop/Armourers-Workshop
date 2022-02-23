package moe.plushie.armourers_workshop.core.gui.wardrobe;

import moe.plushie.armourers_workshop.core.gui.widget.OptionButton;
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
public class ExtraSettingPanel extends BaseSettingPanel {

    private final SkinWardrobe wardrobe;

    public ExtraSettingPanel(SkinWardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.man_extras");
        this.wardrobe = container.getWardrobe();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        addOption(leftPos + 83, topPos + 27, SkinWardrobeOption.MANNEQUIN_IS_CHILD, "label.isChild");
        addOption(leftPos + 83, topPos + 47, SkinWardrobeOption.MANNEQUIN_EXTRA_RENDER, "label.isExtraRenders");
        addOption(leftPos + 83, topPos + 67, SkinWardrobeOption.MANNEQUIN_IS_FLYING, "label.isFlying");
        addOption(leftPos + 83, topPos + 87, SkinWardrobeOption.MANNEQUIN_IS_VISIBLE, "label.isVisible");
        addOption(leftPos + 83, topPos + 107, SkinWardrobeOption.MANNEQUIN_IS_GHOST, "label.noclip");
    }

    private void addOption(int x, int y, SkinWardrobeOption option, String key) {
        addButton(new OptionButton(x, y, 9, 9, getDisplayText(key), option.get(wardrobe, false), button -> {
            if (button instanceof OptionButton) {
                boolean newValue = ((OptionButton) button).isSelected();
                NetworkHandler.getInstance().sendToServer(UpdateWardrobePacket.option(wardrobe, option, newValue));
            }
        }));
    }
}
