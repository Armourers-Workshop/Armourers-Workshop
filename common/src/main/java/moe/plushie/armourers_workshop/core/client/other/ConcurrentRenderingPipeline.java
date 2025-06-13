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

    private final ArrayList<Group> passGroups = new ArrayList<>();

    public void add(ConcurrentBufferCompiler.Group group, ConcurrentRenderingContext context) {
        var pass = Group.POOL.get();
        var poseStack = context.poseStack();
        var modelViewStack = context.modelViewStack();
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


    private static class Group extends ReferenceCounted {

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
            for (var mergedTask : group.passes()) {
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

    private static class Pass implements ShaderVertexObject {

        private int overlay;
        private int lightmap;
        private int outlineColor;

        private float polygonOffset;

        private OpenPoseStack poseStack;
        private ConcurrentBufferCompiler.Pass compiledTask;

        private final Group group;

        public Pass(Group group) {
            this.group = group;
        }

        public void fill(ConcurrentBufferCompiler.Pass compiledTask, OpenPoseStack poseStack, ConcurrentRenderingContext context) {
            this.compiledTask = compiledTask;
            this.poseStack = poseStack;
            this.overlay = context.overlay();
            this.lightmap = context.lightmap();
            this.outlineColor = context.outlineColor();
            this.polygonOffset = compiledTask.polygonOffset + context.renderPriority();
            this.retain();
        }

        @Override
        public IRenderType type() {
            return compiledTask.renderType;
        }

        @Override
        public int offset() {
            return compiledTask.vertexOffset;
        }

        @Override
        public int total() {
            return compiledTask.vertexCount;
        }

        @Override
        public VertexArrayObject arrayObject() {
            return compiledTask.arrayObject;
        }

        @Override
        public VertexIndexObject indexObject() {
            return compiledTask.indexObject;
        }

        @Override
        public VertexBufferObject bufferObject() {
            return compiledTask.bufferObject;
        }

        @Override
        public float polygonOffset() {
            return polygonOffset;
        }

        @Override
        public OpenPoseStack poseStack() {
            return poseStack;
        }

        @Override
        public IVertexFormat format() {
            if (compiledTask.format != null) {
                return compiledTask.format;
            }
            return compiledTask.renderType.format();
        }

        @Override
        public int overlay() {
            return overlay;
        }

        @Override
        public int lightmap() {
            return lightmap;
        }

        @Override
        public int outlineColor() {
            return outlineColor;
        }

        @Override
        public boolean isEmissive() {
            return compiledTask.isEmissive;
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
