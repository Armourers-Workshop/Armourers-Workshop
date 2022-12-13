package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.vertex.BufferUploader;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderExecutor;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexBuffer;
import moe.plushie.armourers_workshop.core.client.shader.Shader;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexGroup;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL15;

@Available("[1.18, )")
@Environment(value = EnvType.CLIENT)
public class AbstractShader extends Shader {

    @Override
    public void begin() {
        super.begin();
        // yep we reset it.
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().setIdentity();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.resetTextureMatrix();
        // ..
        BufferUploader.reset();
    }

    @Override
    public void end() {
        super.end();
        // ..
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public void apply(ShaderVertexGroup group, Runnable action) {
        // we let the vanilla's rendering system normal call rendering once,
        // and then insert our the rendering content in end stage.
        SkinRenderExecutor.execute(group.getType(), () -> super.apply(group, action));
    }

    @Override
    protected void draw(RenderType renderType, VertexIndexBuffer.IndexType indexType, int count, int indices) {
        GL15.glDrawElements(renderType.mode().asGLMode, count, indexType.asGLType, indices);
    }
}
