package moe.plushie.armourers_workshop.core.client.other;

import com.apple.library.uikit.UIColor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.armature.ModelBinder;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.data.cache.SkinCache;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.platform.ClientNativeManager;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Environment(value = EnvType.CLIENT)
public class SkinRenderObjectBuilder {

    private static final ExecutorService workThread = ThreadUtils.newFixedThreadPool(1, "AW-SKIN-VB");
    private static final Cache<Object, CachedTask> cachingTasks = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.SECONDS)
            .removalListener(CachedTask::release)
            .build();

    protected final Skin skin;

    protected final CachedRenderPipeline cachedRenderPipeline = new CachedRenderPipeline();

    protected final ArrayList<CachedTask> pendingCacheTasks = new ArrayList<>();
    protected boolean isSent = false;

    public SkinRenderObjectBuilder(Skin skin) {
        this.skin = skin;
    }

    public static void clearAllCache() {
        cachingTasks.invalidateAll();
        cachingTasks.cleanUp();
    }

    public void addPartData(BakedSkinPart part, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRender, SkinRenderContext context) {
        Object key = SkinCache.borrowKey(bakedSkin.getId(), part.getId(), part.requirements(scheme));
        CachedTask cachedTask = cachingTasks.getIfPresent(key);
        if (cachedTask != null) {
            SkinCache.returnKey(key);
            if (shouldRender && cachedTask.isCompiled) {
                cachedRenderPipeline.render(cachedTask, context.poseStack, context.light, context.partialTicks, context.slotIndex);
                //cachedTask.mergedTasks.forEach(compiledTask -> pendingTasks.add(new CompiledPass(compiledTask, poseStack, light, partialTicks)));
            }
            return;
        }
        CachedTask task = new CachedTask(part, scheme);
        cachingTasks.put(key, task);
        addCompileTask(task);
//        _addPartData(part, scheme, light, partialTicks, slotIndex, poseStack, shouldRender);
    }

    private void _addPartData(BakedSkinPart part, ColorScheme scheme, int light, float partialTicks, int slotIndex, IPoseStack poseStack, boolean shouldRender) {
//        RenderSystem.backupExtendedMatrix();
//
//        RenderSystem.backupExtendedMatrix();
//        RenderSystem.getModelViewStack().pushPose();
//        RenderSystem.getModelViewStack().last().pose().setIdentity();
//        RenderSystem.applyModelViewMatrix();
//
////        Matrix3f normalMatrix = poseStack.last().normal().copy();
////        normalMatrix.invert();
//
//        RenderSystem.setShaderColor(1, 1, 1, 1);
////            RenderSystem.setShaderLight(light);
////        RenderSystem.setExtendedNormalMatrix(normalMatrix);
//        RenderSystem.setExtendedTextureMatrix(OpenMatrix4f.createTranslateMatrix(0, TickUtils.getPaintTextureOffset() / 256.0f, 0));
//
//        AbstractShaderExecutor.getInstance().setup();
//        MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
////        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
////        IPoseStack matrixStack1 = new PoseStack();
//        IPoseStack matrixStack1 = poseStack;
//        part.forEach((renderType, quads) -> {
//            VertexConsumer builder = buffers.getBuffer(renderType);
////            BufferBuilder builder = new BufferBuilder(quads.size() * 8 * renderType.format().getVertexSize());
////            builder.begin(renderType.mode(), renderType.format());
//            quads.forEach(quad -> quad.render(part, scheme, 0xf000f0, OverlayTexture.NO_OVERLAY, matrixStack1, builder));
//
//
////            IPoseStack modelPoseStack = RenderSystem.getExtendedModelViewStack();
////            modelPoseStack.pushPose();
////            modelPoseStack.multiply(poseStack.lastPose());
//            RenderSystem.setExtendedModelViewMatrix(matrixStack1.lastPose());
////            RenderSystem.applyModelViewMatrix();
//            buffers.endBatch();
////            modelPoseStack.popPose();
//        });
//        AbstractShaderExecutor.getInstance().clean();
//
//        RenderSystem.getModelViewStack().popPose();
//        RenderSystem.applyModelViewMatrix();
//        RenderSystem.restoreExtendedMatrix();
    }

    public void addShapePoint(Vector3f origin, SkinRenderContext context) {
        MultiBufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
//        RenderUtils.drawBoundingBox(poseStack, box, color, SkinRenderBuffer.getInstance());
        RenderSystem.drawPoint(context.poseStack, origin, 16, buffers);
    }

    public void addShapeBox(Rectangle3f box, UIColor color, SkinRenderContext context) {
        MultiBufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.drawBoundingBox(context.poseStack, box, color, buffers);
    }

    public void addArmatureBox(ITransformf[] transforms, SkinRenderContext context) {
        if (transforms == null) {
            return;
        }
        IPoseStack poseStack = context.poseStack;
        MultiBufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        ModelBinder.BIPPED_BOXES.forEach((joint, rect) -> {
            ITransformf transform = transforms[joint.getId()];
            if (transform == null) {
                return;
            }
            poseStack.pushPose();

            transform.apply(poseStack);

//			poseStack.translate(box.o.getX(), box.o.getY(), box.o.getZ());
            RenderSystem.drawBoundingBox(poseStack, rect, ColorUtils.getPaletteColor(joint.getId()), buffers);
            RenderSystem.drawPoint(poseStack, Vector3f.ZERO, 4, 4, 4, buffers);
            poseStack.popPose();
        });
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
        workThread.execute(this::doCompile);
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
        IPoseStack matrixStack1 = MatrixUtils.stack();
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
        RenderSystem.recordRenderCall(() -> {
            for (CachedTask cachedTask : qt) {
                cachedTask.setRenderObject(vertexBuffer);
                cachedTask.finish();
            }
            vertexBuffer.release();
        });
    }

    static class CachedTask {

        boolean isCompiled = false;
        ArrayList<CompiledTask> mergedTasks;
        BakedSkinPart part;
        ColorScheme scheme;
        SkinRenderObject renderObject;

        CachedTask(BakedSkinPart part, ColorScheme scheme) {
            this.part = part;
            this.scheme = scheme.copy();
        }

        static void release(RemovalNotification<Object, Object> notification) {
            CachedTask task = ObjectUtils.safeCast(notification.getValue(), CachedTask.class);
            if (task != null) {
                task.setRenderObject(null);
            }
        }

        void setRenderObject(SkinRenderObject renderObject) {
            if (this.renderObject != null) {
                this.renderObject.release();
            }
            this.renderObject = renderObject;
            if (this.renderObject != null) {
                this.renderObject.retain();
            }
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
    }

    static class CachedRenderPipeline {

        protected final ArrayList<CompiledPass> tasks = new ArrayList<>();

        void render(CachedTask task, IPoseStack poseStack, int lightmap, float partialTicks, int slotIndex) {
            IPoseStack modelViewStack = RenderSystem.getExtendedModelViewStack();
            IPoseStack fixedPostStack = new OpenPoseStack();
            IMatrix4f lastPose = fixedPostStack.lastPose();
            lastPose.multiply(modelViewStack.lastPose());
            lastPose.multiply(poseStack.lastPose());
            IMatrix3f lastNormal = fixedPostStack.lastNormal();
//            lastNormal.multiply(modelViewStack.lastNormal());
            lastNormal.multiply(poseStack.lastNormal());
            lastNormal.invert();
            task.mergedTasks.forEach(t -> tasks.add(new CompiledPass(t, fixedPostStack, lightmap, partialTicks, slotIndex)));
        }

        void commit(Consumer<ShaderVertexObject> consumer) {
            if (tasks.size() != 0) {
                tasks.forEach(consumer);
                tasks.clear();
            }
        }
    }

    static class CompiledPass extends ShaderVertexObject {

        int lightmap;
        float partialTicks;

        float additionalPolygonOffset;

        IPoseStack poseStack;
        CompiledTask compiledTask;

        CompiledPass(CompiledTask compiledTask, IPoseStack poseStack, int lightmap, float partialTicks, int slotIndex) {
            super();
            this.compiledTask = compiledTask;
            this.poseStack = poseStack;
            this.lightmap = lightmap;
            this.partialTicks = partialTicks;
            this.additionalPolygonOffset = slotIndex * 10;
        }

        @Override
        public RenderType getType() {
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
        public IPoseStack getPoseStack() {
            return poseStack;
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
