package moe.plushie.armourers_workshop.core.render;

import moe.plushie.armourers_workshop.core.skin.data.Skin;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SkinRenderBuffer {

    private static final SkinRenderBuffer INSTANCE = new SkinRenderBuffer();

    protected final Map<Skin, SkinVertexBufferBuilder> bufferBuilders = new HashMap<>();
    protected final Map<Skin, SkinVertexBufferBuilder> cachingBuilders = new HashMap<>();

    public static SkinRenderBuffer getInstance() {
        return INSTANCE;
    }

    public void clear() {
        cachingBuilders.clear();
    }

//    @Nonnull
//    @Override
//    public IVertexBuilder getBuffer(@Nonnull RenderType renderType) {
//        BufferBuilder buffer = buffers.get(renderType);
//        if (buffer != null) {
//            return buffer;
//        }
//        buffer = new BufferBuilder(renderType.bufferSize());
//        buffer.begin(renderType.mode(), renderType.format());
//        buffers.put(renderType, buffer);
//        return buffer;
//    }

    public SkinVertexBufferBuilder getBuffer(@Nonnull Skin skin) {
        SkinVertexBufferBuilder bufferBuilder = bufferBuilders.get(skin);
        if (bufferBuilder != null) {
            return bufferBuilder;
        }
        bufferBuilder = cachingBuilders.computeIfAbsent(skin, SkinVertexBufferBuilder::new);
        bufferBuilders.put(skin, bufferBuilder);
        return bufferBuilder;
    }


    public void endBatch() {
        if (bufferBuilders.isEmpty()) {
            return;
        }
        SkinRenderTask.Group group = new SkinRenderTask.Group();
        for (SkinVertexBufferBuilder builder : bufferBuilders.values()) {
            builder.endBatch(group);
        }
        bufferBuilders.clear();
        group.endBatch();
    }
}
