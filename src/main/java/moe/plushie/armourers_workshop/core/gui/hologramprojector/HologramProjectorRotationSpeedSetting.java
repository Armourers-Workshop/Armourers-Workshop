package moe.plushie.armourers_workshop.core.gui.hologramprojector;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.container.HologramProjectorContainer;
import moe.plushie.armourers_workshop.core.tileentity.HologramProjectorTileEntity;
import moe.plushie.armourers_workshop.core.gui.widget.AWSliderBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateHologramProjectorPacket;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class HologramProjectorRotationSpeedSetting extends AWTabPanel {

    protected int contentWidth = 200;
    protected int contentHeight = 82;

    private int modelLeft = 0;
    private int modelTop = 0;

    private AWSliderBox sliderX;
    private AWSliderBox sliderY;
    private AWSliderBox sliderZ;

    private final HologramProjectorTileEntity entity;
    private final UpdateHologramProjectorPacket.Field field = UpdateHologramProjectorPacket.Field.ROTATION_SPEED;

    public HologramProjectorRotationSpeedSetting(HologramProjectorTileEntity entity) {
        super("inventory.armourers_workshop.hologram-projector.rotationSpeed");
        this.entity = entity;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.modelTop = 0;
        this.modelLeft = (width - 178) / 2;

        this.sliderX = addSlider(modelLeft, modelTop + 30, 178, 10, "X: ");
        this.sliderY = addSlider(modelLeft, modelTop + 45, 178, 10, "Y: ");
        this.sliderZ = addSlider(modelLeft, modelTop + 60, 178, 10, "Z: ");

        Vector3f value = field.get(entity);
        this.sliderX.setValue(value.x());
        this.sliderY.setValue(value.y());
        this.sliderZ.setValue(value.z());
    }

    @Override
    public void removed() {
        sliderZ = null;
        sliderX = null;
        sliderY = null;
        super.removed();
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int x = (width - contentWidth) / 2;
        RenderUtils.bind(RenderUtils.TEX_HOLOGRAM_PROJECTOR);
        GuiUtils.drawContinuousTexturedBox(matrixStack, x, 0, 0, 138, contentWidth, contentHeight, 38, 38, 4, 0);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }


    private AWSliderBox addSlider(int x, int y, int width, int height, String key) {
        Function<Double, ITextComponent> titleProvider = currentValue -> {
            String formattedValue = String.format("%s%.0fms", key, currentValue);
            return new StringTextComponent(formattedValue);
        };
        AWSliderBox slider = new AWSliderBox(x, y, width, height, titleProvider, -10000, 10000, this::updateValue);
        slider.setEndListener(this::didUpdateValue);
        addButton(slider);
        return slider;
    }


    private void updateValue(Button button) {
        float x = (float) sliderX.getValue();
        float y = (float) sliderY.getValue();
        float z = (float) sliderZ.getValue();
        field.set(entity, new Vector3f(x, y, z));
    }

    private void didUpdateValue(Button button) {
        float x = (float) sliderX.getValue();
        float y = (float) sliderY.getValue();
        float z = (float) sliderZ.getValue();
        UpdateHologramProjectorPacket packet = new UpdateHologramProjectorPacket(entity, field, new Vector3f(x, y, z));
        NetworkHandler.getInstance().sendToServer(packet);
    }
}