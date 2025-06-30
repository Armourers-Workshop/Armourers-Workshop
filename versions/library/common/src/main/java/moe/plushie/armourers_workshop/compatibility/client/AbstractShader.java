package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.BufferUploader;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderExecutor;
import moe.plushie.armourers_workshop.core.client.shader.Shader;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexGroup;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public class AbstractShader extends Shader {

    @Override
    public void begin() {
        super.begin();
        // yep we reset it.
        RenderSystem.resetTextureMatrix();
        // ..
        BufferUploader.reset();
    }

    @Override
    public void end() {
        super.end();
    }

    public void apply(ShaderVertexGroup group, Runnable action) {
        // we let the vanilla's rendering system normal call rendering once,
        // and then insert our the rendering content in end stage.
        SkinRenderExecutor.execute(group.renderType(), () -> super.apply(group, action));
    }
}
