package moe.plushie.armourers_workshop.core.client.shader;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderState;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexObject;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

@Environment(EnvType.CLIENT)
public abstract class Shader {

    private final Int2ObjectOpenHashMap<Vector4f> overlayMatrices = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<OpenMatrix4f> lightmapMatrices = new Int2ObjectOpenHashMap<>();
    private final SkinRenderState renderState = new SkinRenderState();

    public void begin() {
        RenderSystem.backupExtendedFog();
        RenderSystem.backupExtendedMatrix();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Float.MAX_VALUE);
        RenderSystem.setExtendedMatrixFlags(0x80);
        ShaderUniforms.begin();

        if (ModDebugger.wireframeRender) {
            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }
    }

    public void end() {
        if (ModDebugger.wireframeRender) {
            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        RenderSystem.setExtendedMatrixFlags(0x00);
        ShaderUniforms.end();
        RenderSystem.restoreExtendedMatrix();
        RenderSystem.restoreExtendedFog();
    }

    protected void prepare(ShaderVertexGroup group) {
        renderState.save();
        // apply changes of texture animation.
        RenderSystem.setExtendedTextureMatrix(group.getTextureMatrix(TickUtils.animationTicks()));
        RenderSystem.enablePolygonOffset();
    }

    protected void clean(ShaderVertexGroup group) {
        RenderSystem.disablePolygonOffset();
        VertexArrayObject.unbind();
        renderState.load();
    }

    public void apply(ShaderVertexGroup group, Runnable action) {
        prepare(group);
        action.run();
        clean(group);
    }

    public void render(ShaderVertexObject object) {
        var entry = object.getPoseStack().last();

        // we need fast update the uniforms,
        // so we're never using from vanilla uniforms.
        //RenderSystem.setExtendedColorModulator(getColorModulator(object));
        RenderSystem.setExtendedLightmapTextureMatrix(getLightmapTextureMatrix(object));
        RenderSystem.setExtendedNormalMatrix(entry.normal());
        RenderSystem.setExtendedModelViewMatrix(entry.pose());

        // https://sites.google.com/site/threejstuts/home/polygon_offset
        // For polygons that are parallel to the near and far clipping planes, the depth slope is zero.
        // For the polygons in your scene with a depth slope near zero, only a small, constant offset is needed.
        // To create a small, constant offset, you can pass factor = 0.0 and units = 1.0.
        RenderSystem.polygonOffset(0, object.getPolygonOffset() * -1);

        // yes, we need update the uniform every render call.
        // maybe need query uniform from current shader.
        ShaderUniforms.getInstance().apply(getLastProgramId());

        // ..
        drawElements(object, object.getArrayObject(), object.getIndexObject(), object.getTotal());
    }

    protected void drawElements(ShaderVertexObject vertexObject, VertexArrayObject arrayObject, VertexIndexObject indexObject, int count) {
        // convert quad vertexes to triangle vertexes.
        count += count / 2;
        arrayObject.bind();
        GL15.glDrawElements(GL15.GL_TRIANGLES, count, indexObject.type().asGLType, 0);
    }

    protected int getLastProgramId() {
        return renderState.lastProgramId();
    }

    protected Vector4f getColorModulator(ShaderVertexObject object) {
        // We specified the on overlay  when create the vertex,
        // so we don't need any change when overlay is required.
        var test = OverlayTexture.pack(0, true);
        if (test == OverlayTexture.NO_OVERLAY) {
            return Vector4f.ONE;
        }
        // a special matrix, function is reset location of the texture.
        return overlayMatrices.computeIfAbsent(test, overlay -> {
            var u = overlay & 0xffff;
            var v = (overlay >> 16) & 0xffff;
            var k = (1.0f - (float)v / 15.0f * 0.75f);
            return new Vector4f(k, 1, 1, 1);
        });
    }

    protected OpenMatrix4f getLightmapTextureMatrix(ShaderVertexObject object) {
        // We specified the fully lighting when create the vertex,
        // so we don't need any change when growing is required.
        if (object.isGrowing()) {
            return OpenMatrix4f.identity();
        }
        // a special matrix, function is reset location of the texture.
        return lightmapMatrices.computeIfAbsent(object.getLightmap(), lightmap -> {
            var u = lightmap & 0xffff;
            var v = (lightmap >> 16) & 0xffff;
            OpenMatrix4f newValue = OpenMatrix4f.createScaleMatrix(0, 0, 0);
            newValue.m03 = u;
            newValue.m13 = v;
            return newValue;
        });
    }
}
