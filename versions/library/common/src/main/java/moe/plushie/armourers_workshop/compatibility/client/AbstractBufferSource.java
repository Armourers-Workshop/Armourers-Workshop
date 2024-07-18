package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.core.data.cache.CacheQueue;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.time.Duration;
import java.util.function.Supplier;

public class AbstractBufferSource implements IBufferSource {

    private static final AbstractBufferSource DEFAULT = create(AbstractBufferSourceImpl::bufferSource);
    private static final AbstractBufferSource OUTLINE = create(AbstractBufferSourceImpl::outlineBufferSource);
    private static final AbstractBufferSource TESSELATOR = create(80000);

    private static final CacheQueue<Object, AbstractBufferSource> CACHED_BUFFER_SOURCES = new CacheQueue<>(Duration.ofSeconds(30));
    private static final CacheQueue<VertexConsumer, IVertexConsumer> CACHED_BUFFER_BUILDERS = new CacheQueue<>(Duration.ofSeconds(30));

    private final Supplier<MultiBufferSource> bufferSourceProvider;

    private AbstractBufferSource(Supplier<MultiBufferSource> bufferSourceProvider) {
        this.bufferSourceProvider = bufferSourceProvider;
    }

    public static AbstractBufferSource buffer() {
        return DEFAULT;
    }

    public static AbstractBufferSource outline() {
        return OUTLINE;
    }

    public static AbstractBufferSource tesselator() {
        return TESSELATOR;
    }

    public static AbstractBufferSource create(int size) {
        return create(AbstractBufferSourceImpl.immediateSource(size));
    }

    public static AbstractBufferSource create(MultiBufferSource bufferSource) {
        return create(() -> bufferSource);
    }

    public static AbstractBufferSource create(Supplier<MultiBufferSource> bufferSourceProvider) {
        return new AbstractBufferSource(bufferSourceProvider);
    }

    public static AbstractBufferSource wrap(MultiBufferSource bufferSource) {
        var bufferSource1 = CACHED_BUFFER_SOURCES.get(bufferSource);
        if (bufferSource1 == null) {
            bufferSource1 = create(bufferSource);
            CACHED_BUFFER_SOURCES.put(bufferSource, bufferSource1);
        }
        return bufferSource1;
    }

    public static MultiBufferSource unwrap(IBufferSource bufferSource) {
        return ((AbstractBufferSource) bufferSource).bufferSource();
    }

    @Override
    public IVertexConsumer getBuffer(RenderType renderType) {
        var builder = bufferSource().getBuffer(renderType);
        return CACHED_BUFFER_BUILDERS.computeIfAbsent(builder, AbstractVertexConsumer::of);
    }

    @Override
    public void endBatch() {
        if (bufferSource() instanceof MultiBufferSource.BufferSource bufferSource1) {
            bufferSource1.endBatch();
        }
    }

    public MultiBufferSource bufferSource() {
        return bufferSourceProvider.get();
    }
}
