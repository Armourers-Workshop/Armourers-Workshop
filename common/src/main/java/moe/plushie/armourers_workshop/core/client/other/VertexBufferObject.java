package moe.plushie.armourers_workshop.core.client.other;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class VertexBufferObject {

    protected int id;
    protected int size = 0;
    protected final AtomicInteger refCount = new AtomicInteger(1);

    public VertexBufferObject() {
        this.id = GL15.glGenBuffers();
    }

    public static void unbind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void bind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
    }

    public void upload(ByteBuffer byteBuffer) {
        if (id < 0) {
            return;
        }
        size = byteBuffer.limit();
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);
        unbind();
    }

//    public void draw(Matrix4f matrix, int mode, int vertexCount) {
//        PoseStack poseStack = RenderSystem.getModelStack();
//        poseStack.pushPose();
//        //poseStack.mulPose(matrix);
//        poseStack.mulPoseMatrix(matrix);
//        RenderSystem.applyModelViewMatrix();
//        GL11.glDrawArrays(mode, 0, vertexCount);
//    }

    void retain() {
        refCount.incrementAndGet();
    }

    void release() {
        int count = refCount.decrementAndGet();
        if (count <= 0) {
            close();
        }
    }

    public void close() {
        if (id < 0) {
            return;
        }
        GL15.glDeleteBuffers(id);
        id = -1;
        refCount.set(0);
    }
}

