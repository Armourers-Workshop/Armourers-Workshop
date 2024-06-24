package moe.plushie.armourers_workshop.core.data.cache;

import java.util.Objects;

public abstract class PrimaryKey {

    protected int hash;

    public abstract PrimaryKey copy();

    @Override
    public int hashCode() {
        return hash;
    }

    public static PrimaryKey of(Object p1) {
        int hash = (p1 == null ? 0 : p1.hashCode());
        return P1.POOL.get().set(hash, p1);
    }

    public static PrimaryKey of(Object p1, Object p2) {
        int hash = (p1 == null ? 0 : p1.hashCode());
        hash = 31 * hash + (p2 == null ? 0 : p2.hashCode());
        return P2.POOL.get().set(hash, p1, p2);
    }

    public static PrimaryKey of(Object p1, Object p2, Object p3) {
        int hash = (p1 == null ? 0 : p1.hashCode());
        hash = 31 * hash + (p2 == null ? 0 : p2.hashCode());
        hash = 31 * hash + (p3 == null ? 0 : p3.hashCode());
        return P3.POOL.get().set(hash, p1, p2, p3);
    }

    public static PrimaryKey of(Object p1, Object p2, Object p3, Object p4) {
        int hash = (p1 == null ? 0 : p1.hashCode());
        hash = 31 * hash + (p2 == null ? 0 : p2.hashCode());
        hash = 31 * hash + (p3 == null ? 0 : p3.hashCode());
        hash = 31 * hash + (p4 == null ? 0 : p4.hashCode());
        return P4.POOL.get().set(hash, p1, p2, p3, p4);
    }

    public static class P1 extends PrimaryKey {

        protected static final AutoreleasePool<P1> POOL = AutoreleasePool.create(P1::new);
        protected Object p1;

        public P1 set(int hash, Object p1) {
            this.hash = hash;
            this.p1 = p1;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof P1 that)) return false;
            return Objects.equals(p1, that.p1);
        }

        @Override
        public PrimaryKey copy() {
            return new P1().set(hash, p1);
        }
    }

    public static class P2 extends P1 {

        protected static final AutoreleasePool<P2> POOL = AutoreleasePool.create(P2::new);
        protected Object p2;

        public P2 set(int hash, Object p1, Object p2) {
            this.hash = hash;
            this.p1 = p1;
            this.p2 = p2;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof P2 that)) return false;
            return Objects.equals(p1, that.p1) && Objects.equals(p2, that.p2);
        }

        @Override
        public PrimaryKey copy() {
            return new P2().set(hash, p1, p2);
        }
    }

    public static class P3 extends P2 {

        protected static final AutoreleasePool<P3> POOL = AutoreleasePool.create(P3::new);
        protected Object p3;

        public P3 set(int hash, Object p1, Object p2, Object p3) {
            this.hash = hash;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof P3 that)) return false;
            return Objects.equals(p1, that.p1) && Objects.equals(p2, that.p2) && Objects.equals(p3, that.p3);
        }

        @Override
        public PrimaryKey copy() {
            return new P3().set(hash, p1, p2, p3);
        }
    }

    public static class P4 extends P3 {

        protected static final AutoreleasePool<P4> POOL = AutoreleasePool.create(P4::new);
        protected Object p4;

        public P4 set(int hash, Object p1, Object p2, Object p3, Object p4) {
            this.hash = hash;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.p4 = p4;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof P4 that)) return false;
            return Objects.equals(p1, that.p1) && Objects.equals(p2, that.p2) && Objects.equals(p3, that.p3) && Objects.equals(p4, that.p4);
        }

        @Override
        public PrimaryKey copy() {
            return new P4().set(hash, p1, p2, p3, p4);
        }
    }
}
