package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.api.client.IRenderBufferObject;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class AbstractShaderExecutor {

    private static final AbstractShaderExecutor INSTANCE = new AbstractShaderExecutor();


    private int maxVertexCount = 0;
    private int defaultVertexLight = 0;

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

    public void setDefaultVertexLight(int lightmap) {
        defaultVertexLight = lightmap;
    }

    public void execute(IRenderBufferObject object, int vertexOffset, int vertexCount, RenderType renderType, VertexFormat vertexFormat) {
        AbstractLightBufferObject lightBuffer = null;

        if (renderType == SkinRenderType.FACE_SOLID || renderType == SkinRenderType.FACE_TRANSLUCENT) {
            lightBuffer = AbstractLightBufferObject.getLightBuffer(defaultVertexLight);
            lightBuffer.ensureCapacity(maxVertexCount);
            lightBuffer.bind();
            lightBuffer.getFormat().setupBufferState(0L);
        }

        object.bind();
        vertexFormat.setupBufferState(vertexOffset);

        IPoseStack modelViewStack = RenderSystem.getExtendedModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.multiply(RenderSystem.getExtendedModelViewMatrix());
        RenderSystem.drawArrays(renderType.mode(), 0, vertexCount);
        modelViewStack.popPose();

        vertexFormat.clearBufferState();

        if (lightBuffer != null) {
            lightBuffer.getFormat().clearBufferState();
        }
    }
}
