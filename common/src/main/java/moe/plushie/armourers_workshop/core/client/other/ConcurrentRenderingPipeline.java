package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IMeshData;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compat.client.platform.AbstractRenderDevice;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexGroup;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexObject;
import moe.plushie.armourers_workshop.core.client.texture.ColorModulator;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.client.texture.TextureAnimationController;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.core.utils.TickUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;

public class ConcurrentRenderingPipeline {

    private final AbstractRenderDevice device = AbstractRenderDevice.current();

    private final ArrayList<Group> sortedGroups = new ArrayList<>();
    private final IdentityHashMap<IRenderType, Group> unsortedGroups = new IdentityHashMap<>();

    public void submit(ConcurrentBufferCompiler.Pass compiledTask, int lightmap, int overlay, int outlineColor, float renderPriority, OpenPoseStack.Pose pose) {
        var pass = Pass.newInstance(compiledTask, lightmap, overlay, outlineColor, renderPriority, pose);
        addPass(pass);
    }

    public ShaderVertexGroup find(IRenderType renderType) {
        return unsortedGroups.get(renderType);
    }

    public void beginTransaction() {
        device.beginTransaction();
    }

    public void flush(IRenderType renderType) {
        // find the pending render group.
        var group = unsortedGroups.get(renderType);
        if (group == null || group.isEmpty()) {
            return;
        }
        device.beginGroup();
        device.setPolygonOffset(0.0f, -50.0f);

        // apply changes of texture animation.
        device.setTextureMatrix(group.getTextureMatrix(TickUtils.animationTick()));

        for (var object : group.objects()) {
            var pose = object.pose();
            var result = object.data();

            // we need fast update the uniforms,
            // so we're never using from vanilla uniforms.
            device.setMatrixFlags(pose.properties() | 0x01);
            device.setColorModulator(ColorModulator.getColor(object.outlineColor()));
            device.setObjectViewMatrix(pose.pose());
            device.setObjectNormalMatrix(pose.normal());
            device.setOverlayTextureMatrix(OverlayTexture.getTextureMatrix(object.overlay()));
            device.setLightmapTextureMatrix(LightmapTexture.getTextureMatrix(object.lightmap(), object.isEmissive()));

            // https://web.archive.org/web/20201010072314/https://sites.google.com/site/threejstuts/home/polygon_offset
            // For polygons that are parallel to the near and far clipping planes, the depth slope is zero.
            // For the polygons in your scene with a depth slope near zero, only a small, constant offset is needed.
            // To create a small, constant offset, you can pass factor = 0.0 and units = 1.0.
            device.setPolygonOffset(0.0f, -50.0f + object.polygonOffset() * -1f);

            // submit draw into renderer.
            device.draw(result);
        }

        device.setPolygonOffset(0.0f, 0.0f);
        device.endGroup();

        // clear after the rendering.
        group.clear();
    }

    public void endTransaction() {
        device.endTransaction();
    }

    public void clear() {
        sortedGroups.clear();
        unsortedGroups.clear();
    }

    public void clear(IRenderType renderType) {
        var group = unsortedGroups.get(renderType);
        if (group != null) {
            group.clear();
        }
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

        public void clear() {
            objects.forEach(ShaderVertexObject::release);
            objects.clear();
        }

        public List<? extends ShaderVertexObject> objects() {
            return objects;
        }

        @Override
        public IRenderType renderType() {
            return renderType;
        }

        @Override
        public OpenMatrix4f getTextureMatrix(double animationTime) {
            return animationController.getTextureMatrix(animationTime);
        }

        @Override
        public int passCount() {
            return objects.size();
        }

        @Override
        public int vertexCount() {
            var vertexTotal = 0;
            for (var object : objects) {
                vertexTotal += object.data().vertexCount();
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
        public IMeshData data() {
            return compiledTask.data;
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
