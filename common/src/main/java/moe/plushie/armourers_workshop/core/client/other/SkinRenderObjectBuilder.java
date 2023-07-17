package moe.plushie.armourers_workshop.core.client.other;

import com.apple.library.uikit.UIColor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
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
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinRenderObjectBuilder implements SkinRenderBufferSource.ObjectBuilder {

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

    @Override
    public int addPart(BakedSkinPart part, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRender, SkinRenderContext context) {
        CachedTask cachedTask = compile(part, bakedSkin, scheme, context.getOverlay());
        if (cachedTask != null) {
            // we need compile the skin part, but does not render now.
            if (!shouldRender) {
                return 0;
            }
            return cachedRenderPipeline.draw(cachedTask, context);
        }
        return 0;
    }

    @Override
    public void addShape(Vector3f origin, SkinRenderContext context) {
        auto buffers = Minecraft.getInstance().renderBuffers().bufferSource();
//        RenderUtils.drawBoundingBox(poseStack, box, color, SkinRenderBuffer.getInstance());
        RenderSystem.drawPoint(context.pose().pose(), origin, 16, buffers);
    }

    @Override
    public void addShape(OpenVoxelShape shape, UIColor color, SkinRenderContext context) {
        auto buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.drawBoundingBox(context.pose().pose(), shape.bounds(), color, buffers);
    }

    @Override
    public void addShape(ITransformf[] transforms, SkinRenderContext context) {
        if (transforms == null) {
            return;
        }
        auto buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        ModelBinder.BIPPED_BOXES.forEach((joint, rect) -> {
            ITransformf transform = transforms[joint.getId()];
            if (transform == null) {
                return;
            }
            context.pushPose();

            transform.apply(context.pose());

//			poseStack.translate(box.o.getX(), box.o.getY(), box.o.getZ());
            RenderSystem.drawBoundingBox(context.pose().pose(), rect, ColorUtils.getPaletteColor(joint.getId()), buffers);
            RenderSystem.drawPoint(context.pose().pose(), Vector3f.ZERO, 4, 4, 4, buffers);
            context.popPose();
        });
    }

    public void endBatch(SkinVertexBufferBuilder.Pipeline pipeline) {
        cachedRenderPipeline.commit(pipeline::add);
    }

    @Nullable
    public CachedTask compile(BakedSkinPart part, BakedSkin bakedSkin, ColorScheme scheme, int overlay) {
        Object key = SkinCache.borrowKey(bakedSkin.getId(), part.getId(), part.requirements(scheme), overlay);
        auto cachedTask = cachingTasks.getIfPresent(key);
        if (cachedTask != null) {
            SkinCache.returnKey(key);
            if (cachedTask.isCompiled) {
                return cachedTask;
            }
            return null; // wait compile

        }
        cachedTask = new CachedTask(part, scheme, overlay);
        cachingTasks.put(key, cachedTask);
        addCompileTask(cachedTask);
        return null; // wait compile
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
        PoseStack matrixStack1 = new PoseStack();
        ArrayList<CompiledTask> buildingTasks = new ArrayList<>();
        for (CachedTask task : tasks) {
            int overlay = task.overlay;
            BakedSkinPart part = task.part;
            ColorScheme scheme = task.scheme;
            ArrayList<CompiledTask> mergedTasks = new ArrayList<>();
            part.forEach((renderType, quads) -> {
                auto builder = BufferBuilder.createBuilderBuffer(quads.size() * 8 * renderType.format().getVertexSize());
                builder.begin(renderType);
                quads.forEach(quad -> quad.render(part, scheme, 0xf000f0, overlay, matrixStack1, builder.asBufferBuilder()));
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
            auto drawState = compiledTask.bufferBuilder.drawState();
            auto format = drawState.format();
            auto byteBuffer = compiledTask.bufferBuilder.vertexBuffer();
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

        int totalTask;
        boolean isCompiled = false;
        ArrayList<CompiledTask> mergedTasks;
        BakedSkinPart part;
        ColorScheme scheme;
        int overlay;
        SkinRenderObject renderObject;

        CachedTask(BakedSkinPart part, ColorScheme scheme, int overlay) {
            this.part = part;
            this.scheme = scheme.copy();
            this.overlay = overlay;
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
            totalTask = mergedTasks.size();
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

        int draw(CachedTask task, SkinRenderContext context) {
            int lightmap = context.getLightmap();
            float partialTicks = context.getPartialTicks();
            int slotIndex = context.getReferenceSlot();
            IPoseStack poseStack = context.pose();
            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            OpenPoseStack finalPostStack = new OpenPoseStack();
            OpenMatrix4f lastPose = finalPostStack.lastPose();
            OpenMatrix3f lastNormal = finalPostStack.lastNormal();
            lastPose.multiply(modelViewStack.lastPose());
            lastPose.multiply(poseStack.lastPose());
//            lastNormal.multiply(modelViewStack.lastNormal());
            lastNormal.multiply(poseStack.lastNormal());
            lastNormal.invert();
            task.mergedTasks.forEach(t -> tasks.add(new CompiledPass(t, finalPostStack, lightmap, partialTicks, slotIndex)));
            return task.totalTask;
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

        OpenPoseStack poseStack;
        CompiledTask compiledTask;

        CompiledPass(CompiledTask compiledTask, OpenPoseStack poseStack, int lightmap, float partialTicks, int slotIndex) {
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
        public OpenPoseStack getPoseStack() {
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
