package moe.plushie.armourers_workshop.builder.item.tooloption;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

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
    public Integer get(CompoundNBT nbt) {
        if (nbt.contains(name, Constants.NBT.TAG_INT)) {
            return nbt.getInt(name);
        }
        return empty();
    }

    @Override
    public void set(CompoundNBT nbt, Integer value) {
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
