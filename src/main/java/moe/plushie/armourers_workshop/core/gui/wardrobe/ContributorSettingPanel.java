package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.gui.widget.OptionButton;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.*;

import java.util.List;

public class ContributorSettingPanel extends BaseSettingPanel {

    private final List<IReorderingProcessor> thanks;

    public ContributorSettingPanel(SkinWardrobeContainer container) {
        super(TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.contributor"));

        FontRenderer font = Minecraft.getInstance().font;
        StringTextComponent thanks = new StringTextComponent("");
        thanks.append(getText());
        thanks.append("\n\n\nOptions coming here soon!");
        this.thanks = font.split(thanks, 185);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        StringTextComponent title = new StringTextComponent("Magic circle test?");
        addButton(new OptionButton(leftPos + 85, topPos + 128, 9, 9, title, AWConfig.enableMagicWhenContributor, button -> {
            if (button instanceof OptionButton) {
                AWConfig.enableMagicWhenContributor = ((OptionButton) button).isSelected();
            }
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        int x = leftPos + 85;
        int y = topPos + 26;
        for (IReorderingProcessor text : thanks) {
            font.draw(matrixStack, text, x, y, 0x404040);
            y += 9;
        }
    }

    public ITextComponent getText() {
        String key = "inventory.armourers_workshop.wardrobe.tab.contributor.label.contributor";
        return TranslateUtils.translate(key);
    }
}
