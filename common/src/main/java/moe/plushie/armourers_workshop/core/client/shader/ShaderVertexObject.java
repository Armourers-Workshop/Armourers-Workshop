package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;

public interface ShaderVertexObject {

    ShaderVertexBuffer.Slice slice();

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
