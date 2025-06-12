package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.core.client.other.VertexBufferObject;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexObject;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ShaderVertexObject {

    int offset();

    int total();

    VertexArrayObject arrayObject();

    VertexBufferObject bufferObject();

    VertexIndexObject indexObject();

    int overlay();

    int lightmap();

    int outlineColor();

    float polygonOffset();

    OpenPoseStack poseStack();

    IVertexFormat format();

    IRenderType type();

    boolean isEmissive();

    boolean isTranslucent();

    boolean isOutline();

    void release();
}
