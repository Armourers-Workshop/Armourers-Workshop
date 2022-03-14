package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.capability.Wardrobe;
import moe.plushie.armourers_workshop.core.container.WardrobeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class WardrobeLocationSetting extends AWTabPanel {

    private final float[] steps = {1.0f, 1.0f / 8.0f, 1.0f / 16.0f};
    private final Wardrobe wardrobe;
    private final Entity entity;

    public WardrobeLocationSetting(WardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.man_offsets");
        this.wardrobe = container.getWardrobe();
        this.entity = container.getEntity();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        int x = leftPos + 83;
        int y = topPos + 25;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                addIconButton(x + i * 20, y + j * 20, 208, 80, j, -steps[i], "button.sub." + -(i - 3));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                addIconButton(x + 77 + i * 20, y + j * 20, 208, 96, j, steps[3 - i - 1], "button.add." + (i + 1));
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        font.draw(matrixStack, "X", leftPos + 146, topPos + 29, 0x333333);
        font.draw(matrixStack, "Y", leftPos + 146, topPos + 29 + 20, 0x333333);
        font.draw(matrixStack, "Z", leftPos + 146, topPos + 29 + 40, 0x333333);
    }

    private void updateValue(int axis, float step) {
        if (!(entity instanceof MannequinEntity)) {
            return;
        }
        Vector3d pos = entity.position();
        double[] xyz = {pos.x(), pos.y(), pos.z()};
        xyz[axis] += step;
        pos = new Vector3d(xyz[0], xyz[1], xyz[2]);
        UpdateWardrobePacket packet = UpdateWardrobePacket.field(wardrobe, UpdateWardrobePacket.Field.MANNEQUIN_POSITION, pos);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    private void addIconButton(int x, int y, int u, int v, int axis, float step, String key) {
        ITextComponent tooltip = getDisplayText(key);
        Button.IPressable pressable = b -> updateValue(axis, step);
        addButton(new AWImageButton(x, y, 16, 16, u, v, RenderUtils.TEX_BUTTONS, pressable, this::renderIconTooltip, tooltip));

    }

    private void renderIconTooltip(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
        renderTooltip(matrixStack, button.getMessage(), mouseX, mouseY);
    }
}
