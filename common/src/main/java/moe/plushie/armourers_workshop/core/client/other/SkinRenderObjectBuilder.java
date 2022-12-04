package moe.plushie.armourers_workshop.core.client.other;

import com.apple.library.uikit.UIColor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.AbstractRenderPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.data.cache.SkinCache;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.platform.ClientNativeManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Environment(value = EnvType.CLIENT)
public class SkinRenderObjectBuilder {

    private static final ExecutorService executor = Executors.newFixedThreadPool(1, r -> new Thread(r, "AW-SKIN-VB"));

    protected final Skin skin;
    protected final Cache<Object, CachedTask> cachingTasks = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.SECONDS)
            .build();

    protected final CachedRenderPipeline cachedRenderPipeline = new CachedRenderPipeline();

    protected final ArrayList<CachedTask> pendingCacheTasks = new ArrayList<>();
    protected boolean isSent = false;

    public SkinRenderObjectBuilder(Skin skin) {
        this.skin = skin;
    }

    public void addPartData(BakedSkinPart part, ColorScheme scheme, int light, float partialTicks, int slotIndex, PoseStack matrixStack, boolean shouldRender) {
        Object key = SkinCache.borrowKey(part.getId(), part.requirements(scheme));
        CachedTask cachedTask = cachingTasks.getIfPresent(key);
        if (cachedTask != null) {
            SkinCache.returnKey(key);
            if (shouldRender && cachedTask.isCompiled) {
                cachedRenderPipeline.render(cachedTask, matrixStack, light, partialTicks, slotIndex);
                //cachedTask.mergedTasks.forEach(compiledTask -> pendingTasks.add(new CompiledPass(compiledTask, matrixStack, light, partialTicks)));
            }
            return;
        }
        CachedTask task = new CachedTask(part, scheme);
        cachingTasks.put(key, task);
        addCompileTask(task);

//        RenderSystem.backupExtendedMatrix();
//
//        Matrix3f normalMatrix = matrixStack.last().normal().copy();
//        normalMatrix.invert();
//
//        RenderSystem.setShaderColor(1, 1, 1, 1);
////            RenderSystem.setShaderLight(light);
//        RenderSystem.setExtendedNormalMatrix(normalMatrix);
//        RenderSystem.setExtendedTextureMatrix(Matrix4f.createTranslateMatrix(0, TickUtils.getPaintTextureOffset() / 256.0f, 0));
//
//        AbstractShaderExecutor.getInstance().setup();
//        MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
////        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
//        PoseStack matrixStack1 = new PoseStack();
////        PoseStack matrixStack1 = matrixStack;
//        part.forEach((renderType, quads) -> {
//            VertexConsumer builder = buffers.getBuffer(renderType);
////            BufferBuilder builder = new BufferBuilder(quads.size() * 8 * renderType.format().getVertexSize());
////            builder.begin(renderType.mode(), renderType.format());
//            quads.forEach(quad -> quad.render(part, scheme, 0xf000f0, OverlayTexture.NO_OVERLAY, matrixStack1, builder));
//
//
//            AbstractRenderPoseStack modelPoseStack = RenderSystem.getModelStack();
//            modelPoseStack.pushPose();
//            modelPoseStack.mulPose(matrixStack.last().pose());
//            modelPoseStack.apply();
//            buffers.endBatch();
//            modelPoseStack.popPose();
//            modelPoseStack.apply();
//        });
//        AbstractShaderExecutor.getInstance().clean();
//
//        RenderSystem.restoreExtendedMatrix();
    }

    public void addShapeData(Vector3f origin, PoseStack matrixStack) {
        MultiBufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
//        RenderUtils.drawBoundingBox(matrixStack, box, color, SkinRenderBuffer.getInstance());
        RenderSystem.drawPoint(matrixStack, origin, 16, buffers);
    }

    public void addShapeData(Rectangle3f box, UIColor color, PoseStack matrixStack) {
        MultiBufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.drawBoundingBox(matrixStack, box, color, buffers);
    }

    public void endBatch(SkinVertexBufferBuilder.Pipeline pipeline) {
        cachedRenderPipeline.commit(pipeline::add);
    }

    private synchronized void addCompileTask(CachedTask cachedTask) {
        pendingCacheTasks.add(cachedTask);
        if (isSent) {
            return;
        }
        isSent = true;
        executor.execute(this::doCompile);
    }

    private void doCompile() {
        ArrayList<CachedTask> tasks;
        synchronized (this) {
            tasks = new ArrayList<>(pendingCacheTasks);
            pendingCacheTasks.clear();
            isSent = false;
        }
        if (tasks.isEmpty()) {
            return;
        }
//        long startTime = System.currentTimeMillis();
        PoseStack matrixStack1 = new PoseStack();
        ArrayList<CompiledTask> buildingTasks = new ArrayList<>();
        for (CachedTask task : tasks) {
            BakedSkinPart part = task.part;
            ColorScheme scheme = task.scheme;
            ArrayList<CompiledTask> mergedTasks = new ArrayList<>();
            part.forEach((renderType, quads) -> {
                IBufferBuilder builder = ClientNativeManager.createBuilderBuffer(quads.size() * 8 * renderType.format().getVertexSize());
                builder.begin(renderType);
                quads.forEach(quad -> quad.render(part, scheme, 0xf000f0, OverlayTexture.NO_OVERLAY, matrixStack1, builder.asBufferBuilder()));
                IRenderedBuffer renderedBuffer = builder.end();
                CompiledTask compiledTask = new CompiledTask(renderType, renderedBuffer, part.getRenderPolygonOffset(), part.getType());
                mergedTasks.add(compiledTask);
                buildingTasks.add(compiledTask);
            });
            task.mergedTasks = mergedTasks;
        }
        combineAndUpload(tasks, buildingTasks);
//        long totalTime = System.currentTimeMillis() - startTime;
//        ModLog.debug("compile tasks {}, times: {}ms", tasks.size(), totalTime);
    }

    private void combineAndUpload(ArrayList<CachedTask> qt, ArrayList<CompiledTask> buildingTasks) {
        int totalRenderedBytes = 0;
        SkinRenderObject vertexBuffer = new SkinRenderObject();
        ArrayList<ByteBuffer> byteBuffers = new ArrayList<>();

        for (CompiledTask compiledTask : buildingTasks) {
            BufferBuilder.DrawState drawState = compiledTask.bufferBuilder.drawState();
            VertexFormat format = drawState.format();//compiledTask.renderType.format();
            ByteBuffer byteBuffer = compiledTask.bufferBuilder.vertexBuffer();
            compiledTask.vertexBuffer = vertexBuffer;
            compiledTask.vertexCount = drawState.vertexCount();
            compiledTask.vertexOffset = totalRenderedBytes;
            compiledTask.bufferBuilder.release();
            compiledTask.bufferBuilder = null;
            compiledTask.format = format;
            byteBuffers.add(byteBuffer);
            totalRenderedBytes += byteBuffer.remaining();
        }

        ByteBuffer mergedByteBuffer = ByteBuffer.allocateDirect(totalRenderedBytes);
        for (ByteBuffer byteBuffer : byteBuffers) {
            mergedByteBuffer.put(byteBuffer);
        }
        mergedByteBuffer.rewind();
        vertexBuffer.upload(mergedByteBuffer);
        RenderSystem.recordRenderCall(() -> qt.forEach(CachedTask::finish));
    }

    static class CachedTask {

        boolean isCompiled = false;
        ArrayList<CompiledTask> mergedTasks;
        BakedSkinPart part;
        ColorScheme scheme;

        CachedTask(BakedSkinPart part, ColorScheme scheme) {
            this.part = part;
            this.scheme = scheme.copy();
        }

        void finish() {
            isCompiled = true;
        }
    }

    static class CompiledTask {

        final float polygonOffset;
        final ISkinPartType partType;
        final RenderType renderType;
        int vertexCount;
        int vertexOffset;
        IRenderedBuffer bufferBuilder;
        SkinRenderObject vertexBuffer;
        VertexFormat format;

        CompiledTask(RenderType renderType, IRenderedBuffer bufferBuilder, float polygonOffset, ISkinPartType partType) {
            this.partType = partType;
            this.renderType = renderType;
            this.bufferBuilder = bufferBuilder;
            this.polygonOffset = polygonOffset;
        }

        void close() {
            vertexBuffer.close();
        }
    }

    static class CachedRenderPipeline {

        protected final ArrayList<CompiledPass> tasks = new ArrayList<>();

        void render(CachedTask task, PoseStack poseStack, int lightmap, float partialTicks, int slotIndex) {
            AbstractRenderPoseStack modelViewStack = RenderSystem.getExtendedModelViewStack();
            Matrix4f modelViewMatrix = modelViewStack.lastPose().copy();
            modelViewMatrix.multiply(poseStack.last().pose());
//            // transpose(inverse(modelViewMatrix))
            Matrix3f normalMatrix = poseStack.last().normal().copy();
            normalMatrix.invert();
            normalMatrix.mul(modelViewStack.lastNormal());
            task.mergedTasks.forEach(t -> tasks.add(new CompiledPass(t, modelViewMatrix, normalMatrix, lightmap, partialTicks, slotIndex)));
        }

        void commit(Consumer<SkinVertexBufferBuilder.Pass> consumer) {
            if (tasks.size() != 0) {
                tasks.forEach(consumer);
                tasks.clear();
            }
        }
    }

    static class CompiledPass extends SkinVertexBufferBuilder.Pass {

        int lightmap;
        float partialTicks;

        float additionalPolygonOffset;

        Matrix4f matrix;
        Matrix3f invNormalMatrix;
        CompiledTask compiledTask;

        CompiledPass(CompiledTask compiledTask, Matrix4f matrix, Matrix3f invNormalMatrix, int lightmap, float partialTicks, int slotIndex) {
            super();
            this.compiledTask = compiledTask;
            this.matrix = matrix;
            this.invNormalMatrix = invNormalMatrix;
            this.lightmap = lightmap;
            this.partialTicks = partialTicks;
            this.additionalPolygonOffset = slotIndex * 10;
        }

        @Override
        public ISkinPartType getPartType() {
            return compiledTask.partType;
        }

        @Override
        public RenderType getRenderType() {
            return compiledTask.renderType;
        }

        @Override
        public int getVertexOffset() {
            return compiledTask.vertexOffset;
        }

        @Override
        public int getVertexCount() {
            return compiledTask.vertexCount;
        }

        @Override
        public SkinRenderObject getVertexBuffer() {
            return compiledTask.vertexBuffer;
        }

        @Override
        public float getPolygonOffset() {
            return compiledTask.polygonOffset + additionalPolygonOffset;
        }

        @Override
        public Matrix4f getModelViewMatrix() {
            return matrix;
        }

        @Override
        public Matrix3f getInvNormalMatrix() {
            return invNormalMatrix;
        }

        @Override
        public VertexFormat getFormat() {
            if (compiledTask.format == null) {
                return compiledTask.renderType.format();
            }
            return compiledTask.format;
        }

        @Override
        public int getLightmap() {
            return lightmap;
        }
    }
}
