package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class WardrobeContributorSetting extends WardrobeBaseSetting {

    private final List<IReorderingProcessor> thanks;

    public WardrobeContributorSetting(SkinWardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.contributor");

        FontRenderer font = Minecraft.getInstance().font;
        StringTextComponent thanks = new StringTextComponent("");
        thanks.append(getDisplayText("label.contributor"));
        thanks.append("\n\n\nOptions coming here soon!");
        this.thanks = font.split(thanks, 185);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        StringTextComponent title = new StringTextComponent("Magic circle test?");
        addButton(new AWCheckBox(leftPos + 85, topPos + 128, 9, 9, title, AWConfig.enableMagicWhenContributor, button -> {
            if (button instanceof AWCheckBox) {
                AWConfig.enableMagicWhenContributor = ((AWCheckBox) button).isSelected();
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
}
