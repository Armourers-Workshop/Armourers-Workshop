package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ext.OpenPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

@Environment(value = EnvType.CLIENT)
public class SkinRenderObject implements AutoCloseable {

    protected int id = -1;

    public SkinRenderObject() {
        RenderSystem.glGenBuffers(id -> this.id = id);
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
        OpenPoseStack poseStack = RenderSystem.getResolvedModelViewStack();
        poseStack.pushPose();
        poseStack.mulPose(matrix);
        GL11.glDrawArrays(mode, 0, vertexCount);
        poseStack.popPose();
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

