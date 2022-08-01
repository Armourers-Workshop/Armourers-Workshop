package moe.plushie.armourers_workshop.utils.ext;

import net.minecraft.resources.ResourceLocation;

public class OpenResourceLocation<T> extends ResourceLocation {

    public final T extra;
    public final ResourceLocation value;

    public OpenResourceLocation(ResourceLocation value, T extra) {
        super(value.getNamespace(), value.getPath());
        this.value = value;
        this.extra = extra;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OpenResourceLocation) {
            return value.equals(((OpenResourceLocation<?>) o).value);
        }
        return value.equals(o);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

