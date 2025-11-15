package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.core.client.other.VertexBufferObject;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexObject;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;

public interface ShaderVertexObject {

    int vertexOffset();

    int vertexCount();

    VertexArrayObject arrayObject();

    VertexBufferObject bufferObject();

    VertexIndexObject indexObject();

    int overlay();

    int lightmap();

    int outlineColor();

    float polygonOffset();

    OpenPoseStack.Pose pose();

    IVertexFormat format();

    IRenderType type();

    boolean isEmissive();

    boolean isTranslucent();

    boolean isOutline();

    void retain();

    void release();
}
