package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.client.IRenderType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;

public class ShaderVertexMerger {

    private int maxVertexCount = 0;

    private final ArrayList<ShaderVertexGroup> sortedGroups = new ArrayList<>();
    private final HashMap<IRenderType, ShaderVertexGroup> pending = new HashMap<>();

    public void add(ShaderVertexObject pass) {
        var group = pending.get(pass.type());
        if (group == null) {
            group = addAndSort(pass.type());
            pending.put(pass.type(), group);
        }
        group.add(pass);
        maxVertexCount = Math.max(maxVertexCount, pass.vertexCount());
    }

    public void forEach(Consumer<ShaderVertexGroup> consumer) {
        for (var group : sortedGroups) {
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
        maxVertexCount = 0;
    }

    public void clear() {
        sortedGroups.clear();
        pending.clear();
    }

    public int size() {
        int total = 0;
        for (var group : pending.values()) {
            total += group.size();
        }
        return total;
    }

    public int vertexCount() {
        int vertexTotal = 0;
        for (var group : pending.values()) {
            vertexTotal += group.vertexCount();
        }
        return vertexTotal;
    }

    public boolean isEmpty() {
        return maxVertexCount == 0;
    }

    private ShaderVertexGroup addAndSort(IRenderType type) {
        var group = new ShaderVertexGroup(type);
        sortedGroups.add(group);
        sortedGroups.sort(Comparator.comparing(this::getRenderOrder));
        return group;
    }

    private int getRenderOrder(ShaderVertexGroup group) {
        int index = group.renderType().ordinal();
        if (index > 0) {
            return index;
        }
        return Integer.MAX_VALUE;
    }
}
