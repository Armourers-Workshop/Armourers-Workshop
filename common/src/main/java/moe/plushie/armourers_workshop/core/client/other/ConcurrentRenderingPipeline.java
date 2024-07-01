package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.data.cache.AutoreleasePool;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConcurrentRenderingPipeline {

    protected final ArrayList<Group> passGroups = new ArrayList<>();

    public void add(List<ConcurrentBufferCompiler.Pass> passes, ConcurrentRenderingContext context) {
        var pass = Group.POOL.get();
        var poseStack = context.getPoseStack();
        var modelViewStack = context.getModelViewStack();
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
        passGroups.add(pass.fill(passes, context.getLightmap(), context.getOverlay(), context.getAnimationTicks(), context.getRenderPriority()));
    }

    public void commit(Consumer<ShaderVertexObject> consumer) {
        for (var pass : passGroups) {
            pass.forEach(consumer);
        }
        passGroups.clear();
    }


    public static class Group {

        private static final AutoreleasePool<Group> POOL = AutoreleasePool.create(Group::new);

        private final OpenPoseStack poseStack = new OpenPoseStack();
        private final ArrayList<Pass> pendingQueue = new ArrayList<>();

        private int usedCount = 0;
        private int totalCount = 0;

        public void forEach(Consumer<ShaderVertexObject> consumer) {
            for (int i = 0; i < usedCount; ++i) {
                var pass = pendingQueue.get(i);
                consumer.accept(pass);
            }
        }

        public Group fill(List<ConcurrentBufferCompiler.Pass> passes, int lightmap, int overlay, float animationTicks, float renderPriority) {
            usedCount = passes.size();
            for (int i = 0; i < usedCount; ++i) {
                var mergedTask = passes.get(i);
                if (i < totalCount) {
                    var pass = pendingQueue.get(i);
                    pass.fill(mergedTask, poseStack, lightmap, overlay, animationTicks, renderPriority);
                } else {
                    var pass = new Pass();
                    pass.fill(mergedTask, poseStack, lightmap, overlay, animationTicks, renderPriority);
                    pendingQueue.add(pass);
                    totalCount += 1;
                }
            }
            return this;
        }
    }

    public static class Pass implements ShaderVertexObject {

        int overlay;
        int lightmap;
        float animationTicks;

        float additionalPolygonOffset;

        OpenPoseStack poseStack;
        ConcurrentBufferCompiler.Pass compiledTask;

        public Pass fill(ConcurrentBufferCompiler.Pass compiledTask, OpenPoseStack poseStack, int lightmap, int overlay, float animationTicks, float renderPriority) {
            this.compiledTask = compiledTask;
            this.poseStack = poseStack;
            this.overlay = overlay;
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
        public int getOffset() {
            return compiledTask.vertexOffset;
        }

        @Override
        public int getTotal() {
            return compiledTask.vertexCount;
        }

        @Override
        public VertexArrayObject getArrayObject() {
            return compiledTask.arrayObject;
        }

        @Override
        public VertexIndexObject getIndexObject() {
            return compiledTask.indexObject;
        }

        @Override
        public VertexBufferObject getBufferObject() {
            return compiledTask.bufferObject;
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
        public int getOverlay() {
            return overlay;
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
