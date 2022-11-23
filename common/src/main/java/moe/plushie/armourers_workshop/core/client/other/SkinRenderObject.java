package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.api.common.IRenderBufferObject;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

@Environment(value = EnvType.CLIENT)
public class SkinRenderObject implements IRenderBufferObject, AutoCloseable {

    protected int id = -1;

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

    public void upload(BufferBuilder builder) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.upload(builder));
            return;
        }
        if (this.id < 0) {
            return;
        }
        upload(builder.popNextBuffer().getSecond());
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
//        PoseStack poseStack = RenderSystem.getModelStack();
//        poseStack.pushPose();
//        //poseStack.mulPose(matrix);
//        poseStack.mulPoseMatrix(matrix);
//        RenderSystem.applyModelViewMatrix();
//        GL11.glDrawArrays(mode, 0, vertexCount);
    }

    public void close() {
        if (this.id < 0) {
            return;
        }
        int id = this.id;
        Minecraft.getInstance().submit(() -> RenderSystem.glDeleteBuffers(id));
        this.id = -1;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}

