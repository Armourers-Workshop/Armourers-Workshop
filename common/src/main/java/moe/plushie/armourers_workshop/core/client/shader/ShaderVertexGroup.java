package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.core.client.texture.TextureAnimationController;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ShaderVertexGroup {

    public int maxVertexCount;

    private final RenderType renderType;
    private final TextureAnimationController animationController;
    private final ArrayList<ShaderVertexObject> objects = new ArrayList<>();

    public ShaderVertexGroup(RenderType renderType) {
        this.renderType = renderType;
        this.animationController = TextureAnimationController.of(renderType);
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public OpenMatrix4f getTextureMatrix(int ticks) {
        return animationController.getTextureMatrix(ticks);
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public void add(ShaderVertexObject object) {
        objects.add(object);
    }

    public void clear() {
        objects.clear();
        maxVertexCount = 0;
    }

    public void forEach(Consumer<ShaderVertexObject> consumer) {
        objects.forEach(consumer);
    }
}
