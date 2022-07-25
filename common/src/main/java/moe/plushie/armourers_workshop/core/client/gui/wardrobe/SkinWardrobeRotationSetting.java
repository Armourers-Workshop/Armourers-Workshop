package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWExtendedButton;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWSliderBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

import java.util.Random;
import java.util.function.Function;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeRotationSetting extends AWTabPanel {

    private final SkinWardrobe wardrobe;
    private final Entity entity;

    private int modelLeft = 0;
    private int modelTop = 0;
    private int selectedIndex = 1;

    private AWSliderBox sliderX;
    private AWSliderBox sliderY;
    private AWSliderBox sliderZ;

    public SkinWardrobeRotationSetting(SkinWardrobeMenu container) {
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

        this.addButton(new AWExtendedButton(leftPos + 83, topPos + 70, 100, 16, getDisplayText("reset"), this::resetRotation));
        this.addButton(new AWExtendedButton(leftPos + 83, topPos + 90, 100, 16, getDisplayText("random"), this::randomRotation));

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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        RenderUtils.blit(matrixStack, modelLeft, modelTop, 22, 0, 24, 40, RenderUtils.TEX_WARDROBE_2);
        for (Part part : Part.values()) {
            Rectangle2i rect = part.bounds;
            int color = 0xccffff00;
            if (part.bounds.contains(mouseX - modelLeft, mouseY - modelTop)) {
                color = 0xccffffff;
            }
            if (part == getSelectedPart()) {
                color = 0xcc00ff00;
            }
            fill(matrixStack, modelLeft + rect.getX(), modelTop + rect.getY(), modelLeft + rect.getX() + rect.getWidth(), modelTop + rect.getY() + rect.getHeight(), color);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        for (int i = 0; i < Part.values().length; ++i) {
            Rectangle2i rect = Part.values()[i].bounds;
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
        Function<Double, Component> titleProvider = currentValue -> {
            String formattedValue = String.format("%s%.2f\u00b0", key, currentValue);
            return TranslateUtils.literal(formattedValue);
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
        CompoundTag nbt = ((MannequinEntity) entity).saveCustomPose();
        UpdateWardrobePacket packet = UpdateWardrobePacket.field(wardrobe, UpdateWardrobePacket.Field.MANNEQUIN_POSE, nbt);
        NetworkManager.sendToServer(packet);
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

        final Rectangle2i bounds;
        final Rotations defaultValue;
        final EntityDataAccessor<Rotations> dataParameter;

        Part(EntityDataAccessor<Rotations> dataParameter, Rotations defaultValue, int x, int y, int width, int height) {
            this.bounds = new Rectangle2i(x, y, width, height);
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