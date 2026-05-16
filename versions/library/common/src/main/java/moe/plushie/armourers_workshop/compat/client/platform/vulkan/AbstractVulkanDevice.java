package moe.plushie.armourers_workshop.compat.client.platform.vulkan;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IMeshData;
import moe.plushie.armourers_workshop.compat.client.platform.AbstractRenderBuffer;
import moe.plushie.armourers_workshop.compat.client.platform.AbstractRenderDevice;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public class AbstractVulkanDevice implements AbstractRenderDevice {

    @Override
    public void beginTransaction() {

    }

    @Override
    public void beginGroup() {

    }

    @Override
    public void draw(IMeshData meshData) {

    }

    @Override
    public void endGroup() {

    }

    @Override
    public void endTransaction() {

    }

    @Override
    public void setTextureMatrix(OpenMatrix4f matrix) {

    }

    @Override
    public void setModelViewMatrix(OpenMatrix4f matrix) {

    }

    @Override
    public void setOverlayTextureMatrix(OpenMatrix4f matrix) {

    }

    @Override
    public void setLightmapTextureMatrix(OpenMatrix4f matrix) {

    }

    @Override
    public void setObjectViewMatrix(OpenMatrix4f matrix) {

    }

    @Override
    public void setObjectNormalMatrix(OpenMatrix3f matrix) {

    }

    @Override
    public void setMatrixFlags(int flags) {

    }

    @Override
    public void setTintColor(float r, float g, float b, float a) {

    }

    @Override
    public void setColorModulator(OpenVector4f value) {

    }

    @Override
    public void setPolygonMode(int mode) {

    }

    @Override
    public void setPolygonOffset(float factor, float units) {

    }

    @Override
    public AbstractRenderBuffer createBuffer(ByteBuffer bytes) {
        return null;
    }
}
