package moe.plushie.armourers_workshop.core.client.gui.hologramprojector;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWComboBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.network.UpdateHologramProjectorPacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class HologramProjectorExtraSetting extends AWTabPanel {

    private final Component powerModeTips;
    private final HologramProjectorBlockEntity entity;
    private final UpdateHologramProjectorPacket.Field field = UpdateHologramProjectorPacket.Field.POWER_MODE;
    private final UpdateHologramProjectorPacket.Field field2 = UpdateHologramProjectorPacket.Field.IS_GLOWING;
    protected int contentWidth = 200;
    protected int contentHeight = 78;
    private int modelLeft = 0;
    private int modelTop = 0;

    public HologramProjectorExtraSetting(HologramProjectorBlockEntity entity) {
        super("inventory.armourers_workshop.hologram-projector.extra");
        this.entity = entity;
        this.powerModeTips = getDisplayText("powerMode");
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.modelTop = 0;
        this.modelLeft = (width - 178) / 2;

        addOption(modelLeft, modelTop + 30, field2, "glowing");
        addComboList(modelLeft, modelTop + 55, field);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int x = (width - contentWidth) / 2;
        RenderUtils.bind(RenderUtils.TEX_HOLOGRAM_PROJECTOR);
        RenderUtils.drawContinuousTexturedBox(matrixStack, x, 0, 0, 138, contentWidth, contentHeight, 38, 38, 4, 0);
        font.draw(matrixStack, powerModeTips, modelLeft, modelTop + 45, 0x404040);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private AWCheckBox addOption(int x, int y, UpdateHologramProjectorPacket.Field field, String key) {
        AWCheckBox box = new AWCheckBox(x, y, 9, 9, getDisplayText(key), field.get(entity), button -> {
            if (button instanceof AWCheckBox) {
                boolean newValue = ((AWCheckBox) button).isSelected();
                field.set(entity, newValue);
                UpdateHologramProjectorPacket packet = new UpdateHologramProjectorPacket(entity, field, newValue);
                NetworkManager.sendToServer(packet);
            }
        });
        addButton(box);
        return box;
    }

    private AWComboBox addComboList(int x, int y, UpdateHologramProjectorPacket.Field field) {
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        items.add(new AWComboBox.ComboItem(getDisplayText("powerMode.ignored")));
        items.add(new AWComboBox.ComboItem(getDisplayText("powerMode.high")));
        items.add(new AWComboBox.ComboItem(getDisplayText("powerMode.low")));
        AWComboBox comboBox = new AWComboBox(x, y, 80, 14, items, field.get(entity), button -> {
            if (button instanceof AWComboBox) {
                int newValue = ((AWComboBox) button).getSelectedIndex();
                field.set(entity, newValue);
                UpdateHologramProjectorPacket packet = new UpdateHologramProjectorPacket(entity, field, newValue);
                NetworkManager.sendToServer(packet);
            }
        });
        addButton(comboBox);
        return comboBox;
    }

}
