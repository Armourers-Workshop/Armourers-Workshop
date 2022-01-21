package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public class SkinVertexBuffer implements AutoCloseable {

    protected int id = -1;

    public SkinVertexBuffer() {
        RenderSystem.glGenBuffers((id) -> {
            this.id = id;
        });
    }

    public static void unbind() {
        RenderSystem.glBindBuffer(GL15.GL_ARRAY_BUFFER, () -> 0);
    }

    public void bind() {
        RenderSystem.glBindBuffer(GL15.GL_ARRAY_BUFFER, () -> this.id);
    }

    public void upload(BufferBuilder builder) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.upload(builder));
            return;
        }
        if (this.id < 0) {
            return;
        }
        Pair<BufferBuilder.DrawState, ByteBuffer> pair = builder.popNextBuffer();
        upload(pair.getSecond());
    }

    public void upload(ByteBuffer byteBuffer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.upload(byteBuffer));
            return;
        }
        if (this.id < 0) {
            return;
        }
        this.bind();
        RenderSystem.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);
        unbind();
    }

    public void draw(Matrix4f matrix, int mode, int vertexCount) {
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix);
        RenderSystem.drawArrays(mode, 0, vertexCount);
        RenderSystem.popMatrix();
    }

    public void close() {
        if (this.id < 0) {
            return;
        }
        RenderSystem.glDeleteBuffers(this.id);
        this.id = -1;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}

