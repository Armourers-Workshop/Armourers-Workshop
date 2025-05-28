package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexFormat;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

public class AbstractRenderType implements IRenderType, IAssociatedContainerProvider {

    private boolean isGrowing = false;
    private boolean isOutline = false;

    private Target target = Target.MAIN;
    private Transparency transparency = Transparency.NONE;

    private final IVertexFormat.Mode mode;
    private final IVertexFormat format;

    private final RenderType impl;
    private final DataContainer storage = new DataContainer();
    private final Optional<IRenderType> outline;

    private AbstractRenderType(RenderType renderType) {
        this.impl = renderType;
        this.mode = AbstractVertexFormat.of(renderType.mode());
        this.format = AbstractVertexFormat.of(renderType.format());
        this.outline = renderType.outline().map(AbstractRenderType::new);
        this.isOutline = renderType.isOutline();
    }

    public static AbstractRenderType of(RenderType renderType) {
        return DataContainer.of(renderType, AbstractRenderType::new);
    }

    public static IRenderTypeBuilder builder(SkinVertexFormat format) {
        return AbstractRenderTypeImpl.builder(format);
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        return storage.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainerKey<T> key, T value) {
        storage.setAssociatedObject(key, value);
    }

    @Override
    public boolean isGrowing() {
        return isGrowing;
    }

    @Override
    public boolean isTranslucent() {
        return transparency != Transparency.NONE;
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

    public static abstract class Builder implements IRenderTypeBuilder {

        protected final ArrayList<Consumer<AbstractRenderType>> updater = new ArrayList<>();

        public IRenderTypeBuilder format(SkinVertexFormat format) {
            //updater.add(t -> t.format = format);
            return this;
        }

        @Override
        public IRenderTypeBuilder emissive() {
            updater.add(t -> t.isGrowing = true);
            return this;
        }

        @Override
        public IRenderTypeBuilder target(Target target) {
            updater.add(t -> t.target = target);
            return this;
        }

        @Override
        public IRenderTypeBuilder transparency(Transparency transparency) {
            updater.add(t -> t.transparency = transparency);
            return this;
        }
    }
}
