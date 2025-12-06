package moe.plushie.armourers_workshop.compat.client.shader;

import com.mojang.blaze3d.vertex.BufferUploader;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.utils.RenderSystem;

@Available("[1.18, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractShaderContextImpl extends AbstractShaderContext {

    protected static final AbstractShaderContextImpl INSTANCE = new AbstractShaderContextImpl();

    @Override
    public void saveMatrices() {
        super.saveMatrices();
        RenderSystem.resetTextureMatrix();
    }

    @Override
    public void restoreMatrices() {
        super.restoreMatrices();
    }

    @Override
    public void saveObjects() {
        super.saveObjects();
        BufferUploader.reset();
    }

    @Override
    public void restoreObjects() {
        VertexArrayObject.unbind();
        super.restoreObjects();
    }
}
