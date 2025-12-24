package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.texture.ColorModulator;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModDebugger;

@SuppressWarnings("unsed")
@OnlyIn(Dist.CLIENT)
public class Shader {

    protected final ShaderContext context = ShaderContext.newInstance();

    public void setupRenderState() {
        context.saveStates();
        context.saveMatrices();

        context.setTintColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.setModelViewMatrix(OpenMatrix4f.identity());
        context.setMatrixFlags(0x01);

        if (ModDebugger.wireframeRender) {
            context.setPolygonMode(1);
        }
    }

    public void setupRenderState(ShaderVertexGroup group) {
        context.saveObjects();
        context.saveUniforms();
        context.setPolygonOffset(0.0f, -50.0f);

        // apply changes of texture animation.
        context.setTextureMatrix(group.getTextureMatrix(TickUtils.animationTick()));
    }

    public void render(ShaderVertexObject object, ShaderVertexGroup group) {
        var pose = object.pose();

        // we need fast update the uniforms,
        // so we're never using from vanilla uniforms.
        context.setMatrixFlags(pose.properties() | 0x01);
        context.setColorModulator(ColorModulator.getColor(object.outlineColor()));
        context.setObjectViewMatrix(pose.pose());
        context.setObjectNormalMatrix(pose.normal());
        context.setOverlayTextureMatrix(OverlayTexture.getTextureMatrix(object.overlay()));
        context.setLightmapTextureMatrix(LightmapTexture.getTextureMatrix(object.lightmap(), object.isEmissive()));

        // https://web.archive.org/web/20201010072314/https://sites.google.com/site/threejstuts/home/polygon_offset
        // For polygons that are parallel to the near and far clipping planes, the depth slope is zero.
        // For the polygons in your scene with a depth slope near zero, only a small, constant offset is needed.
        // To create a small, constant offset, you can pass factor = 0.0 and units = 1.0.
        context.setPolygonOffset(0.0f, -50.0f + object.polygonOffset() * -1f);

        // yes, we need update the uniform every render call.
        // maybe need query uniform from current shader.
        context.applyUniforms();

        // submit draw into renderer.
        context.draw(object);
    }

    public void clearRenderState(ShaderVertexGroup group) {
        context.setPolygonOffset(0.0f, 0.0f);
        context.restoreUniforms();
        context.restoreObjects();
    }

    public void clearRenderState() {
        if (ModDebugger.wireframeRender) {
            context.setPolygonMode(0);
        }

        context.setMatrixFlags(0x00);

        context.restoreMatrices();
        context.restoreStates();
    }
}
