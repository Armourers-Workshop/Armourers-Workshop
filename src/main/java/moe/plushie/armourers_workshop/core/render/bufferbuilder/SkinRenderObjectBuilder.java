package moe.plushie.armourers_workshop.core.render.bufferbuilder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.utils.Rectangle3f;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
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

    public void addPartData(BakedSkinPart part, ColorScheme scheme, int light, float partialTicks, int slotIndex, MatrixStack matrixStack, boolean shouldRender) {
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
    }

    public void addShapeData(Vector3f origin, MatrixStack matrixStack) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//        RenderUtils.drawBoundingBox(matrixStack, box, color, SkinRenderBuffer.getInstance());
        RenderUtils.drawPoint(matrixStack, origin, 16, buffer);
    }

    public void addShapeData(Rectangle3f box, Color color, MatrixStack matrixStack) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderUtils.drawBoundingBox(matrixStack, box, color, buffer);
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
        // because the skin top side brightness is too highly,
        // this leads to some color exposed for pure white,
        // so we need to reduce (30%) the reflection color of all faces.
        MatrixStack matrixStack1 = new MatrixStack();
        matrixStack1.last().normal().mul(Matrix3f.createScaleMatrix(0.7f, 0.7f, 0.7f));
        ArrayList<CompiledTask> buildingTasks = new ArrayList<>();
        for (CachedTask task : tasks) {
            BakedSkinPart part = task.part;
            ColorScheme scheme = task.scheme;
            ArrayList<CompiledTask> mergedTasks = new ArrayList<>();
            part.forEach((renderType, quads) -> {
                BufferBuilder builder = new BufferBuilder(quads.size() * 8 * renderType.format().getVertexSize());
                builder.begin(renderType.mode(), renderType.format());
                quads.forEach(quad -> quad.render(part, scheme, matrixStack1, builder));
                builder.end();
                CompiledTask compiledTask = new CompiledTask(renderType, builder, part.getRenderPolygonOffset(), part.getType());
                mergedTasks.add(compiledTask);
                buildingTasks.add(compiledTask);
            });
            task.mergedTasks = mergedTasks;
        }
        combineAndUpload(tasks, buildingTasks);
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

        void render(CachedTask task, MatrixStack matrixStack, int lightmap, float partialTicks, int slotIndex) {
            Matrix4f matrix = matrixStack.last().pose().copy();
            task.mergedTasks.forEach(t -> tasks.add(new CompiledPass(t, matrix, lightmap, partialTicks, slotIndex)));
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
        CompiledTask compiledTask;

        CompiledPass(CompiledTask compiledTask, Matrix4f matrix, int lightmap, float partialTicks, int slotIndex) {
            super();
            this.compiledTask = compiledTask;
            this.matrix = matrix;
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
        public int getLightmap() {
            return lightmap;
        }
    }
}
