package moe.plushie.armourers_workshop.builder.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.BooleanToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.IntegerToolProperty;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWSliderBox;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdatePaintingToolPacket;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class PaintingToolScreen extends Screen {

    protected final ArrayList<Pair<IPaintingToolProperty<?>, Button>> properties = new ArrayList<>();

    protected final Hand hand;
    protected final ItemStack itemStack;

    protected int leftPos;
    protected int topPos;
    protected int titleLabelX;
    protected int titleLabelY;
    protected int imageWidth = 176;
    protected int imageHeight = 24; // 24 + n + 8

    public PaintingToolScreen(ITextComponent title, ArrayList<IPaintingToolProperty<?>> properties, ItemStack itemStack, Hand hand) {
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

    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), this.titleLabelX, this.titleLabelY, 4210752);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        this.renderBackground(matrixStack);
        RenderUtils.bind(RenderUtils.TEX_COMMON);
        GuiUtils.drawContinuousTexturedBox(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 128, 128, 4, 4, 4, 4, 0);
        this.renderLabels(matrixStack, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
    }

    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        InputMappings.Input mouseKey = InputMappings.getKey(p_231046_1_, p_231046_2_);
        if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) {
            return true;
        } else if (Minecraft.getInstance().options.keyInventory.isActiveAndMatches(mouseKey)) {
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
        UpdatePaintingToolPacket packet = new UpdatePaintingToolPacket(hand, itemStack);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    protected ITextComponent getOptionText(String key) {
        return TranslateUtils.title("tooloption.armourers_workshop" + "." + key);
    }

    private Button createOptionView(IPaintingToolProperty<?> property) {
        if (property instanceof BooleanToolProperty) {
            BooleanToolProperty property1 = (BooleanToolProperty) property;
            ITextComponent title = getOptionText(property.getName());
            return new AWCheckBox(0, 0, 9, 9, title, property1.get(itemStack), button -> {
                if (button instanceof AWCheckBox) {
                    boolean value = ((AWCheckBox)button).isSelected();
                    property1.setValue(itemStack, value);
                    sendToServer();
                }
            });
        }
        if (property instanceof IntegerToolProperty) {
            IntegerToolProperty property1 = (IntegerToolProperty) property;
            ITextComponent title = getOptionText(property.getName());
            Function<Double, ITextComponent> titleProvider = currentValue -> {
                StringTextComponent formattedValue = new StringTextComponent("");
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
