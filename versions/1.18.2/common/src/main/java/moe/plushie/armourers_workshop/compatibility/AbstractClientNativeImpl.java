package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.init.provider.ClientNativeFactory;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;

import java.nio.ByteBuffer;

public abstract class AbstractClientNativeImpl implements ClientNativeProvider, ClientNativeFactory {

    @Override
    public IBufferBuilder createBuilderBuffer(int size) {
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
