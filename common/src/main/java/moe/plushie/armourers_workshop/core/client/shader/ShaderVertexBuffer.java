package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.compat.client.renderer.shader.AbstractShaderVertexBuffer;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public abstract class ShaderVertexBuffer extends ReferenceCounted {

    public static ShaderVertexBuffer newInstance() {
        return new AbstractShaderVertexBuffer();
    }

    public abstract void upload(ByteBuffer byteBuffer);

    public abstract Slice slice(int offset, int count, IVertexFormat.Mode mode, IVertexFormat format);

    public interface Slice {

        default void draw() {
            // nop
        }

        int offset();

        int count();

        IVertexFormat.Mode mode();

        IVertexFormat format();

        void retain();

        void release();
    }
}
