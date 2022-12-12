package moe.plushie.armourers_workshop.compatibility;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import moe.plushie.armourers_workshop.api.client.IRenderBufferObject;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ShaderUniform;
import moe.plushie.armourers_workshop.utils.ShaderUniforms;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class AbstractShaderExecutor {

    private static final AbstractShaderExecutor INSTANCE = new AbstractShaderExecutor();

    private int maxVertexCount = 0;
    private int defaultVertexLight = 0;

    private int lastLightmap = 0;
    private OpenMatrix4f lastLightmapMat;

    private final OpenMatrix4f noneLightmapMat = OpenMatrix4f.createScaleMatrix(1, 1, 1);

    public static AbstractShaderExecutor getInstance() {
        return INSTANCE;
    }

    public void setup() {
        ShaderUniforms.begin();
        BufferUploader.reset();
        // yep we reset it.
        RenderSystem.getModelViewMatrix().setIdentity();
        RenderSystem.getTextureMatrix().setIdentity();
    }

    public void clean() {
        RenderSystem.applyModelViewMatrix();
        ShaderUniforms.end();
    }

    public void setMaxVertexCount(int count) {
        maxVertexCount = count;
    }

    public void setDefaultVertexLight(int lightmap) {
        defaultVertexLight = lightmap;
    }

    public void execute(IRenderBufferObject object, int vertexOffset, int vertexCount, RenderType renderType, VertexFormat vertexFormat) {
        ShaderUniforms uniforms = ShaderUniforms.getInstance();
        ShaderInstance shader = RenderSystem.getShader();
        if (shader == null) {
            return;
        }

        if (renderType == SkinRenderType.FACE_SOLID || renderType == SkinRenderType.FACE_TRANSLUCENT) {
            // we only recreate when something changes.
            if (lastLightmapMat == null || defaultVertexLight != lastLightmap) {
                int u = defaultVertexLight & 0xffff;
                int v = (defaultVertexLight >> 16) & 0xffff;
                OpenMatrix4f newValue = OpenMatrix4f.createScaleMatrix(0, 0, 0);
                newValue.m03 = u;
                newValue.m13 = v;
                lastLightmap = defaultVertexLight;
                lastLightmapMat = newValue;
            }
            RenderSystem.setExtendedLightmapTextureMatrix(lastLightmapMat);
        } else {
            RenderSystem.setExtendedLightmapTextureMatrix(noneLightmapMat);
        }

//        VertexFormat vertexFormat = renderType.format();
        VertexFormat.Mode mode = renderType.mode();
//            ByteBuffer byteBuffer = pair.getSecond();
//            int i = drawState.vertexCount();
//            int k = i * vertexFormat.getVertexSize();
//            int j = drawState.indexCount();
        int j = vertexCount + vertexCount / 2;
        int i = vertexCount;

        _setupVertexFormat(vertexFormat, object, vertexOffset);

        int m;
        int l = 0;
        if (/*drawState.sequentialIndex()*/true) {
            int q = maxVertexCount + maxVertexCount / 2;
            RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(renderType.mode(), q);
            l = autoStorageIndexBuffer.name();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, l);
            m = autoStorageIndexBuffer.type().asGLType;
//            } else {
//                int n = vertexFormat.getOrCreateIndexBufferObject();
//                    GlStateManager._glBindBuffer(34963, n);
//                byteBuffer.position(k);
//                byteBuffer.limit(k + j * indexType.bytes);
//                GlStateManager._glBufferData(34963, byteBuffer, 35048);
//                m = indexType.asGLType;
        }

        _setupShader(shader);
        shader.apply();
        uniforms.apply();
        GL11.glDrawElements(renderType.mode().asGLMode, j, m, 0L);
        shader.clear();

        _cleanVertexFormat(vertexFormat);

    }

    static int VAO = 0;
    private void _setupVertexFormat(VertexFormat vertexFormat, IRenderBufferObject object, int offset) {
        boolean bl;
        if (VAO == 0) {
            // RenderSystem.glGenVertexArrays();
            VAO = GlStateManager._glGenVertexArrays();
        }
        RenderSystem.glBindVertexArray(() -> VAO);

        object.bind();

//        vertexFormat.setupBufferState();
        int x = vertexFormat.getVertexSize();
        int q = offset;
        ImmutableList<VertexFormatElement> list = vertexFormat.getElements();
        for (int z = 0; z < list.size(); ++z) {
            VertexFormatElement element = list.get(z);
            element.setupBufferState(z, q, x);
            q += element.getByteSize();
        }
    }

    private void _cleanVertexFormat(VertexFormat vertexFormat) {
        vertexFormat.clearBufferState();

        RenderSystem.glBindBuffer(GL15.GL_ARRAY_BUFFER, () -> 0);
        RenderSystem.glBindVertexArray(() -> 0);
    }


    private void _setupShader(ShaderInstance shader) {
        // setup shader uniforms and sampler.
        for (int i = 0; i < 8; ++i) {
            shader.setSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
        }
        if (shader.MODEL_VIEW_MATRIX != null) {
            shader.MODEL_VIEW_MATRIX.set(RenderSystem.getModelViewMatrix());
        }
        if (shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
        }
        if (shader.INVERSE_VIEW_ROTATION_MATRIX != null) {
            shader.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
        }
        if (shader.TEXTURE_MATRIX != null) {
            shader.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }
        if (shader.COLOR_MODULATOR != null) {
            shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }
        if (shader.FOG_START != null) {
            shader.FOG_START.set(RenderSystem.getShaderFogStart());
        }
        if (shader.FOG_END != null) {
            shader.FOG_END.set(RenderSystem.getShaderFogEnd());
        }
        if (shader.FOG_COLOR != null) {
            shader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }
        if (shader.FOG_SHAPE != null) {
            shader.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }
        if (shader.GAME_TIME != null) {
            shader.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }
        if (shader.SCREEN_SIZE != null) {
            Window window = Minecraft.getInstance().getWindow();
            shader.SCREEN_SIZE.set((float) window.getWidth(), (float) window.getHeight());
        }
        if (shader.LINE_WIDTH != null) {
            shader.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
        }
        RenderSystem.setupShaderLights(shader);
    }
}
