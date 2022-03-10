package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.gui.widget.AWSliderBox;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class WardrobeRotationSetting extends WardrobeBaseSetting {

    private final SkinWardrobe wardrobe;
    private final Entity entity;

    private int modelLeft = 0;
    private int modelTop = 0;
    private int selectedIndex = 1;

    private AWSliderBox sliderX;
    private AWSliderBox sliderY;
    private AWSliderBox sliderZ;


    public WardrobeRotationSetting(SkinWardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.man_rotations");
        this.wardrobe = container.getWardrobe();
        this.entity = container.getEntity();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);

        this.modelLeft = leftPos + 83;
        this.modelTop = topPos + 25;

        this.sliderX = addSlider(leftPos + 110, topPos + 25, 160, 10, "X: ");
        this.sliderY = addSlider(leftPos + 110, topPos + 36, 160, 10, "Y: ");
        this.sliderZ = addSlider(leftPos + 110, topPos + 47, 160, 10, "Z: ");

        this.addButton(new ExtendedButton(leftPos + 83, topPos + 70, 100, 16, getDisplayText("reset"), this::resetRotation));
        this.addButton(new ExtendedButton(leftPos + 83, topPos + 90, 100, 16, getDisplayText("random"), this::randomRotation));

        this.setSelectedIndex(selectedIndex);
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
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        RenderUtils.blit(matrixStack, modelLeft, modelTop, 22, 0, 24, 40, RenderUtils.TEX_WARDROBE_2);
        for (Part part : Part.values()) {
            Rectangle rect = part.bounds;
            int colour = 0xccffff00;
            if (part.bounds.contains(mouseX - modelLeft, mouseY - modelTop)) {
                colour = 0xccffffff;
            }
            if (part == getSelectedPart()) {
                colour = 0xcc00ff00;
            }
            fill(matrixStack, modelLeft + rect.x, modelTop + rect.y, modelLeft + rect.x + rect.width, modelTop + rect.y + rect.height, colour);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        for (int i = 0; i < Part.values().length; ++i) {
            Rectangle rect = Part.values()[i].bounds;
            if (rect.contains(mouseX - modelLeft, mouseY - modelTop)) {
                setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        Rotations rotations = getSelectedPart().getValue(entity);
        this.sliderX.setValue(getAngle(rotations.getX()));
        this.sliderY.setValue(getAngle(rotations.getY()));
        this.sliderZ.setValue(getAngle(rotations.getZ()));
    }

    public Part getSelectedPart() {
        Part[] parts = Part.values();
        if (selectedIndex < parts.length) {
            return parts[selectedIndex];
        }
        return parts[0];
    }

    private AWSliderBox addSlider(int x, int y, int width, int height, String key) {
        Function<Double, ITextComponent> titleProvider = currentValue -> {
            String formattedValue = String.format("%s%.2f", key, currentValue);
            return new StringTextComponent(formattedValue);
        };
        AWSliderBox slider = new AWSliderBox(x, y, width, height, titleProvider, -180, 180, this::updateValue);
        slider.setEndListener(this::didUpdateValue);
        addButton(slider);
        return slider;
    }

    private void updateValue(Button button) {
        float x = (float) sliderX.getValue();
        float y = (float) sliderY.getValue();
        float z = (float) sliderZ.getValue();
        getSelectedPart().setValue(entity, new Rotations(x, y, z));
    }

    private void didUpdateValue(Button button) {
        if (!(entity instanceof MannequinEntity)) {
            return;
        }
        CompoundNBT nbt = ((MannequinEntity) entity).saveCustomPose();
        UpdateWardrobePacket packet = UpdateWardrobePacket.opt(wardrobe, SkinWardrobeOption.MANNEQUIN_POSE, nbt);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    private void randomRotation(Button button) {
        Random random = new Random();
        for (Part part : Part.values()) {
            if (part == Part.BODY) {
                continue;
            }
            float x = 0.0f;
            float y = 0.0f;
            float z = 0.0f;
            for (int j = 0; j < 3; j++) {
                x += random.nextFloat() * 60.0f - 30.0f;
                y += random.nextFloat() * 60.0f - 30.0f;
                z += random.nextFloat() * 60.0f - 30.0f;
            }
            part.setValue(entity, new Rotations(x, y, z));
        }
        setSelectedIndex(selectedIndex);
        didUpdateValue(sliderX);
    }

    private void resetRotation(Button button) {
        boolean isCtrl = Screen.hasControlDown();
        for (Part part : Part.values()) {
            if (isCtrl) {
                part.setValue(entity, new Rotations(0, 0, 0));
            } else {
                part.setValue(entity, part.defaultValue);
            }
        }
        setSelectedIndex(selectedIndex);
        didUpdateValue(sliderX);
    }

    private double getAngle(double degree) {
        if (degree <= 180) {
            return degree;
        }
        return degree - 360;
    }

    private enum Part {
        HEAD(MannequinEntity.DATA_HEAD_POSE, MannequinEntity.DEFAULT_HEAD_POSE, 8, 3, 8, 8),
        BODY(MannequinEntity.DATA_BODY_POSE, MannequinEntity.DEFAULT_BODY_POSE, 8, 12, 8, 12),
        RIGHT_ARM(MannequinEntity.DATA_RIGHT_ARM_POSE, MannequinEntity.DEFAULT_RIGHT_ARM_POSE, 3, 12, 4, 12),
        LEFT_ARM(MannequinEntity.DATA_LEFT_ARM_POSE, MannequinEntity.DEFAULT_LEFT_ARM_POSE, 17, 12, 4, 12),
        RIGHT_LEG(MannequinEntity.DATA_RIGHT_LEG_POSE, MannequinEntity.DEFAULT_RIGHT_LEG_POSE, 7, 25, 4, 12),
        LEFT_LEG(MannequinEntity.DATA_LEFT_LEG_POSE, MannequinEntity.DEFAULT_LEFT_LEG_POSE, 13, 25, 4, 12);

        final Rectangle bounds;
        final Rotations defaultValue;
        final DataParameter<Rotations> dataParameter;

        Part(DataParameter<Rotations> dataParameter, Rotations defaultValue, int x, int y, int width, int height) {
            this.bounds = new Rectangle(x, y, width, height);
            this.dataParameter = dataParameter;
            this.defaultValue = defaultValue;
        }

        public void setValue(Entity entity, Rotations value) {
            entity.getEntityData().set(dataParameter, value);
        }

        public Rotations getValue(Entity entity) {
            if (entity == null) {
                return defaultValue;
            }
            return entity.getEntityData().get(dataParameter);
        }
    }
}