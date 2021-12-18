package moe.plushie.armourers_workshop.core.render.buffer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public class SkinVertexBuffer implements AutoCloseable {

    protected final VertexFormat format;
    protected int id;
    protected int vertexCount;

    public SkinVertexBuffer(VertexFormat format) {
        this.format = format;
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
            RenderSystem.recordRenderCall(() -> {
                this.uploadStatic(builder);
            });
        } else {
            this.uploadStatic(builder);
        }
    }

    public CompletableFuture<Void> uploadLater(BufferBuilder builder) {
        if (!RenderSystem.isOnRenderThread()) {
            return CompletableFuture.runAsync(() -> {
                this.uploadStatic(builder);
            }, (task) -> {
                RenderSystem.recordRenderCall(task::run);
            });
        } else {
            this.uploadStatic(builder);
            return CompletableFuture.completedFuture((Void) null);
        }
    }

    protected void uploadStatic(BufferBuilder builder) {
        if (this.id < 0) {
            return;
        }
        Pair<BufferBuilder.DrawState, ByteBuffer> pair = builder.popNextBuffer();
        ByteBuffer bytebuffer = pair.getSecond();
        this.vertexCount = bytebuffer.remaining() / this.format.getVertexSize();
        this.bind();
        RenderSystem.glBufferData(GL15.GL_ARRAY_BUFFER, bytebuffer, GL15.GL_STATIC_DRAW);
        unbind();
    }

    public void draw(Matrix4f matrix, int mode) {
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix);
        RenderSystem.drawArrays(mode, 0, this.vertexCount);
        RenderSystem.popMatrix();
    }

    public void close() {
        if (this.id < 0) {
            return;
        }
        RenderSystem.glDeleteBuffers(this.id);
        this.id = -1;
    }

    public VertexFormat getFormat() {
        return format;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}