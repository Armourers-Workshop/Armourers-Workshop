package moe.plushie.armourers_workshop.core.item.option;

import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class IntegerToolProperty extends ToolProperty<Integer> {

    protected final int minValue;
    protected final int maxValue;

    public IntegerToolProperty(String key, int defaultValue, int minValue, int maxValue) {
        super(key, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Integer get(CompoundTag nbt) {
        if (nbt.contains(name, Constants.TagFlags.INT)) {
            return nbt.getInt(name);
        }
        return empty();
    }

    @Override
    public void set(CompoundTag nbt, Integer value) {
        if (Objects.equals(empty(), value)) {
            nbt.remove(name);
        } else {
            nbt.putInt(name, value);
        }
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }
}
