package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeLocationSetting extends AWTabPanel {

    private final float[] steps = {1.0f, 1.0f / 8.0f, 1.0f / 16.0f};
    private final SkinWardrobe wardrobe;
    private final Entity entity;

    public SkinWardrobeLocationSetting(SkinWardrobeMenu container) {
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        font.draw(matrixStack, "X", leftPos + 146, topPos + 29, 0x333333);
        font.draw(matrixStack, "Y", leftPos + 146, topPos + 29 + 20, 0x333333);
        font.draw(matrixStack, "Z", leftPos + 146, topPos + 29 + 40, 0x333333);
    }

    private void updateValue(int axis, float step) {
        if (!(entity instanceof MannequinEntity)) {
            return;
        }
        Vec3 pos = entity.position();
        double[] xyz = {pos.x(), pos.y(), pos.z()};
        xyz[axis] += step;
        pos = new Vec3(xyz[0], xyz[1], xyz[2]);
        UpdateWardrobePacket packet = UpdateWardrobePacket.field(wardrobe, UpdateWardrobePacket.Field.MANNEQUIN_POSITION, pos);
        NetworkManager.sendToServer(packet);
    }

    private void addIconButton(int x, int y, int u, int v, int axis, float step, String key) {
        Component tooltip = getDisplayText(key);
        Button.OnPress pressable = b -> updateValue(axis, step);
        addButton(new AWImageButton(x, y, 16, 16, u, v, RenderUtils.TEX_BUTTONS, pressable, this::renderIconTooltip, tooltip));
    }

    private void renderIconTooltip(Button button, PoseStack matrixStack, int mouseX, int mouseY) {
        renderTooltip(matrixStack, button.getMessage(), mouseX, mouseY);
    }
}
