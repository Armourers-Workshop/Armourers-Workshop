package moe.plushie.armourers_workshop.core.gui.wardrobe;

import moe.plushie.armourers_workshop.core.gui.widget.OptionButton;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.text.ITextComponent;

public class DisplaySettingPanel extends BaseSettingPanel {

    private final SkinWardrobe wardrobe;

    public DisplaySettingPanel(SkinWardrobeContainer container) {
        super(TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.displaySettings"));
        this.wardrobe = container.getWardrobe();
    }


    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        addOption(leftPos + 83, topPos + 27, EquipmentSlotType.HEAD, "renderHeadArmour");
        addOption(leftPos + 83, topPos + 47, EquipmentSlotType.CHEST, "renderChestArmour");
        addOption(leftPos + 83, topPos + 67, EquipmentSlotType.LEGS, "renderLegArmour");
        addOption(leftPos + 83, topPos + 87, EquipmentSlotType.FEET, "renderFootArmour");
    }

    protected boolean getValue(EquipmentSlotType slotType) {
        return wardrobe.shouldRenderEquipment(slotType);
    }

    protected void setValue(boolean value, EquipmentSlotType slotType) {
        wardrobe.setRenderEquipment(value, slotType);
        wardrobe.sendToServer();
    }

    private void addOption(int x, int y, EquipmentSlotType slotType, String key) {
        ITextComponent title = TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.display_settings." + key);
        addButton(new OptionButton(x, y, 9, 9, title, getValue(slotType), button -> {
            if (button instanceof OptionButton) {
                setValue(((OptionButton) button).isSelected(), slotType);
            }
        }));
    }
}
