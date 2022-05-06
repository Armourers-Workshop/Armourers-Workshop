package moe.plushie.armourers_workshop.builder.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.core.gui.widget.AWSliderBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class ArmourerBaseSkinPanel extends AWTabPanel {

    protected int cursorX = 0;
    protected int cursorY = 0;

    protected final SkinProperties skinProperties;
    protected final ArrayList<Widget> widgets = new ArrayList<>();

    protected Consumer<SkinProperties> applier;

    public ArmourerBaseSkinPanel(SkinProperties skinProperties) {
        super("inventory.armourers_workshop.armourer.skinSettings");
        this.skinProperties = skinProperties;
    }

    @Override
    public void init(Minecraft minecraft, int x, int y) {
        widgets.clear();
        cursorX = leftPos + 10;
        cursorY = topPos + 20;
        super.init(minecraft, x, y);
    }

    public void apply() {
        if (applier != null) {
            applier.accept(skinProperties);
        }
    }


    public Consumer<SkinProperties> getApplier() {
        return applier;
    }

    public void setApplier(Consumer<SkinProperties> applier) {
        this.applier = applier;
    }

    protected AWCheckBox addCheckBox(int x, int y, int width, int height, SkinProperty<Boolean> property) {
        boolean oldValue = skinProperties.get(property);
        AWCheckBox checkBox = new AWCheckBox(cursorX + x, cursorY + y, width, height, getDisplayText(property.getKey()), oldValue, b -> {
            boolean value = ((AWCheckBox) b).isSelected();
            skinProperties.put(property, value);
            apply();
        });
        addButton(checkBox);
        cursorY += 4;
        return checkBox;
    }

    protected AWSliderBox addSliderBox(int x, int y, int width, int height, double minValue, double maxValue, String suffix, SkinProperty<Double> property) {
        Function<Double, ITextComponent> titleProvider = value -> {
            String formattedValue = String.format("%.0f%s", value, suffix);
            return new StringTextComponent(formattedValue);
        };
        AWSliderBox box = new AWSliderBox(cursorX + x, cursorY + y, width, height, titleProvider, minValue, maxValue, Objects::hash);
        box.setEndListener(button -> {
            double value = ((AWSliderBox) button).getValue();
            skinProperties.put(property, value);
            apply();
        });
        box.setValue(skinProperties.get(property));
        addButton(box);
        cursorY += 2;
        return box;
    }

    @Override
    protected AWLabel addLabel(int x, int y, int width, int height, ITextComponent message) {
        return super.addLabel(cursorX + x, cursorY + y, width, height, message);
    }

    @Override
    protected <T extends Widget> T addButton(T button) {
        super.addButton(button);
        widgets.add(button);
        cursorY += button.getHeight() + 2;
        return button;
    }
}
