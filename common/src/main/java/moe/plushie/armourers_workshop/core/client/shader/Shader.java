package moe.plushie.armourers_workshop.core.client.shader;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayObject;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexObject;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModDebugger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public abstract class Shader {

    protected final Int2ObjectOpenHashMap<OpenMatrix4f> overlayMatrices = new Int2ObjectOpenHashMap<>();
    protected final Int2ObjectOpenHashMap<OpenMatrix4f> lightmapMatrices = new Int2ObjectOpenHashMap<>();
    protected final Int2ObjectOpenHashMap<OpenVector4f> outlineColors = new Int2ObjectOpenHashMap<>();

    protected final ShaderContext context = ShaderContext.getInstance();
    protected final ShaderRenderState renderState = new ShaderRenderState();

    public void begin() {
        context.saveState();
        context.setModelViewMatrix(OpenMatrix4f.identity());
        context.setMatrixFlags(0x01);
        context.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        ShaderUniforms.begin();

        if (ModDebugger.wireframeRender) {
            context.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }
    }

    public void end() {
        if (ModDebugger.wireframeRender) {
            context.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        ShaderUniforms.end();

        context.setMatrixFlags(0x00);
        context.restoreState();
    }

    protected void prepare(ShaderVertexGroup group) {
        renderState.save();
        // apply changes of texture animation.
        context.setTextureMatrix(group.getTextureMatrix(TickUtils.animationTicks()));
        context.enablePolygonOffset();
    }

    protected void clean(ShaderVertexGroup group) {
        context.disablePolygonOffset();
        VertexArrayObject.unbind();
        renderState.load();
    }

    public void apply(ShaderVertexGroup group, Runnable action) {
        prepare(group);
        action.run();
        clean(group);
    }

    public void render(ShaderVertexObject object) {
        var pose = object.pose();

        // we need fast update the uniforms,
        // so we're never using from vanilla uniforms.
        context.setObjectViewMatrix(pose.pose());
        context.setObjectNormalMatrix(pose.normal());
        context.setOverlayTextureMatrix(getOverlayTextureMatrix(object));
        context.setLightmapTextureMatrix(getLightmapTextureMatrix(object));
        context.setColorModulator(getColorColorModulator(object));
        context.setMatrixFlags(pose.properties() | 0x01);

        // https://web.archive.org/web/20201010072314/https://sites.google.com/site/threejstuts/home/polygon_offset
        // For polygons that are parallel to the near and far clipping planes, the depth slope is zero.
        // For the polygons in your scene with a depth slope near zero, only a small, constant offset is needed.
        // To create a small, constant offset, you can pass factor = 0.0 and units = 1.0.
        context.polygonOffset(0.0f, -50.0f + object.polygonOffset() * -1f);

        // yes, we need update the uniform every render call.
        // maybe need query uniform from current shader.
        ShaderUniforms.getInstance().apply(getLastProgramId());

        // ..
        drawElements(object, object.arrayObject(), object.indexObject(), object.vertexCount());
    }

    protected void drawElements(ShaderVertexObject vertexObject, VertexArrayObject arrayObject, @Nullable VertexIndexObject indexObject, int count) {
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
        if (object.overlay() == OverlayTexture.NO_OVERLAY) {
            return OpenMatrix4f.identity();
        }
        // a special matrix, function is reset location of the texture.
        return overlayMatrices.computeIfAbsent(object.overlay(), overlay -> {
            var u = OverlayTexture.getU(overlay);
            var v = OverlayTexture.getV(overlay);
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
        return lightmapMatrices.computeIfAbsent(object.lightmap(), lightmap -> {
            var u = LightmapTexture.getU(lightmap);
            var v = LightmapTexture.getV(lightmap);
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
        return outlineColors.computeIfAbsent(object.outlineColor() | 0xff000000, color -> {
            float red = Colors.getRed(color) / 255f;
            float green = Colors.getGreen(color) / 255f;
            float blue = Colors.getBlue(color) / 255f;
            return new OpenVector4f(red, green, blue, 1f);
        });
    }
}
