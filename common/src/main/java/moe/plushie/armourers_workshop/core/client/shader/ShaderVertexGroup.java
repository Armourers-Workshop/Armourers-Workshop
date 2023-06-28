package moe.plushie.armourers_workshop.core.client.shader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ShaderVertexGroup {

    public int maxVertexCount;

    private final RenderType type;
    private final ArrayList<ShaderVertexObject> objects = new ArrayList<>();

    public ShaderVertexGroup(RenderType type) {
        this.type = type;
    }

    public RenderType getType() {
        return type;
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
