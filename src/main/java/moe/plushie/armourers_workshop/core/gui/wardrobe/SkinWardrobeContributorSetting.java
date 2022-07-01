package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.container.SkinWardrobeContainer;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class SkinWardrobeContributorSetting extends AWTabPanel {

    private final List<IReorderingProcessor> thanks;
    private final SkinWardrobe wardrobe;

    public SkinWardrobeContributorSetting(SkinWardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.contributor");

        FontRenderer font = Minecraft.getInstance().font;
        StringTextComponent thanks = new StringTextComponent("");
        thanks.append(getDisplayText("label.contributor"));
        thanks.append("\n\n\nOptions coming here soon!");
        this.thanks = font.split(thanks, 185);
        this.wardrobe = container.getWardrobe();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        ITextComponent title = getDisplayText("label.enableContributorMagic");
        UpdateWardrobePacket.Field option = UpdateWardrobePacket.Field.WARDROBE_EXTRA_RENDER;
        addButton(new AWCheckBox(leftPos + 85, topPos + 128, 9, 9, title, option.get(wardrobe, true), button -> {
            if (button instanceof AWCheckBox) {
                boolean newValue = ((AWCheckBox) button).isSelected();
                NetworkHandler.getInstance().sendToServer(UpdateWardrobePacket.field(wardrobe, option, newValue));
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
