package moe.plushie.armourers_workshop.common.data.type;

import net.minecraft.util.IStringSerializable;

public enum TextureType implements IStringSerializable {
    NONE,
    USER,
    URL;

    @Override
    public String getName() {
        return name();
    }
}
