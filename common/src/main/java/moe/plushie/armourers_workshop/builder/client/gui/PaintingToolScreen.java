package moe.plushie.armourers_workshop.builder.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.BooleanToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.IntegerToolProperty;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWSliderBox;
import moe.plushie.armourers_workshop.core.network.UpdatePaintingToolPacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

@Environment(value = EnvType.CLIENT)
public class PaintingToolScreen extends Screen {

    protected final ArrayList<Pair<IPaintingToolProperty<?>, Button>> properties = new ArrayList<>();

    protected final InteractionHand hand;
    protected final ItemStack itemStack;

    protected int leftPos;
    protected int topPos;
    protected int titleLabelX;
    protected int titleLabelY;
    protected int imageWidth = 176;
    protected int imageHeight = 24; // 24 + n + 8

    public PaintingToolScreen(Component title, ArrayList<IPaintingToolProperty<?>> properties, ItemStack itemStack, InteractionHand hand) {
        super(title);
        this.hand = hand;
        this.itemStack = itemStack;
        properties.forEach(property -> {
            Button button = createOptionView(property);
            if (button != null) {
                this.properties.add(Pair.of(property, button));
                this.imageHeight += button.getHeight() + 8;
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.titleLabelX = leftPos + imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = topPos + 7;
        int dy = topPos + 24;
        for (Pair<IPaintingToolProperty<?>, Button> pair : properties) {
            Button button = pair.getSecond();
            button.x = leftPos + 8;
            button.y = dy;
            button.setWidth(imageWidth - 16);
            addButton(button);
            dy += button.getHeight() + 8;
        }
    }

    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), this.titleLabelX, this.titleLabelY, 4210752);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        this.renderBackground(matrixStack);
        RenderUtils.bind(RenderUtils.TEX_COMMON);
        RenderUtils.drawContinuousTexturedBox(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 128, 128, 4, 4, 4, 4, 0);
        this.renderLabels(matrixStack, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (Minecraft.getInstance().options.keyInventory.matches(i, j)) {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void sendToServer() {
        NetworkManager.sendToServer(new UpdatePaintingToolPacket(hand, itemStack));
    }

    protected Component getOptionText(String key) {
        return TranslateUtils.title("tooloption.armourers_workshop" + "." + key);
    }

    private Button createOptionView(IPaintingToolProperty<?> property) {
        if (property instanceof BooleanToolProperty) {
            BooleanToolProperty property1 = (BooleanToolProperty) property;
            Component title = getOptionText(property.getName());
            return new AWCheckBox(0, 0, 9, 9, title, property1.get(itemStack), button -> {
                if (button instanceof AWCheckBox) {
                    boolean value = ((AWCheckBox) button).isSelected();
                    property1.setValue(itemStack, value);
                    sendToServer();
                }
            });
        }
        if (property instanceof IntegerToolProperty) {
            IntegerToolProperty property1 = (IntegerToolProperty) property;
            Component title = getOptionText(property.getName());
            Function<Double, Component> titleProvider = currentValue -> {
                TextComponent formattedValue = new TextComponent("");
                formattedValue.append(title);
                formattedValue.append(" ");
                formattedValue.append(String.format("%.1f", currentValue));
                return formattedValue;
            };
            AWSliderBox box = new AWSliderBox(0, 0, 150, 20, titleProvider, property1.getMinValue(), property1.getMaxValue(), Objects::hash);
            box.setHands(false);
            box.setValue(property1.get(itemStack));
            box.setEndListener(button -> {
                if (button instanceof AWSliderBox) {
                    int value = (int) ((AWSliderBox) button).getValue();
                    property1.setValue(itemStack, value);
                    sendToServer();
                }
            });
            return box;
        }
        return null;
    }
}
