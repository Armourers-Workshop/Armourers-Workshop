package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.api.core.IResourceKey;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModConstants;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class SkinRenderType implements IRenderType, IAssociatedContainer {

    protected boolean isEmissive = false;
    protected boolean isOutline = false;

    protected int ordinal = 0;
    protected int bufferSize = 0;

    protected Group group = Group.MAIN;
    protected BlendMode blendMode = BlendMode.NONE;

    protected IResourceKey texture;

    protected IVertexFormat.Mode mode;
    protected IVertexFormat format;

    protected String name;
    protected Optional<IRenderType> outline;

    @Override
    public <T> T getAssociatedObject(IAssociatedContainer.Key<T> key) {
        return ((IAssociatedContainer) get()).getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainer.Key<T> key, T value) {
        ((IAssociatedContainer) get()).setAssociatedObject(key, value);
    }

    @Override
    public boolean isEmissive() {
        return isEmissive;
    }

    @Override
    public boolean isTranslucent() {
        return blendMode == BlendMode.TRANSLUCENT;
    }

    @Override
    public boolean isOutline() {
        return isOutline;
    }

    @Override
    public int bufferSize() {
        return bufferSize;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Group group() {
        return group;
    }

    @Override
    public IVertexFormat.Mode mode() {
        return mode;
    }

    @Override
    public IVertexFormat format() {
        return format;
    }

    @Override
    public Optional<IRenderType> outline() {
        return outline;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinRenderType that)) return false;
        return get().equals(that.get());
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "texture", texture);
    }

    public static abstract class Builder implements IRenderType.Builder {

        protected final ArrayList<Consumer<SkinRenderType>> updater = new ArrayList<>();

        public Builder format(SkinVertexFormat format) {
            //updater.add(t -> t.format = format);
            return this;
        }

        @Override
        public Builder texture(IResourceKey texture, boolean blur, boolean mipmap) {
            updater.add(t -> t.texture = texture);
            return this;
        }

        @Override
        public Builder emissive() {
            updater.add(t -> t.isEmissive = true);
            return this;
        }

        @Override
        public Builder group(Group group) {
            updater.add(t -> t.group = group);
            return this;
        }

        @Override
        public Builder blend(BlendMode mode) {
            updater.add(t -> t.blendMode = mode);
            return this;
        }

        @Override
        public Builder ordinal(int ordinal) {
            updater.add(t -> t.ordinal = ordinal);
            return this;
        }

        @Override
        public SkinRenderType build(String name) {
            var registryName = ModConstants.key("rendertype/" + name);
            var renderType = create(registryName.toString());
            updater.forEach(it -> it.accept(renderType));
            renderType.outline().ifPresent(type -> {
                if (type instanceof SkinRenderType type1) {
                    type1.group = renderType.group().outline();
                    type1.ordinal = renderType.ordinal();
                }
            });
            return renderType;
        }

        protected abstract SkinRenderType create(String registryName);
    }
}
