package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Matrix4f;

public interface SkinRenderTask {

    int getLightmap();

    int getVertexOffset();

    int getVertexCount();

    Matrix4f getMatrix();

    RenderType getRenderType();

    SkinVertexBuffer getVertexBuffer();

    default void render(SkinRenderType renderType, int index, int maxVertexCount) {
        SkinLightBuffer lightBuffer = null;
        SkinVertexBuffer vertexBuffer = getVertexBuffer();

        if (index != 0) {
            RenderSystem.enablePolygonOffset();
            RenderSystem.polygonOffset(index * -0.1f, -3f);
        }

        if (renderType.usesLight()) {
            lightBuffer = SkinLightBuffer.getLightBuffer(getLightmap());
            lightBuffer.ensureCapacity(maxVertexCount);
            lightBuffer.bind();
            lightBuffer.getFormat().setupBufferState(0L);
        }

        vertexBuffer.bind();
        renderType.format().setupBufferState(getVertexOffset());

        vertexBuffer.draw(getMatrix(), renderType.mode(), getVertexCount());

        renderType.format().clearBufferState();

        if (lightBuffer != null) {
            lightBuffer.getFormat().clearBufferState();
        }

        if (index != 0) {
            RenderSystem.disablePolygonOffset();
            RenderSystem.polygonOffset(0f, 0f);
        }
    }
}
