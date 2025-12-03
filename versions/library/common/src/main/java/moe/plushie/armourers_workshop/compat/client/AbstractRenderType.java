package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexFormat;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

public class AbstractRenderType extends SkinRenderType implements IAssociatedContainer {

    private boolean isEmissive = false;
    private boolean isOutline = false;
    private int ordinal = 0;

    private Group group = Group.MAIN;
    private BlendMode blendMode = BlendMode.NONE;

    private IResourceLocation texture;

    private final IVertexFormat.Mode mode;
    private final IVertexFormat format;

    private final String name;
    private final RenderType impl;
    private final Optional<IRenderType> outline;

    private AbstractRenderType(RenderType renderType) {
        var desc = renderType.toString();
        this.impl = renderType;
        this.mode = AbstractVertexFormat.wrap(renderType.mode());
        this.format = AbstractVertexFormat.wrap(renderType.format());
        this.outline = renderType.outline().map(AbstractRenderType::of);
        this.isOutline = renderType.isOutline();
        this.name = desc.replaceAll("RenderType\\[(.+?):CompositeState.+$", "$1");
    }

    public static AbstractRenderType of(RenderType renderType) {
        return DataContainer.of(renderType, AbstractRenderType::new);
    }

    public static IRenderType.Builder builder(SkinVertexFormat format) {
        return AbstractRenderTypeImpl.builder(format);
    }

    public void apply(ArrayList<Consumer<AbstractRenderType>> updater) {
        updater.forEach(it -> it.accept(this));
        outline.ifPresent(type -> ((AbstractRenderType) type).ordinal = ordinal);
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainer.Key<T> key) {
        return ((IAssociatedContainer) impl).getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainer.Key<T> key, T value) {
        ((IAssociatedContainer) impl).setAssociatedObject(key, value);
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
        return impl.bufferSize();
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
    public RenderType get() {
        return impl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractRenderType that)) return false;
        return impl.equals(that.impl);
    }

    @Override
    public int hashCode() {
        return impl.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "texture", texture);
    }

    public static abstract class Builder implements IRenderType.Builder {

        protected final ArrayList<Consumer<AbstractRenderType>> updater = new ArrayList<>();

        public Builder format(SkinVertexFormat format) {
            //updater.add(t -> t.format = format);
            return this;
        }

        @Override
        public Builder texture(IResourceLocation texture, boolean blur, boolean mipmap) {
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
    }
}
