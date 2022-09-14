package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.common.IRenderBufferObject;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.renderer.RenderType;

public class AbstractShaderExecutor {

    private static final AbstractShaderExecutor INSTANCE = new AbstractShaderExecutor();

    private int maxVertexCount = 0;

    public static AbstractShaderExecutor getInstance() {
        return INSTANCE;
    }

    public void setup() {
        RenderSystem.enableRescaleNormal();
    }

    public void clean() {
        RenderSystem.disableRescaleNormal();
    }

    public void setMaxVertexCount(int count) {
        maxVertexCount = count;
    }

    public void execute(IRenderBufferObject object, int vertexOffset, int vertexCount, RenderType renderType) {
        AbstractLightBufferObject lightBuffer = null;
        VertexFormat format = renderType.format();

        if (renderType == SkinRenderType.FACE_SOLID || renderType == SkinRenderType.FACE_TRANSLUCENT) {
            lightBuffer = AbstractLightBufferObject.getLightBuffer(RenderSystem.getShaderLight());
            lightBuffer.ensureCapacity(maxVertexCount);
            lightBuffer.bind();
            lightBuffer.getFormat().setupBufferState(0L);
        }

        object.bind();
        format.setupBufferState(vertexOffset);

        RenderSystem.drawArrays(renderType.mode(), 0, vertexCount);

        format.clearBufferState();

        if (lightBuffer != null) {
            lightBuffer.getFormat().clearBufferState();
        }
    }
}
