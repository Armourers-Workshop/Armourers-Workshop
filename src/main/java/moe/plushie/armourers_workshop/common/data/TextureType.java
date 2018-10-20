package moe.plushie.armourers_workshop.common.data;

import net.minecraft.util.IStringSerializable;

public enum TextureType implements IStringSerializable {
    USER,
    URL;

    @Override
    public String getName() {
        return name();
    }
}
