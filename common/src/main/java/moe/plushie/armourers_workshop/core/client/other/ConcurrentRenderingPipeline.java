package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.data.cache.ObjectPool;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ConcurrentRenderingPipeline {

    protected final ArrayList<Group> passGroups = new ArrayList<>();

    public void add(ConcurrentBufferCompiler.Group group, ConcurrentRenderingContext context) {
        var pass = Group.POOL.get();
        var poseStack = context.getPoseStack();
        var modelViewStack = context.getModelViewStack();
        var last = pass.poseStack.last();
        var lastPose = last.pose();
        var lastNormal = last.normal();
        lastPose.set(modelViewStack.last().pose());
        lastPose.multiply(poseStack.last().pose());
        lastNormal.set(modelViewStack.last().normal());
        lastNormal.multiply(poseStack.last().normal());
        // https://web.archive.org/web/20240125142900/http://www.songho.ca/opengl/gl_normaltransform.html
        last.setProperties(poseStack.last().properties());
        passGroups.add(pass.fill(group, context));
    }

    public void commit(Consumer<ShaderVertexObject> consumer) {
        for (var pass : passGroups) {
            pass.forEach(consumer);
        }
        passGroups.clear();
    }


    public static class Group extends ReferenceCounted {

        private static final ObjectPool<Group> POOL = ObjectPool.create(Group::new);

        private final OpenPoseStack poseStack = new OpenPoseStack();
        private final ArrayList<Pass> pendingQueue = new ArrayList<>();

        private int usedCount = 0;
        private int totalCount = 0;

        private ConcurrentBufferCompiler.Group compiledGroup;

        public void forEach(Consumer<ShaderVertexObject> consumer) {
            for (int i = 0; i < usedCount; ++i) {
                var pass = pendingQueue.get(i);
                consumer.accept(pass);
            }
        }

        public Group fill(ConcurrentBufferCompiler.Group group, ConcurrentRenderingContext context) {
            usedCount = 0;
            compiledGroup = group;
            for (var mergedTask : group.getPasses()) {
                // skip outline task, when not enable.
                if (!context.shouldRenderOutline() && mergedTask.isOutline) {
                    continue;
                }
                poll().fill(mergedTask, poseStack, context);
            }
            return this;
        }

        @Override
        protected void init() {
            if (compiledGroup != null) {
                compiledGroup.retain();
            }
        }

        @Override
        protected void dispose() {
            if (compiledGroup != null) {
                compiledGroup.release();
                compiledGroup = null;
            }
        }

        private Pass poll() {
            if (usedCount < totalCount) {
                return pendingQueue.get(usedCount++);
            }
            var pass = new Pass(this);
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

        float polygonOffset;

        OpenPoseStack poseStack;
        ConcurrentBufferCompiler.Pass compiledTask;

        private final Group group;

        public Pass(Group group) {
            this.group = group;
        }

        public Pass fill(ConcurrentBufferCompiler.Pass compiledTask, OpenPoseStack poseStack, ConcurrentRenderingContext context) {
            this.compiledTask = compiledTask;
            this.poseStack = poseStack;
            this.overlay = context.getOverlay();
            this.lightmap = context.getLightmap();
            this.outlineColor = context.getOutlineColor();
            this.polygonOffset = compiledTask.polygonOffset + context.getRenderPriority();
            this.retain();
            return this;
        }

        @Override
        public IRenderType getType() {
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
            return polygonOffset;
        }

        @Override
        public OpenPoseStack getPoseStack() {
            return poseStack;
        }

        @Override
        public IVertexFormat getFormat() {
            if (compiledTask.format != null) {
                return compiledTask.format;
            }
            return compiledTask.renderType.format();
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
            return outlineColor;
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

        public void retain() {
            group.retain();
        }

        @Override
        public void release() {
            group.release();
        }
    }
}
