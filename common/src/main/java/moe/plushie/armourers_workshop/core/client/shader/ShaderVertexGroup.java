package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.client.texture.TextureAnimationController;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ShaderVertexGroup {

    public int maxVertexCount;

    private final IRenderType renderType;
    private final TextureAnimationController animationController;
    private final ArrayList<ShaderVertexObject> objects = new ArrayList<>();

    public ShaderVertexGroup(IRenderType renderType) {
        this.renderType = renderType;
        this.animationController = TextureAnimationController.of(renderType);
    }

    public OpenMatrix4f getTextureMatrix(double animationTime) {
        return animationController.getTextureMatrix(animationTime);
    }

    public IRenderType renderType() {
        return renderType;
    }

    public int size() {
        return objects.size();
    }

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

    public void add(ShaderVertexObject object) {
        object.retain();
        objects.add(object);
    }

    public void clear() {
        objects.forEach(ShaderVertexObject::release);
        objects.clear();
        maxVertexCount = 0;
    }

    public void forEach(Consumer<ShaderVertexObject> consumer) {
        objects.forEach(consumer);
    }
}
