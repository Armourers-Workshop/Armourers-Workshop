package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.client.IMeshData;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;

public interface ShaderVertexObject {

    boolean isEmissive();

    boolean isTranslucent();

    boolean isOutline();

    int overlay();

    int lightmap();

    int outlineColor();

    float polygonOffset();

    IMeshData data();

    OpenPoseStack.Pose pose();

    IRenderType type();

    void retain();

    void release();
}
