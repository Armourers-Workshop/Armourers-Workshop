package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;

import java.util.List;

public interface ShaderVertexGroup {

    int passCount();

    int vertexCount();

    IRenderType renderType();

    OpenMatrix4f getTextureMatrix(double animationTime);
}
