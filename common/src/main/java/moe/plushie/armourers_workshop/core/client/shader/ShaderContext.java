package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;

public interface ShaderContext {

    void saveUniforms();

    void applyUniforms();

    void restoreUniforms();

    void saveObjects();

    void restoreObjects();

    void saveMatrices();

    void restoreMatrices();

    void saveStates();

    void restoreStates();

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
}
