package moe.plushie.armourers_workshop.utils.hash;

import java.util.ArrayList;

public final class Hasher {

    private static final ThreadLocal<Hasher> LHS = ThreadLocal.withInitial(Hasher::new);
    private static final ThreadLocal<Hasher> RHS = ThreadLocal.withInitial(Hasher::new);

    private final ArrayList<Object> values = new ArrayList<>();

    public static boolean equals(Hashable o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null || !(o2 instanceof Hashable)) {
            return false;
        }
        Hasher lhs = LHS.get().reset();
        Hasher rhs = RHS.get().reset();
        o1.collect(lhs);
        ((Hashable) o2).collect(rhs);
        return lhs.values.equals(rhs.values);
    }

    public static int hash(Hashable o1) {
        Hasher lhs = LHS.get().reset();
        o1.collect(lhs);
        return lhs.values.hashCode();
    }

    public void put(Object value) {
        values.add(value);
    }

    private Hasher reset() {
        values.clear();
        return this;
    }
}
