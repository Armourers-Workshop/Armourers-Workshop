package moe.plushie.armourers_workshop.utils.ext;

import net.minecraft.resources.ResourceLocation;

public class ExtendedResourceLocation<T> extends ResourceLocation {

    public final T extra;
    public final ResourceLocation value;

    public ExtendedResourceLocation(ResourceLocation value, T extra) {
        super(value.getNamespace(), value.getPath());
        this.value = value;
        this.extra = extra;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ExtendedResourceLocation) {
            return value.equals(((ExtendedResourceLocation<?>) o).value);
        }
        return value.equals(o);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

