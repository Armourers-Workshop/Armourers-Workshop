package moe.plushie.armourers_workshop.core.data.action;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashMap;

public class EntityActionSet {

    protected final BitSet flags = new BitSet(EntityAction.values().length);

    public void set(EntityAction action, boolean value) {
        if (value) {
            flags.set(action.ordinal());
        }
    }

    public boolean contains(EntityAction action) {
        if (action == EntityAction.IDLE) {
            return flags.isEmpty();
        }
        return flags.get(action.ordinal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityActionSet that)) return false;
        return flags.equals(that.flags);
    }

    @Override
    public int hashCode() {
        return flags.hashCode();
    }

    public EntityActionSet copy() {
        var result = new EntityActionSet();
        result.flags.or(flags);
        return result;
    }

    @Override
    public String toString() {
        var prefix = "";
        var lists = new LinkedHashMap<String, ArrayList<String>>();
        var results = new StringBuilder();
        for (var flag : EntityAction.values()) {
            if (contains(flag)) {
                var parts = flag.name().toLowerCase().split("_");
                var sp = lists.computeIfAbsent(parts[0], k -> new ArrayList<>());
                sp.addAll(Arrays.asList(parts).subList(1, parts.length));
            }
        }
        for (var entry : lists.entrySet()) {
            results.append(prefix);
            results.append(entry.getKey());
            prefix = "; ";
            if (!entry.getValue().isEmpty()) {
                results.append(entry.getValue());
            }
        }
        return results.toString();
    }
}
