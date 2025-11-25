package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.client.shader.Shader;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexGroup;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.client.texture.TextureAnimationController;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.function.Consumer;

public class ConcurrentRenderingPipeline {

    private final ArrayList<Group> sortedGroups = new ArrayList<>();
    private final IdentityHashMap<IRenderType, Group> unsortedGroups = new IdentityHashMap<>();

    public void submit(ConcurrentBufferCompiler.Pass compiledTask, int lightmap, int overlay, int outlineColor, float renderPriority, OpenPoseStack.Pose pose) {
        var pass = Pass.newInstance(compiledTask, lightmap, overlay, outlineColor, renderPriority, pose);
        addPass(pass);
    }

    public ShaderVertexGroup find(IRenderType renderType) {
        return unsortedGroups.get(renderType);
    }

    public void render(Shader shader, IRenderType renderType) {
        var group = unsortedGroups.get(renderType);
        if (group.isEmpty()) {
            return;
        }
        shader.setupRenderState(group);
        group.forEach(object -> shader.render(object, group));
        shader.clearRenderState(group);

        // clear after the rendering.
        group.clear();
    }

    public void clear() {
        sortedGroups.clear();
        unsortedGroups.clear();
    }

    public int passCount() {
        int total = 0;
        for (var group : sortedGroups) {
            total += group.passCount();
        }
        return total;
    }

    public int vertexCount() {
        int vertexTotal = 0;
        for (var group : sortedGroups) {
            vertexTotal += group.vertexCount();
        }
        return vertexTotal;
    }

    public int maxVertexCount() {
        int vertexTotal = 0;
        for (var group : sortedGroups) {
            vertexTotal = Math.max(vertexTotal, group.vertexCount());
        }
        return vertexTotal;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "passes", passCount(), "vertices", vertexCount());
    }

    private void addPass(ShaderVertexObject pass) {
        var group = unsortedGroups.get(pass.type());
        if (group == null) {
            group = addAndSortGroup(pass.type());
            unsortedGroups.put(pass.type(), group);
        }
        group.add(pass);
    }

    private Group addAndSortGroup(IRenderType type) {
        var group = new Group(type);
        sortedGroups.add(group);
        sortedGroups.sort(Comparator.comparing(this::getRenderOrder));
        return group;
    }

    private int getRenderOrder(Group group) {
        int index = group.renderType().ordinal();
        if (index > 0) {
            return index;
        }
        return Integer.MAX_VALUE;
    }

    private static class Group implements ShaderVertexGroup {

        private final IRenderType renderType;
        private final TextureAnimationController animationController;
        private final ArrayList<ShaderVertexObject> objects = new ArrayList<>();

        public Group(IRenderType renderType) {
            this.renderType = renderType;
            this.animationController = TextureAnimationController.of(renderType);
        }

        public void add(ShaderVertexObject object) {
            object.retain();
            objects.add(object);
        }

        public void forEach(Consumer<ShaderVertexObject> consumer) {
            objects.forEach(consumer);
        }

        public void clear() {
            objects.forEach(ShaderVertexObject::release);
            objects.clear();
        }

        @Override
        public OpenMatrix4f getTextureMatrix(double animationTime) {
            return animationController.getTextureMatrix(animationTime);
        }

        @Override
        public IRenderType renderType() {
            return renderType;
        }

        @Override
        public int passCount() {
            return objects.size();
        }

        @Override
        public int vertexCount() {
            var vertexTotal = 0;
            for (var object : objects) {
                vertexTotal += object.vertexCount();
            }
            return vertexTotal;
        }

        public boolean isEmpty() {
            return objects.isEmpty();
        }

        @Override
        public String toString() {
            return Objects.toString(this, "passes", passCount(), "vertices", vertexCount());
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
            if (isOutline()) {
                return outlineColor;
            }
            return -1;
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
