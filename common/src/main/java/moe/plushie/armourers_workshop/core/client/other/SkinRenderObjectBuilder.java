package moe.plushie.armourers_workshop.core.client.other;

import com.apple.library.uikit.UIColor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferBuilder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.client.texture.TextureManager;
import moe.plushie.armourers_workshop.core.data.cache.ObjectPool;
import moe.plushie.armourers_workshop.core.data.cache.PrimaryKey;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class SkinRenderObjectBuilder implements SkinRenderBufferSource.ObjectBuilder {

    private static final ExecutorService workThread = ThreadUtils.newFixedThreadPool(1, "AW-SKIN-VB");
    private static final Cache<Object, CachedTask> cachingTasks = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .removalListener(CachedTask::release)
            .build();

    protected final BakedSkin skin;
    protected final CachedRenderPipeline cachedRenderPipeline = new CachedRenderPipeline();

    protected final ArrayList<CachedTask> pendingCacheTasks = new ArrayList<>();
    protected boolean isSent = false;

    public SkinRenderObjectBuilder(BakedSkin skin) {
        this.skin = skin;
    }

    public static void clearAllCache() {
        cachingTasks.invalidateAll();
        cachingTasks.cleanUp();
    }

    @Override
    public int addPart(BakedSkinPart part, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRender, SkinRenderContext context) {
        // debug the vbo render.
        if (ModDebugger.vbo) {
            return drawWithoutVBO(part, bakedSkin, scheme, context.getOverlay(), context);
        }
        var cachedTask = compile(part, bakedSkin, scheme, context.getOverlay());
        if (cachedTask != null) {
            // we need compile the skin part, but does not render now.
            if (!shouldRender || cachedTask.isEmpty()) {
                return 0;
            }
            return cachedRenderPipeline.draw(cachedTask, context);
        }
        return 0;
    }

    @Override
    public void addShape(Vector3f origin, SkinRenderContext context) {
        var buffers = AbstractBufferSource.buffer();
//        RenderUtils.drawBoundingBox(poseStack, box, color, SkinRenderBuffer.getInstance());
        ShapeTesselator.vector(origin, 16, context.pose(), buffers);
    }

    @Override
    public void addShape(OpenVoxelShape shape, UIColor color, SkinRenderContext context) {
        var buffers = AbstractBufferSource.buffer();
        ShapeTesselator.stroke(shape.bounds(), color, context.pose(), buffers);
    }

    @Override
    public void addShape(BakedArmature armature, SkinRenderContext context) {
        var buffers = AbstractBufferSource.buffer();
        var transforms = armature.getTransforms();
        var armature1 = armature.getArmature();
        for (var joint : armature1.allJoints()) {
            var shape = armature1.getShape(joint.getId());
            var transform = transforms[joint.getId()];
            if (ModDebugger.defaultArmature) {
                transform = armature1.getGlobalTransform(joint.getId());
            }
            if (shape != null && transform != null) {
                context.pushPose();
                transform.apply(context.pose());
//                ModDebugger.translate(context.pose().pose());
//			poseStack.translate(box.o.getX(), box.o.getY(), box.o.getZ());
                ShapeTesselator.stroke(shape, ColorUtils.getPaletteColor(joint.getId()), context.pose(), buffers);
                ShapeTesselator.vector(0, 0, 0, 4, 4, 4, context.pose(), buffers);
                context.popPose();
            }
        }
    }

    public void endBatch(SkinVertexBufferBuilder.Pipeline pipeline) {
        cachedRenderPipeline.commit(pipeline::add);
    }

    @Nullable
    private CachedTask compile(BakedSkinPart part, BakedSkin bakedSkin, ColorScheme scheme, int overlay) {
        var key = PrimaryKey.of(bakedSkin.getId(), part.getId(), part.requirements(scheme), overlay);
        var cachedTask = cachingTasks.getIfPresent(key);
        if (cachedTask != null) {
            key.release();
            if (cachedTask.isCompiled) {
                return cachedTask;
            }
            return null; // wait compile

        }
        cachedTask = new CachedTask(part, scheme, overlay);
        cachingTasks.put(key, cachedTask);
        pushCompileTask(cachedTask);
        return null; // wait compile
    }

    private synchronized void pushCompileTask(CachedTask cachedTask) {
        pendingCacheTasks.add(cachedTask);
        if (isSent) {
            return;
        }
        isSent = true;
        workThread.execute(this::doCompile);
    }

    private synchronized ArrayList<CachedTask> popCompileTasks() {
        var tasks = new ArrayList<>(pendingCacheTasks);
        pendingCacheTasks.clear();
        isSent = false;
        return tasks;
    }

    private void doCompile() {
        var cachedTasks = popCompileTasks();
        if (cachedTasks.isEmpty()) {
            return;
        }
//        long startTime = System.currentTimeMillis();
        var poseStack1 = new OpenPoseStack();
        var buildingTasks = new ArrayList<CompiledTask>();
        for (var cachedTask : cachedTasks) {
            var overlay = cachedTask.overlay;
            var part = cachedTask.part;
            var scheme = cachedTask.scheme;
            var usingTypes = new HashSet<RenderType>();
            var mergedTasks = new ArrayList<CompiledTask>();
            part.getQuads().forEach((renderType, quads) -> {
                var builder = new AbstractBufferBuilder(quads.size() * 8 * renderType.format().getVertexSize());
                builder.begin(renderType);
                quads.forEach((transform, faces) -> {
                    poseStack1.pushPose();
                    transform.apply(poseStack1);
                    faces.forEach(face -> face.render(part, scheme, 0xf000f0, overlay, poseStack1, builder));
                    poseStack1.popPose();
                });
                var renderedBuffer = builder.end();
                var compiledTask = new CompiledTask(renderType, renderedBuffer, part.getRenderPolygonOffset(), part.getType());
                usingTypes.add(renderType);
                mergedTasks.add(compiledTask);
                buildingTasks.add(compiledTask);
            });
            cachedTask.mergedTasks = mergedTasks;
            cachedTask.usingTypes = usingTypes;
        }
        combineAndUpload(cachedTasks, buildingTasks);
//        long totalTime = System.currentTimeMillis() - startTime;
//        ModLog.debug("compile cachedTasks {}, times: {}ms", cachedTasks.size(), totalTime);
    }

    private int drawWithoutVBO(BakedSkinPart part, BakedSkin bakedSkin, ColorScheme scheme, int overlay, SkinRenderContext context) {
        var buffers = context.getBuffers();
        var poseStack1 = context.pose();
        part.getQuads().forEach((renderType, quads) -> {
            var builder = buffers.getBuffer(renderType);
            quads.forEach((transform, faces) -> {
                poseStack1.pushPose();
                transform.apply(poseStack1);
                faces.forEach(face -> face.render(part, scheme, 0xf000f0, overlay, poseStack1, builder));
                poseStack1.popPose();
            });
        });
        return 1;
    }

    private int drawWithVBO(CachedTask task, SkinRenderContext context) {
        var pipeline1 = new CachedRenderPipeline();
        var pipeline2 = new SkinVertexBufferBuilder.Pipeline();
        pipeline1.draw(task, context);
        pipeline1.commit(pipeline2::add);
        pipeline2.end();
        return task.totalTask;
    }

    private void combineAndUpload(ArrayList<CachedTask> cachedTasks, ArrayList<CompiledTask> buildingTasks) {
        var totalRenderedBytes = 0;
        var vertexBuffer = new SkinRenderObject();
        var byteBuffers = new ArrayList<ByteBuffer>();

        for (var compiledTask : buildingTasks) {
            var renderedBuffer = compiledTask.bufferBuilder;
            var format = renderedBuffer.format();
            var byteBuffer = renderedBuffer.vertexBuffer().duplicate();
            compiledTask.vertexBuffer = vertexBuffer;
            compiledTask.vertexCount = renderedBuffer.vertexCount();
            compiledTask.vertexOffset = totalRenderedBytes;
            compiledTask.bufferBuilder = null;
            compiledTask.format = format;
            byteBuffers.add(byteBuffer);
            totalRenderedBytes += byteBuffer.remaining();
            renderedBuffer.release();
        }

        var mergedByteBuffer = ByteBuffer.allocateDirect(totalRenderedBytes);
        for (var byteBuffer : byteBuffers) {
            mergedByteBuffer.put(byteBuffer);
        }
        mergedByteBuffer.rewind();
        vertexBuffer.upload(mergedByteBuffer);

        RenderSystem.recordRenderCall(() -> {
            cachedTasks.forEach(it -> it.finish(vertexBuffer));
            vertexBuffer.release();
        });
    }

    static class CachedTask {

        int totalTask;
        boolean isCompiled = false;
        ArrayList<CompiledTask> mergedTasks;
        HashSet<RenderType> usingTypes;
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
            if (notification.getValue() instanceof CachedTask task) {
                task.release();
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

        void finish(SkinRenderObject renderObject) {
            if (mergedTasks == null) {
                return; // is released or not init.
            }
            isCompiled = true;
            totalTask = mergedTasks.size();
            usingTypes.forEach(TextureManager.getInstance()::open);
            setRenderObject(renderObject);
        }

        void release() {
            setRenderObject(null);
            usingTypes.forEach(TextureManager.getInstance()::close);
            usingTypes = null;
            mergedTasks = null;
            isCompiled = false;
        }

        boolean isEmpty() {
            return mergedTasks == null || mergedTasks.isEmpty();
        }
    }

    static class CompiledTask {

        final boolean isGrowing;
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
            this.isGrowing = SkinRenderType.isGrowing(renderType);
        }
    }

    static class CachedRenderPipeline {

        protected final ObjectPool<CompiledPassGroup> passGroupPool = new ObjectPool<>(CompiledPassGroup::new);
        protected final ArrayList<CompiledPassGroup> passGroups = new ArrayList<>();

        int draw(CachedTask task, SkinRenderContext context) {
            var pass = passGroupPool.get();
            var lightmap = context.getLightmap();
            var animationTicks = context.getAnimationTicks();
            var renderPriority = context.getReferenced().getRenderPriority();
            var poseStack = context.pose();
            var modelViewStack = RenderSystem.getExtendedModelViewStack();
            var finalPoseStack = pass.poseStack;
            var lastPose = finalPoseStack.last().pose();
            var lastNormal = finalPoseStack.last().normal();
            // https://web.archive.org/web/20240125142900/http://www.songho.ca/opengl/gl_normaltransform.html
            //finalPoseStack.last().setProperties(poseStack.last().properties());
            lastPose.set(modelViewStack.last().pose());
            lastPose.multiply(poseStack.last().pose());
            //lastNormal.set(modelViewStack.last().normal());
            lastNormal.set(poseStack.last().normal());
            lastNormal.invert();
            passGroups.add(pass.fill(task, lightmap, animationTicks, renderPriority));
            return task.totalTask;
        }

        void commit(Consumer<ShaderVertexObject> consumer) {
            for (var pass : passGroups) {
                pass.forEach(consumer);
                passGroupPool.add(pass);
            }
            passGroups.clear();
        }
    }

    static class CompiledPassGroup {

        private final OpenPoseStack poseStack = new OpenPoseStack();
        private final ArrayList<CompiledPass> pendingQueue = new ArrayList<>();

        private int usedCount = 0;
        private int totalCount = 0;

        public void forEach(Consumer<ShaderVertexObject> consumer) {
            for (int i = 0; i < usedCount; ++i) {
                var pass = pendingQueue.get(i);
                consumer.accept(pass);
            }
        }

        public CompiledPassGroup fill(CachedTask task, int lightmap, float animationTicks, float renderPriority) {
            usedCount = task.mergedTasks.size();
            for (int i = 0; i < usedCount; ++i) {
                var mergedTask = task.mergedTasks.get(i);
                if (i < totalCount) {
                    var pass = pendingQueue.get(i);
                    pass.fill(mergedTask, poseStack, lightmap, animationTicks, renderPriority);
                } else {
                    var pass = new CompiledPass();
                    pass.fill(mergedTask, poseStack, lightmap, animationTicks, renderPriority);
                    pendingQueue.add(pass);
                    totalCount += 1;
                }
            }
            return this;
        }
    }

    static class CompiledPass implements ShaderVertexObject {

        int lightmap;
        float animationTicks;

        float additionalPolygonOffset;

        OpenPoseStack poseStack;
        CompiledTask compiledTask;

        public CompiledPass fill(CompiledTask compiledTask, OpenPoseStack poseStack, int lightmap, float animationTicks, float renderPriority) {
            this.compiledTask = compiledTask;
            this.poseStack = poseStack;
            this.lightmap = lightmap;
            this.animationTicks = animationTicks;
            this.additionalPolygonOffset = renderPriority;
            return this;
        }

        @Override
        public void release() {
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

        @Override
        public boolean isGrowing() {
            return compiledTask.isGrowing;
        }
    }
}
