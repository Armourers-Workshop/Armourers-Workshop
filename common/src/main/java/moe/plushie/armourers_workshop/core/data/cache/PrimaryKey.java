package moe.plushie.armourers_workshop.core.data.cache;


import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.core.utils.Objects;

public abstract class PrimaryKey {

    protected int hash;

    public abstract PrimaryKey copy();

    @Override
    public int hashCode() {
        return hash;
    }

    public static PrimaryKey of(Object p1) {
        int hash = (p1 == null ? 0 : p1.hashCode());
        return P1.newInstance(hash, p1);
    }

    public static PrimaryKey of(Object p1, Object p2) {
        int hash = (p1 == null ? 0 : p1.hashCode());
        hash = 31 * hash + (p2 == null ? 0 : p2.hashCode());
        return P2.newInstance(hash, p1, p2);
    }

    public static PrimaryKey of(Object p1, Object p2, Object p3) {
        int hash = (p1 == null ? 0 : p1.hashCode());
        hash = 31 * hash + (p2 == null ? 0 : p2.hashCode());
        hash = 31 * hash + (p3 == null ? 0 : p3.hashCode());
        return P3.newInstance(hash, p1, p2, p3);
    }

    public static PrimaryKey of(Object p1, Object p2, Object p3, Object p4) {
        int hash = (p1 == null ? 0 : p1.hashCode());
        hash = 31 * hash + (p2 == null ? 0 : p2.hashCode());
        hash = 31 * hash + (p3 == null ? 0 : p3.hashCode());
        hash = 31 * hash + (p4 == null ? 0 : p4.hashCode());
        return P4.newInstance(hash, p1, p2, p3, p4);
    }

    public static class P1 extends PrimaryKey {

        private static final ObjectPool<P1> POOL = ObjectPool.create(P1::new);

        protected Object p1;

        public static P1 newInstance(int hash, Object p1) {
            var that = POOL.alloc();
            that.hash = hash;
            that.p1 = p1;
            return that;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof P1 that)) return false;
            return Objects.equals(p1, that.p1);
        }

        @Override
        public PrimaryKey copy() {
            var that = new P1();
            that.hash = hash;
            that.p1 = p1;
            return that;
        }
    }

    public static class P2 extends P1 {

        private static final ObjectPool<P2> POOL = ObjectPool.create(P2::new);

        protected Object p2;

        public static P2 newInstance(int hash, Object p1, Object p2) {
            var that = POOL.alloc();
            that.hash = hash;
            that.p1 = p1;
            that.p2 = p2;
            return that;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof P2 that)) return false;
            return Objects.equals(p1, that.p1) && Objects.equals(p2, that.p2);
        }

        @Override
        public PrimaryKey copy() {
            var that = new P2();
            that.hash = hash;
            that.p1 = p1;
            that.p2 = p2;
            return that;
        }
    }

    public static class P3 extends P2 {

        private static final ObjectPool<P3> POOL = ObjectPool.create(P3::new);

        protected Object p3;

        public static P3 newInstance(int hash, Object p1, Object p2, Object p3) {
            var that = POOL.alloc();
            that.hash = hash;
            that.p1 = p1;
            that.p2 = p2;
            that.p3 = p3;
            return that;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof P3 that)) return false;
            return Objects.equals(p1, that.p1) && Objects.equals(p2, that.p2) && Objects.equals(p3, that.p3);
        }

        @Override
        public PrimaryKey copy() {
            var that = new P3();
            that.hash = hash;
            that.p1 = p1;
            that.p2 = p2;
            that.p3 = p3;
            return that;
        }
    }

    public static class P4 extends P3 {

        private static final ObjectPool<P4> POOL = ObjectPool.create(P4::new);

        protected Object p4;

        public static P4 newInstance(int hash, Object p1, Object p2, Object p3, Object p4) {
            var that = POOL.alloc();
            that.hash = hash;
            that.p1 = p1;
            that.p2 = p2;
            that.p3 = p3;
            that.p4 = p4;
            return that;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof P4 that)) return false;
            return Objects.equals(p1, that.p1) && Objects.equals(p2, that.p2) && Objects.equals(p3, that.p3) && Objects.equals(p4, that.p4);
        }

        @Override
        public PrimaryKey copy() {
            var that = new P4();
            that.hash = hash;
            that.p1 = p1;
            that.p2 = p2;
            that.p3 = p3;
            that.p4 = p4;
            return that;
        }
    }
}
