package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ShaderVertexMerger {

    private int maxVertexCount = 0;

    private final ArrayList<ShaderVertexGroup> sortedGroups = new ArrayList<>();
    private final HashMap<RenderType, ShaderVertexGroup> pending = new HashMap<>();

    public void add(ShaderVertexObject pass) {
        pending.computeIfAbsent(pass.getType(), this::addAndSort).add(pass);
        maxVertexCount = Math.max(maxVertexCount, pass.getVertexCount());
    }

    public void forEach(Consumer<ShaderVertexGroup> consumer) {
        for (ShaderVertexGroup group : sortedGroups) {
            if (group.isEmpty()) {
                continue;
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


    private ShaderVertexGroup addAndSort(RenderType type) {
        ShaderVertexGroup group = new ShaderVertexGroup(type);
        sortedGroups.add(group);
        sortedGroups.sort(Comparator.comparing(this::getRenderOrder));
        return group;
    }

    private int getRenderOrder(ShaderVertexGroup group) {
        int index = SkinRenderType.getOrdering(group.getRenderType());
        if (index > 0) {
            return index;
        }
        return Integer.MAX_VALUE;
    }
}
