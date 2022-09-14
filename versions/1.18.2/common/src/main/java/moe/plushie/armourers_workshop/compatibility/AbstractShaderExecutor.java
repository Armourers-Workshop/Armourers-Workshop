package moe.plushie.armourers_workshop.compatibility;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import moe.plushie.armourers_workshop.api.common.IRenderBufferObject;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class AbstractShaderExecutor {

    private static final AbstractShaderExecutor INSTANCE = new AbstractShaderExecutor();

    private int maxVertexCount = 0;

    public static AbstractShaderExecutor getInstance() {
        return INSTANCE;
    }

    public void setup() {
        BufferUploader.reset();
    }

    public void clean() {
    }

    public void setMaxVertexCount(int count) {
        maxVertexCount = count;
    }

    public void execute(IRenderBufferObject object, int vertexOffset, int vertexCount, RenderType renderType) {
        ShaderInstance shader = RenderSystem.getShader();
        if (shader == null) {
            return;
        }

        VertexFormat vertexFormat = renderType.format();
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

        shader.apply();
//            GL11.glDrawArrays(mode.asGLMode, 0, j);
        GL11.glDrawElements(renderType.mode().asGLMode, j, m, 0L);
        shader.clear();

        _cleanVertexFormat(vertexFormat);
    }

    private void _setupVertexFormat(VertexFormat vertexFormat, IRenderBufferObject object, int offset) {
        boolean bl;
        int i = vertexFormat.getOrCreateVertexArrayObject();
//        int j = vertexFormat.getOrCreateVertexBufferObject();
//        boolean bl2 = bl = vertexFormat != lastFormat;
//        if (bl) {
//            BufferUploader.reset();
//        }
//        if (i != lastVertexArrayObject) {
        GlStateManager._glBindVertexArray(i);
//            lastVertexArrayObject = i;
//        }
        object.bind();
//        if (j != lastVertexBufferObject) {
//            GlStateManager._glBindBuffer(34962, j);
//            lastVertexBufferObject = j;
//        }
//        if (bl) {
//            vertexFormat.setupBufferState();
//            lastFormat = vertexFormat;
//        }
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
        GlStateManager._glBindBuffer(34962, 0);
        GlStateManager._glBindVertexArray(0);
    }





////            if (renderType == SkinRenderType.SOLID_FACE || renderType == SkinRenderType.TRANSLUCENT_SOLID_FACE) {
////                lightBuffer = SkinLightBufferObject.getLightBuffer(getLightmap());
////                lightBuffer.ensureCapacity(maxVertexCount);
////                lightBuffer.bind();
////                lightBuffer.setupBufferState(lightBuffer.getFormat(), 0L);
////            }
////            com.mojang.blaze3d.systems.RenderSystem
//
//
////            Matrix4f projectMat = RenderSystem.getProjectionMatrix().copy();
////            projectMat.multiply(getMatrix());
//
////            PoseStack poseStack = RenderSystem.getModelStack();
////            poseStack.pushPose();
////            poseStack.last().pose().multiply(getMatrix());
////            poseStack.last().normal().mul(getNormalMatrix());
////
////            Matrix3f matrix3f = poseStack.last().normal().copy();
////            if (matrix3f.invert()) {
//////                RenderSystem.setInverseViewRotationMatrix(matrix3f);
////            }
////
////            RenderSystem.applyModelViewMatrix();
//
////            BufferUploader.end(getBuildBuffer());
//
//    //            Pair<DrawState, ByteBuffer> pair = getBuildBuffer().popNextBuffer();
////            BufferBuilder.DrawState drawState = pair.getFirst();
//    VertexFormat vertexFormat = renderType.format();//drawState.format();
//    VertexFormat.Mode mode = renderType.mode();
//    //            ByteBuffer byteBuffer = pair.getSecond();
////            int i = drawState.vertexCount();
////            int k = i * vertexFormat.getVertexSize();
////            int j = drawState.indexCount();
//    int j = getVertexCount() + getVertexCount() / 2;
//    int i = getVertexCount();
//
//    //            vertexBuffer.bind();
//    setupVertexFormat(vertexFormat, vertexBuffer, getVertexOffset());
//
//    int m;
//    int l = 0;
//            if (/*drawState.sequentialIndex()*/true) {
//        int q = maxVertexCount + maxVertexCount / 2;
//        RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(renderType.mode(), q);
//        l = autoStorageIndexBuffer.name();
//        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, l);
//        m = autoStorageIndexBuffer.type().asGLType;
////            } else {
////                int n = vertexFormat.getOrCreateIndexBufferObject();
////                    GlStateManager._glBindBuffer(34963, n);
////                byteBuffer.position(k);
////                byteBuffer.limit(k + j * indexType.bytes);
////                GlStateManager._glBufferData(34963, byteBuffer, 35048);
////                m = indexType.asGLType;
//    }
//
////
////            int vboid = vertexFormat.getOrCreateVertexBufferObject();
////            VertexFormat.IndexType indexType = drawState.indexType();
////            GlStateManager._glBindBuffer(34962, vboid);
////            byteBuffer.clear();
////            vertexFormat.setupBufferState();
//////            BufferUploader.updateVertexSetup(vertexFormat);
////            byteBuffer.position(0);
////            byteBuffer.limit(k);
////            GlStateManager._glBufferData(34962, byteBuffer, 35048);
//
//
////                        Matrix3f mq = getNormalMatrix().copy();
////            mq.invert();
////            AbstractRenderSystem.SkinShader.SKIN_NORMAL_MAT__ = mq;
//
//            RenderSystem.setShaderColor(1, 1, 1, 1);
//            RenderSystem.setShaderLight(getLightmap());
//            RenderSystem.setTextureMatrix(Matrix4f.createTranslateMatrix(0, TickUtils.getPaintTextureOffset() / 256.0f, 0));
//            RenderSystem.setInverseNormalMatrix(getInvNormalMatrix());
//
//    PoseStack modelViewStack = RenderSystem.getModelStack();
//            modelViewStack.pushPose();
//            modelViewStack.last().pose().multiply(getMatrix());
//            RenderSystem.applyModelViewMatrix();
//
//            shader.apply();
////            GL11.glDrawArrays(mode.asGLMode, 0, j);
//            GL11.glDrawElements(renderType.mode().asGLMode, j, m, 0L);
//            shader.clear();
//
//    cleanVertexFormat(vertexFormat);
//
//            modelViewStack.popPose();
//            RenderSystem.applyModelViewMatrix();
//
//
////            if (l != 0) {
////                GlStateManager._glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
////            }
//
////            vertexBuffer.bind();
////
//////            renderType.format().setupBufferState();
//////            renderType.format().setupBufferState(getVertexOffset());
//////            vertexBuffer.setupBufferState(renderType.format(), getVertexOffset());
////            int i = renderType.format().getVertexSize();
////            int q = getVertexOffset();
////            ImmutableList<VertexFormatElement> list = renderType.format().getElements();
////            for (int j = 0; j < list.size(); ++j) {
////                VertexFormatElement element = list.get(j);
////                element.setupBufferState(j, q, i);
////                q += element.getByteSize();
////            }
////
////            vertexBuffer.draw(getMatrix(), 7, getVertexCount());
//////            vertexBuffer.clearBufferState(renderType.format());
////
//////            renderType.format().clearBufferState();
////            for (int j = 0; j < list.size(); ++j) {
////                list.get(j).clearBufferState(j);
////            }
////
////            if (lightBuffer != null) {
////                lightBuffer.clearBufferState(lightBuffer.getFormat());
////            }
//
}
