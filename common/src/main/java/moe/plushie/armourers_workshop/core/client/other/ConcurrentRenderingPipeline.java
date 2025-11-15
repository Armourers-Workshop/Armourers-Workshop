package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.compat.client.AbstractShader;
import moe.plushie.armourers_workshop.core.client.shader.Shader;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexMerger;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;

public class ConcurrentRenderingPipeline {

    private final Pipeline solidPipeline = new Pipeline();
    private final Pipeline outlinePipeline = new Pipeline();
    private final Pipeline translucentPipeline = new Pipeline();

    public void clear() {
        solidPipeline.clear();
        outlinePipeline.clear();
        translucentPipeline.clear();
    }

    public void submit(ConcurrentBufferCompiler.Pass compiledTask, int lightmap, int overlay, int outlineColor, float renderPriority, OpenPoseStack.Pose pose) {
        var pass = Pass.newInstance(compiledTask, lightmap, overlay, outlineColor, renderPriority, pose);
        if (pass.isOutline()) {
            outlinePipeline.add(pass);
        } else if (pass.isTranslucent()) {
            translucentPipeline.add(pass);
        } else {
            solidPipeline.add(pass);
        }
    }

    public void endSolidBatch() {
        solidPipeline.end();
    }

    public void endOutlineBatch() {
        outlinePipeline.end();
    }

    public void endTranslucentBatch() {
        translucentPipeline.end();
    }

    public boolean hasSolidTasks() {
        return !solidPipeline.isEmpty();
    }

    public boolean hasOutlineTasks() {
        return !outlinePipeline.isEmpty();
    }

    public boolean hasTranslucentTasks() {
        return !translucentPipeline.isEmpty();
    }

    @Override
    public String toString() {
        var size = solidPipeline.size() + outlinePipeline.size() + translucentPipeline.size();
        var vertexCount = solidPipeline.vertexCount() + outlinePipeline.vertexCount() + translucentPipeline.vertexCount();
        return Objects.toString(this, "passes", size, "vertices", vertexCount);
    }

    private static class Pipeline {

        private final Shader shader = new AbstractShader();
        private final ShaderVertexMerger merger = new ShaderVertexMerger();

        public void add(ShaderVertexObject pass) {
            merger.add(pass);
        }

        public void end() {
            if (merger.isEmpty()) {
                return;
            }
            merger.prepare();

            shader.begin();
            merger.forEach(group -> shader.apply(group, () -> group.forEach(shader::render)));
            shader.end();

            merger.reset();
        }

        public void clear() {
            merger.reset();
            merger.clear();
        }

        public int size() {
            return merger.size();
        }

        public int vertexCount() {
            return merger.vertexCount();
        }

        public boolean isEmpty() {
            return merger.isEmpty();
        }

        @Override
        public String toString() {
            return Objects.toString(this, "passes", size(), "vertices", vertexCount());
        }
    }

    private static class Pass extends ReferenceCounted implements ShaderVertexObject {

        private static final ObjectPool<Pass> POOL = ObjectPool.create(Pass::new);

        private int overlay;
        private int lightmap;
        private int outlineColor;

        private float polygonOffset;

        private OpenPoseStack.Pose pose;
        private ConcurrentBufferCompiler.Pass compiledTask;

        public static Pass newInstance(ConcurrentBufferCompiler.Pass compiledTask, int lightmap, int overlay, int outlineColor, float renderPriority, OpenPoseStack.Pose pose) {
            var that = POOL.alloc();
            that.compiledTask = compiledTask;
            that.pose = pose;
            that.overlay = overlay;
            that.lightmap = lightmap;
            that.outlineColor = outlineColor;
            that.polygonOffset = compiledTask.polygonOffset + renderPriority;
            return that;
        }

        @Override
        public IRenderType type() {
            return compiledTask.renderType;
        }

        @Override
        public int vertexOffset() {
            return compiledTask.vertexOffset;
        }

        @Override
        public int vertexCount() {
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
        public OpenPoseStack.Pose pose() {
            return pose;
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

        @Override
        protected void init() {
            if (compiledTask != null) {
                compiledTask.retain();
            }
        }

        @Override
        protected void dispose() {
            if (compiledTask != null) {
                compiledTask.release();
                compiledTask = null;
            }
        }
    }
}
