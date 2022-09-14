package moe.plushie.armourers_workshop.core.client.other;

import com.apple.library.uikit.UIColor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.data.cache.SkinCache;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.Skin;
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

//        AbstractRenderSystem.SkinShader.SKIN_NORMAL_MAT__ = matrixStack.last().normal().copy();
//        AbstractRenderSystem.SkinShader.SKIN_NORMAL_MAT__.invert();
//        AbstractRenderSystem.SkinShader.SKIN_POSE_MAT__ = matrixStack.last().pose();
//        MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
//        PoseStack matrixStack1 = new PoseStack();
////        PoseStack matrixStack1 = matrixStack;
//        part.forEach((renderType, quads) -> {
//            VertexConsumer builder = buffers.getBuffer(renderType);
////            BufferBuilder builder = new BufferBuilder(quads.size() * 8 * renderType.format().getVertexSize());
////            builder.begin(renderType.mode(), renderType.format());
//            quads.forEach(quad -> quad.render(part, scheme, light, OverlayTexture.NO_OVERLAY, matrixStack1, builder));
////            for (Direction dir : Direction.values()) {
////                ExtendedFaceRenderer.render(0, 0, 0, dir, PaintColor.WHITE, 255, light, OverlayTexture.NO_OVERLAY, matrixStack1, builder);
////            }
////            builder.end();
////            BufferUploader.end(builder);
//
////            CachedTask cachedTask = new CachedTask(part, scheme, builder);
////            CompiledTask compiledTask = new CompiledTask(renderType, builder, part.getRenderPolygonOffset(), part.getType());
////            compiledTask.vertexCount = quads.size() * 4;
////            compiledTask.bufferBuilder = builder;
////            cachedTask.mergedTasks = new ArrayList<>();
////            cachedTask.mergedTasks.add(compiledTask);
////            cachedRenderPipeline.render(cachedTask, matrixStack, light, partialTicks, slotIndex);
//
//            PoseStack modelPoseStack = RenderSystem.getModelStack();
//            modelPoseStack.pushPose();
//            modelPoseStack.mulPoseMatrix(matrixStack.last().pose());
//            RenderSystem.applyModelViewMatrix();
//            buffers.endBatch();
//            modelPoseStack.popPose();
//            RenderSystem.applyModelViewMatrix();
//        });
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
                BufferBuilder builder = new BufferBuilder(quads.size() * 8 * renderType.format().getVertexSize());
                builder.begin(renderType.mode(), renderType.format());
                quads.forEach(quad -> quad.render(part, scheme, 0xf000f0, OverlayTexture.NO_OVERLAY, matrixStack1, builder));
                builder.end();
                CompiledTask compiledTask = new CompiledTask(renderType, builder, part.getRenderPolygonOffset(), part.getType());
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
            VertexFormat format = compiledTask.renderType.format();
            Pair<BufferBuilder.DrawState, ByteBuffer> pair = compiledTask.bufferBuilder.popNextBuffer();
            ByteBuffer byteBuffer = pair.getSecond();
            compiledTask.vertexBuffer = vertexBuffer;
            compiledTask.vertexCount = byteBuffer.remaining() / format.getVertexSize();
            compiledTask.vertexOffset = totalRenderedBytes;
            compiledTask.bufferBuilder = null;
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
        BufferBuilder bufferBuilder;
        SkinRenderObject vertexBuffer;

        CompiledTask(RenderType renderType, BufferBuilder bufferBuilder, float polygonOffset, ISkinPartType partType) {
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

        void render(CachedTask task, PoseStack matrixStack, int lightmap, float partialTicks, int slotIndex) {
            Matrix4f matrix = matrixStack.last().pose().copy();
            Matrix3f invNormalMatrix = matrixStack.last().normal().copy();
            invNormalMatrix.invert();
            task.mergedTasks.forEach(t -> tasks.add(new CompiledPass(t, matrix, invNormalMatrix, lightmap, partialTicks, slotIndex)));
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
        public Matrix4f getMatrix() {
            return matrix;
        }

        @Override
        public Matrix3f getInvNormalMatrix() {
            return invNormalMatrix;
        }

        @Override
        public int getLightmap() {
            return lightmap;
        }
    }
}
