package moe.plushie.armourers_workshop.core.client.shader;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderState;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexObject;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

@Environment(EnvType.CLIENT)
public abstract class Shader {

    private final Int2ObjectOpenHashMap<OpenMatrix4f> overlayMatrices = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<OpenMatrix4f> lightmapMatrices = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<OpenVector4f> outlineColors = new Int2ObjectOpenHashMap<>();
    private final SkinRenderState renderState = new SkinRenderState();

    public void begin() {
        RenderSystem.backupExtendedMatrix();
        RenderSystem.setExtendedMatrixFlags(0x01);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        ShaderUniforms.begin();

        if (ModDebugger.wireframeRender) {
            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }
    }

    public void end() {
        if (ModDebugger.wireframeRender) {
            RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        ShaderUniforms.end();
        RenderSystem.setExtendedMatrixFlags(0x00);
        RenderSystem.restoreExtendedMatrix();
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
        RenderSystem.setExtendedOverlayTextureMatrix(getOverlayTextureMatrix(object));
        RenderSystem.setExtendedLightmapTextureMatrix(getLightmapTextureMatrix(object));
        RenderSystem.setExtendedColorModulator(getColorColorModulator(object));
        RenderSystem.setExtendedMatrixFlags(entry.properties() | 0x01);
        RenderSystem.setExtendedNormalMatrix(entry.normal());
        RenderSystem.setExtendedModelViewMatrix(entry.pose());

        // https://web.archive.org/web/20201010072314/https://sites.google.com/site/threejstuts/home/polygon_offset
        // For polygons that are parallel to the near and far clipping planes, the depth slope is zero.
        // For the polygons in your scene with a depth slope near zero, only a small, constant offset is needed.
        // To create a small, constant offset, you can pass factor = 0.0 and units = 1.0.
        RenderSystem.polygonOffset(0.0f, -50.0f + object.getPolygonOffset() * -1f);

        // yes, we need update the uniform every render call.
        // maybe need query uniform from current shader.
        ShaderUniforms.getInstance().apply(getLastProgramId());

        // ..
        drawElements(object, object.getArrayObject(), object.getIndexObject(), object.getTotal());
    }

    protected void drawElements(ShaderVertexObject vertexObject, VertexArrayObject arrayObject, VertexIndexObject indexObject, int count) {
        arrayObject.bind();
        if (indexObject != null) {
            GL15.glDrawElements(GL15.GL_TRIANGLES, indexObject.stride(count), indexObject.type(), 0);
        } else {
            GL15.glDrawArrays(GL15.GL_TRIANGLES, 0, count);
        }
    }

    protected int getLastProgramId() {
        return renderState.lastProgramId();
    }

    protected OpenMatrix4f getOverlayTextureMatrix(ShaderVertexObject object) {
        // We specified the no overlay when create the vertex,
        // so we don't need any change when no overlay is required.
        if (object.getOverlay() == OverlayTexture.NO_OVERLAY) {
            return OpenMatrix4f.identity();
        }
        // a special matrix, function is reset location of the texture.
        return overlayMatrices.computeIfAbsent(object.getOverlay(), overlay -> {
            var u = overlay & 0xffff;
            var v = (overlay >> 16) & 0xffff;
            var newValue = OpenMatrix4f.createScaleMatrix(0, 0, 0);
            newValue.setTranslation(u, v, 0);
            return newValue;
        });
    }

    protected OpenMatrix4f getLightmapTextureMatrix(ShaderVertexObject object) {
        // We specified the fully lighting when create the vertex,
        // so we don't need any change when growing is required.
        if (object.isEmissive()) {
            return OpenMatrix4f.identity();
        }
        // a special matrix, function is reset location of the texture.
        return lightmapMatrices.computeIfAbsent(object.getLightmap(), lightmap -> {
            var u = lightmap & 0xffff;
            var v = (lightmap >> 16) & 0xffff;
            var newValue = OpenMatrix4f.createScaleMatrix(0, 0, 0);
            newValue.setTranslation(u, v, 0);
            return newValue;
        });
    }

    protected OpenVector4f getColorColorModulator(ShaderVertexObject object) {
        if (object.isOutline()) {
            return getOutlineColor(object);
        }
        return OpenVector4f.ONE;
    }

    protected OpenVector4f getOutlineColor(ShaderVertexObject object) {
        return outlineColors.computeIfAbsent(object.getOutlineColor() | 0xff000000, color -> {
            float red = ColorUtils.getRed(color) / 255f;
            float green = ColorUtils.getGreen(color) / 255f;
            float blue = ColorUtils.getBlue(color) / 255f;
            return new OpenVector4f(red, green, blue, 1f);
        });
    }
}
