package extensions.com.mojang.blaze3d.vertex.BufferBuilder;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;

import java.nio.ByteBuffer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, 1.19)")
@Extension
public class Wrapper {

    public static IBufferBuilder createBuilderBuffer(@ThisClass Class<?> clazz, int size) {
        BufferBuilder bufferBuilder = new BufferBuilder(size);
        return new IBufferBuilder() {
            @Override
            public BufferBuilder asBufferBuilder() {
                return bufferBuilder;
            }

            @Override
            public IRenderedBuffer end() {
                bufferBuilder.end();
                Pair<BufferBuilder.DrawState, ByteBuffer> pair = bufferBuilder.popNextBuffer();
                return new IRenderedBuffer() {
                    @Override
                    public ByteBuffer vertexBuffer() {
                        return pair.getSecond();
                    }

                    @Override
                    public BufferBuilder.DrawState drawState() {
                        return pair.getFirst();
                    }
                };
            }
        };
    }
}
