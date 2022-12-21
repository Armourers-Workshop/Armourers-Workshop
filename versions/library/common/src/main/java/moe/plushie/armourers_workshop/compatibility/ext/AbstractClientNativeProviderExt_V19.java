package moe.plushie.armourers_workshop.compatibility.ext;

import com.mojang.blaze3d.vertex.BufferBuilder;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.init.provider.ClientNativeFactory;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;

import java.nio.ByteBuffer;

@Available("[1.19, )")
public interface AbstractClientNativeProviderExt_V19 extends ClientNativeProvider, ClientNativeFactory {

    @Override
    default IBufferBuilder createBuilderBuffer(int size) {
        BufferBuilder bufferBuilder = new BufferBuilder(size);
        return new IBufferBuilder() {

            @Override
            public BufferBuilder asBufferBuilder() {
                return bufferBuilder;
            }

            @Override
            public IRenderedBuffer end() {
                BufferBuilder.RenderedBuffer buffer = bufferBuilder.end();
                return new IRenderedBuffer() {
                    @Override
                    public ByteBuffer vertexBuffer() {
                        return buffer.vertexBuffer();
                    }

                    @Override
                    public BufferBuilder.DrawState drawState() {
                        return buffer.drawState();
                    }

                    @Override
                    public void release() {
                        buffer.release();
                    }
                };
            }
        };
    }
}
