package moe.plushie.armourers_workshop.compatibility.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AbstractBufferSource implements IBufferSource {

    private static final AbstractBufferSource DEFAULT = create(AbstractBufferSourceImpl.bufferSource());
    private static final AbstractBufferSource TESSELATOR = create(AbstractBufferSourceImpl.immediateSource(80000));
    private static final AbstractBufferSource OUTLINE = create(AbstractBufferSourceImpl.outlineBufferSource());

    private static final Cache<Object, AbstractBufferSource> CACHED_BUFFER_SOURCES = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.SECONDS)
            //.removalListener(AbstractBufferSource::release)
            .build();

    private final HashMap<VertexConsumer, IVertexConsumer> cachedBuffers = new HashMap<>();
    private final MultiBufferSource bufferSource;

    private AbstractBufferSource(MultiBufferSource bufferSource) {
        this.bufferSource = bufferSource;
    }

    public static AbstractBufferSource buffer() {
        return DEFAULT;
    }

    public static AbstractBufferSource create(int size) {
        return new AbstractBufferSource(AbstractBufferSourceImpl.immediateSource(size));
    }

    public static AbstractBufferSource create(MultiBufferSource bufferSource) {
        return new AbstractBufferSource(bufferSource);
    }

    public static AbstractBufferSource outline() {
        return OUTLINE;
    }

    public static AbstractBufferSource tesselator() {
        return TESSELATOR;
    }

    public static AbstractBufferSource wrap(MultiBufferSource bufferSource) {
        var bufferSource1 = CACHED_BUFFER_SOURCES.getIfPresent(bufferSource);
        if (bufferSource1 == null) {
            bufferSource1 = create(bufferSource);
            CACHED_BUFFER_SOURCES.put(bufferSource, bufferSource1);
        }
        return bufferSource1;
    }

    public static MultiBufferSource unwrap(IBufferSource bufferSource) {
        return ((AbstractBufferSource) bufferSource).bufferSource;
    }

    @Override
    public IVertexConsumer getBuffer(RenderType renderType) {
        var builder = bufferSource.getBuffer(renderType);
        return cachedBuffers.computeIfAbsent(builder, AbstractVertexConsumer::of);
    }

    @Override
    public void endBatch() {
        if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
            bufferSource1.endBatch();
        }
    }

    public MultiBufferSource bufferSource() {
        return bufferSource;
    }
}
