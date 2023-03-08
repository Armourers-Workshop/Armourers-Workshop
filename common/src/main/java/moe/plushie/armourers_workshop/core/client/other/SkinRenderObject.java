package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderBufferObject;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(value = EnvType.CLIENT)
public class SkinRenderObject implements IRenderBufferObject, AutoCloseable {

    protected int id = -1;
    protected int size = 0;
    protected final AtomicInteger refCount = new AtomicInteger(1);

    public SkinRenderObject() {
        RenderSystem.glGenBuffers(id -> this.id = id);
    }

    public static void unbind() {
        RenderSystem.glBindBuffer(GL15.GL_ARRAY_BUFFER, () -> 0);
    }

    @Override
    public void bind() {
        RenderSystem.glBindBuffer(GL15.GL_ARRAY_BUFFER, () -> this.id);
    }

//    public void upload(BufferBuilder builder) {
//        if (!RenderSystem.isOnRenderThread()) {
//            RenderSystem.recordRenderCall(() -> this.upload(builder));
//            return;
//        }
//        if (this.id < 0) {
//            return;
//        }
//        upload(builder.popNextBuffer().getSecond());
//    }

    public void upload(ByteBuffer byteBuffer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.upload(byteBuffer));
            return;
        }
        if (this.id < 0) {
            return;
        }
        this.bind();
        this.size = byteBuffer.limit();
        RenderSystem.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);
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

    @Override
    public void close() {
        if (this.id < 0) {
            return;
        }
        int id = this.id;
        Minecraft.getInstance().execute(() -> RenderSystem.glDeleteBuffers(id));
        this.id = -1;
        this.refCount.set(0);
    }
}

