package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.client.texture.TextureAnimationController;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ShaderVertexGroup {

    public int maxVertexCount;

    private final IRenderType renderType;
    private final TextureAnimationController animationController;
    private final ArrayList<ShaderVertexObject> objects = new ArrayList<>();

    public ShaderVertexGroup(IRenderType renderType) {
        this.renderType = renderType;
        this.animationController = TextureAnimationController.of(renderType);
    }

    public IRenderType renderType() {
        return renderType;
    }

    public OpenMatrix4f textureMatrix(double animationTime) {
        return animationController.textureMatrix(animationTime);
    }

    public int size() {
        return objects.size();
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public void add(ShaderVertexObject object) {
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
