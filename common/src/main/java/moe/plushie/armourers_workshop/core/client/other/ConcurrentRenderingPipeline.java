package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.data.cache.ObjectPool;
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
        passGroups.add(pass.fill(passes, context));
    }

    public void commit(Consumer<ShaderVertexObject> consumer) {
        for (var pass : passGroups) {
            pass.forEach(consumer);
        }
        passGroups.clear();
    }


    public static class Group {

        private static final ObjectPool<Group> POOL = ObjectPool.create(Group::new);

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

        public Group fill(List<ConcurrentBufferCompiler.Pass> passes, ConcurrentRenderingContext context) {
            usedCount = 0;
            for (var mergedTask : passes) {
                // skip outline task, when not enable.
                if (mergedTask.isOutline && !context.shouldRenderOutline()) {
                    continue;
                }
                poll().fill(mergedTask, poseStack, context);
            }
            return this;
        }

        private Pass poll() {
            if (usedCount < totalCount) {
                return pendingQueue.get(usedCount++);
            }
            var pass = new Pass();
            pendingQueue.add(pass);
            totalCount += 1;
            usedCount += 1;
            return pass;
        }
    }

    public static class Pass implements ShaderVertexObject {

        int overlay;
        int lightmap;
        int outlineColor;

        float animationTicks;

        float polygonOffset;

        OpenPoseStack poseStack;
        RenderType renderType;
        ConcurrentBufferCompiler.Pass compiledTask;

        public Pass fill(ConcurrentBufferCompiler.Pass compiledTask, OpenPoseStack poseStack, ConcurrentRenderingContext context) {
            this.compiledTask = compiledTask;
            this.poseStack = poseStack;
            this.overlay = context.getOverlay();
            this.lightmap = context.getLightmap();
            this.outlineColor = context.getOutlineColor();
            this.animationTicks = context.getAnimationTicks();
            this.polygonOffset = compiledTask.polygonOffset + context.getRenderPriority();
            this.renderType = compiledTask.renderType;
            return this;
        }

        @Override
        public void release() {
        }

        @Override
        public RenderType getType() {
            return renderType;
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
            return polygonOffset;
        }

        @Override
        public OpenPoseStack getPoseStack() {
            return poseStack;
        }

        @Override
        public VertexFormat getFormat() {
            if (compiledTask.format != null) {
                return compiledTask.format;
            }
            return renderType.format();
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
        public int getOutlineColor() {
            if (compiledTask.isOutline) {
                return outlineColor;
            }
            return 0;
        }

        @Override
        public boolean isGrowing() {
            return compiledTask.isGrowing;
        }

        @Override
        public boolean isTranslucent() {
            return compiledTask.isTranslucent;
        }

        @Override
        public boolean isOutline() {
            return compiledTask.isOutline;
        }
    }
}
