package moe.plushie.armourers_workshop.compat.client.platform;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IMeshData;
import moe.plushie.armourers_workshop.api.client.IVertexBuffer;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public interface AbstractRenderDevice {

    /**
     * Retrieves the current device of {@code AbstractRenderDevice}.
     */
    static AbstractRenderDevice current() {
        return AbstractRenderDeviceImpl.CURRENT;
    }

    void beginTransaction();

    void beginGroup();

    void draw(IMeshData meshData);

    void endGroup();

    void endTransaction();


    void setTextureMatrix(OpenMatrix4f matrix);

    void setModelViewMatrix(OpenMatrix4f matrix);

    void setOverlayTextureMatrix(OpenMatrix4f matrix);

    void setLightmapTextureMatrix(OpenMatrix4f matrix);

    void setObjectViewMatrix(OpenMatrix4f matrix);

    void setObjectNormalMatrix(OpenMatrix3f matrix);

    void setMatrixFlags(int flags);

    void setTintColor(float r, float g, float b, float a);

    void setColorModulator(OpenVector4f value);

    void setPolygonMode(int mode); // 0 fill, 1 line

    void setPolygonOffset(float factor, float units);

    /**
     * Creates a new render buffer using the provided byte data.
     *
     * @param bytes the byte buffer containing the data to initialize the render buffer
     * @return an instance of {@code IVertexBuffer} initialized with the provided data
     */
    IVertexBuffer createVertexBuffer(ByteBuffer bytes);
}
