package moe.plushie.armourers_workshop.core.gui.hologramprojector;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.container.HologramProjectorContainer;
import moe.plushie.armourers_workshop.core.tileentity.HologramProjectorTileEntity;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWComboBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateHologramProjectorPacket;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class HologramProjectorExtraSetting extends AWTabPanel {

    protected int contentWidth = 200;
    protected int contentHeight = 78;

    private int modelLeft = 0;
    private int modelTop = 0;

    private final ITextComponent powerModeTips;
    private final HologramProjectorTileEntity entity;

    private final UpdateHologramProjectorPacket.Field field = UpdateHologramProjectorPacket.Field.POWER_MODE;
    private final UpdateHologramProjectorPacket.Field field2 = UpdateHologramProjectorPacket.Field.IS_GLOWING;

    public HologramProjectorExtraSetting(HologramProjectorContainer container) {
        super("inventory.armourers_workshop.hologram-projector.extra");
        this.entity = container.getEntity();
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int x = (width - contentWidth) / 2;
        RenderUtils.bind(RenderUtils.TEX_HOLOGRAM_PROJECTOR);
        GuiUtils.drawContinuousTexturedBox(matrixStack, x, 0, 0, 138, contentWidth, contentHeight, 38, 38, 4, 0);
        font.draw(matrixStack, powerModeTips, modelLeft, modelTop + 45, 0x404040);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private AWCheckBox addOption(int x, int y, UpdateHologramProjectorPacket.Field field, String key) {
        AWCheckBox box = new AWCheckBox(x, y, 9, 9, getDisplayText(key), field.get(entity), button -> {
            if (button instanceof AWCheckBox) {
                boolean newValue = ((AWCheckBox) button).isSelected();
                field.set(entity, newValue);
                UpdateHologramProjectorPacket packet = new UpdateHologramProjectorPacket(entity, field, newValue);
                NetworkHandler.getInstance().sendToServer(packet);
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
                NetworkHandler.getInstance().sendToServer(packet);
            }
        });
        addButton(comboBox);
        return comboBox;
    }

}