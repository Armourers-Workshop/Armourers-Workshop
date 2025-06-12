package moe.plushie.armourers_workshop.core.item.option;

import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.nbt.CompoundTag;


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
        return nbt.getOptionalInt(name).orElseGet(this::empty);
    }

    @Override
    public void set(CompoundTag nbt, Integer value) {
        if (Objects.equals(empty(), value)) {
            nbt.remove(name);
        } else {
            nbt.putInt(name, value);
        }
    }

    public int minValue() {
        return minValue;
    }

    public int maxValue() {
        return maxValue;
    }
}
