package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeContributorSetting extends AWTabPanel {

    private final List<FormattedCharSequence> thanks;
    private final SkinWardrobe wardrobe;

    public SkinWardrobeContributorSetting(SkinWardrobeMenu container) {
        super("inventory.armourers_workshop.wardrobe.contributor");

        Font font = Minecraft.getInstance().font;
        TextComponent thanks = new TextComponent("");
        thanks.append(getDisplayText("label.contributor"));
        thanks.append("\n\n\nOptions coming here soon!");
        this.thanks = font.split(thanks, 185);
        this.wardrobe = container.getWardrobe();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        Component title = getDisplayText("label.enableContributorMagic");
        UpdateWardrobePacket.Field option = UpdateWardrobePacket.Field.WARDROBE_EXTRA_RENDER;
        addButton(new AWCheckBox(leftPos + 85, topPos + 128, 9, 9, title, option.get(wardrobe, true), button -> {
            if (button instanceof AWCheckBox) {
                boolean newValue = ((AWCheckBox) button).isSelected();
                NetworkManager.sendToServer(UpdateWardrobePacket.field(wardrobe, option, newValue));
            }
        }));
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        int x = leftPos + 85;
        int y = topPos + 26;
        for (FormattedCharSequence text : thanks) {
            font.draw(matrixStack, text, x, y, 0x404040);
            y += 9;
        }
    }
}
