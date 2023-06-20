package moe.plushie.armourers_workshop.core.item.option;

import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class BooleanToolProperty extends ToolProperty<Boolean> {

    public BooleanToolProperty(String key, boolean defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public Boolean get(CompoundTag nbt) {
        if (nbt.contains(name, Constants.TagFlags.BYTE)) {
            return nbt.getBoolean(name);
        }
        return empty();
    }

    @Override
    public void set(CompoundTag nbt, Boolean value) {
        if (Objects.equals(empty(), value)) {
            nbt.remove(name);
        } else {
            nbt.putBoolean(name, value);
        }
    }
}
