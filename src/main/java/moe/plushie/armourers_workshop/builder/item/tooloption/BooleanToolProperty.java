package moe.plushie.armourers_workshop.builder.item.tooloption;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import java.util.Objects;

public class BooleanToolProperty extends ToolProperty<Boolean> {

    public BooleanToolProperty(String key, boolean defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public Boolean get(CompoundNBT nbt) {
        if (nbt.contains(name, Constants.NBT.TAG_BYTE)) {
            return nbt.getBoolean(name);
        }
        return empty();
    }

    @Override
    public void set(CompoundNBT nbt, Boolean value) {
        if (Objects.equals(empty(), value)) {
            nbt.remove(name);
        } else {
            nbt.putBoolean(name, value);
        }
    }
}
