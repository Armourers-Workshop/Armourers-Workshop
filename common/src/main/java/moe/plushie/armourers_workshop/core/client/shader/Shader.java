package moe.plushie.armourers_workshop.core.client.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderObject;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderState;
import moe.plushie.armourers_workshop.core.client.other.VertexArrayBuffer;
import moe.plushie.armourers_workshop.core.client.other.VertexIndexBuffer;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public abstract class Shader {

    private int lastMaxVertexCount = 0;

    private int lastLightmap = 0;

    private OpenMatrix4f lastLightmapMat;

    private final SkinRenderState renderState = new SkinRenderState();
    private final VertexArrayBuffer arrayBuffer = new VertexArrayBuffer();
    private final VertexIndexBuffer indexBuffer = new VertexIndexBuffer(4, 6, (builder, index) -> {
        builder.accept(index);
        builder.accept(index + 1);
        builder.accept(index + 2);
        builder.accept(index + 2);
        builder.accept(index + 3);
        builder.accept(index);
    });

    public void begin() {
        RenderSystem.backupExtendedFog();
        RenderSystem.backupExtendedMatrix();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Float.MAX_VALUE);
        RenderSystem.setExtendedMatrixFlags(0x80);
        //RenderSystem.setExtendedTextureMatrix(OpenMatrix4f.createTranslateMatrix(0, TickUtils.getPaintTextureOffset() / 256.0f, 0));
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
        renderState.push();
        lastMaxVertexCount = group.maxVertexCount;
        arrayBuffer.bind();
        // apply changes of texture animation.
        RenderSystem.setExtendedTextureMatrix(group.getTextureMatrix(TickUtils.ticks()));
    }

    protected void clean(ShaderVertexGroup group) {
        SkinRenderObject.unbind();
        indexBuffer.unbind();
        arrayBuffer.unbind();
        renderState.pop();
    }

    public void apply(ShaderVertexGroup group, Runnable action) {
        prepare(group);
        action.run();
        clean(group);
    }

    public void render(ShaderVertexObject object) {
        int vertexes = toTriangleVertex(object.getVertexCount());
        int maxVertexes = toTriangleVertex(lastMaxVertexCount);
        auto entry = object.getPoseStack().last();

        // we need fast update the uniforms,
        // so we're never using from vanilla uniforms.
        RenderSystem.setExtendedLightmapTextureMatrix(getLightmapTextureMatrix(object));
        RenderSystem.setExtendedNormalMatrix(entry.normal());
        RenderSystem.setExtendedModelViewMatrix(entry.pose());

        // yes, we need update the uniform every render call.
        // maybe need query uniform from current shader.
        ShaderUniforms.getInstance().apply();

        // bind VBO and and setup vertex pointer,
        // the vertex offset no longer supported in vanilla,
        // so we need a special version of the format setup.
        object.getVertexBuffer().bind();
        setupVertexFormat(object.getFormat(), object.getVertexOffset());
        setupPolygonState(object);

        // in the newer version rendering system, we will use a shader.
        // and shader requires we to split the quad into two triangles,
        // so we need use index buffer to control size of the vertex data.
        indexBuffer.bind(maxVertexes);

        draw(object.getType(), indexBuffer.type(), vertexes, 0);

        cleanPolygonState(object);
        cleanVertexFormat(object.getFormat());
    }

    protected abstract void draw(RenderType renderType, VertexIndexBuffer.IndexType indexType, int count, int indices);

    protected void setupPolygonState(ShaderVertexObject object) {
        float polygonOffset = object.getPolygonOffset();
        if (polygonOffset != 0) {
            // https://sites.google.com/site/threejstuts/home/polygon_offset
            // For polygons that are parallel to the near and far clipping planes, the depth slope is zero.
            // For the polygons in your scene with a depth slope near zero, only a small, constant offset is needed.
            // To create a small, constant offset, you can pass factor = 0.0 and units = 1.0.
            RenderSystem.polygonOffset(0, polygonOffset * -1);
            RenderSystem.enablePolygonOffset();
        }
    }

    protected void cleanPolygonState(ShaderVertexObject object) {
        float polygonOffset = object.getPolygonOffset();
        if (polygonOffset != 0) {
            RenderSystem.polygonOffset(0f, 0f);
            RenderSystem.disablePolygonOffset();
        }
    }

    private void setupVertexFormat(VertexFormat format, int offset) {
        int strict = format.getVertexSize();
        auto elements = format.getElements();
        for (int index = 0; index < elements.size(); ++index) {
            auto element = elements.get(index);
            element.setupBufferState(index, offset, strict);
            offset += element.getByteSize();
        }
    }

    private void cleanVertexFormat(VertexFormat vertexFormat) {
        vertexFormat.clearBufferState();
    }

    private OpenMatrix4f getLightmapTextureMatrix(ShaderVertexObject object) {
        // We specified the fully lighting when create the vertex,
        // so we don't need any change when growing is required.
        if (object.isGrowing()) {
            return OpenMatrix4f.identity();
        }
        // we only recreate when something changes.
        int lightmap = object.getLightmap();
        if (lastLightmapMat == null || lightmap != lastLightmap) {
            int u = lightmap & 0xffff;
            int v = (lightmap >> 16) & 0xffff;
            // a special matrix, function is reset location of the texture.
            OpenMatrix4f newValue = OpenMatrix4f.createScaleMatrix(0, 0, 0);
            newValue.m03 = u;
            newValue.m13 = v;
            lastLightmap = lightmap;
            lastLightmapMat = newValue;
        }
        return lastLightmapMat;
    }

    private int toTriangleVertex(int count) {
        return count + count / 2;
    }
}
