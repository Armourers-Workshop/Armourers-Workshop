package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ShaderVertexMerger {

    private int maxVertexCount = 0;

    private final HashMap<RenderType, ShaderVertexGroup> pending = new HashMap<>();

    public void add(ShaderVertexObject pass) {
        pending.computeIfAbsent(pass.getType(), ShaderVertexGroup::new).add(pass);
        maxVertexCount = Math.max(maxVertexCount, pass.getVertexCount());
    }

    public void forEach(Consumer<ShaderVertexGroup> consumer) {
        for (RenderType renderType : SkinRenderType.RENDER_ORDERING_FACES) {
            ShaderVertexGroup group = pending.get(renderType);
            if (group == null || group.isEmpty()) {
                return;
            }
            group.maxVertexCount = maxVertexCount;
            consumer.accept(group);
        }
    }

    public void prepare() {
    }

    public void reset() {
        pending.forEach((renderType, group) -> group.clear());
    }

    public boolean isEmpty() {
        return maxVertexCount == 0;
    }
}
