package moe.plushie.armourers_workshop.core.data.ticket;

import java.util.HashMap;
import java.util.HashSet;

public abstract class Ticket {

    public static Ticket limited(int time, float priority) {
        return new LimitedTime(time, priority);
    }

    public static Ticket normal(float priority) {
        return new Impl(priority);
    }

    public static Ticket list() {
        return normal(0);
    }

    public static Ticket wardrobe() {
        return normal(0);
    }

    public abstract void invalidate();

    public abstract void add(Object key);

    public abstract boolean contains(Object key);

    public abstract float priority(Object key);


    protected static class LimitedTime extends Ticket {

        private final int interval;
        private final float priority;
        private final HashMap<Object, Long> identifiers = new HashMap<>();

        public LimitedTime(int interval, float priority) {
            this.interval = interval;
            this.priority = priority;
        }

        @Override
        public void invalidate() {
            identifiers.clear();
        }

        @Override
        public void add(Object key) {
            Long time = System.currentTimeMillis();
            identifiers.put(key, time);
        }

        @Override
        public boolean contains(Object key) {
            Long time = identifiers.get(key);
            if (time != null) {
                return (System.currentTimeMillis() - time) <= interval;
            }
            return false;
        }

        @Override
        public float priority(Object key) {
            return priority;
        }
    }

    protected static class Impl extends Ticket {

        private final float priority;
        private final HashSet<Object> identifiers = new HashSet<>();

        protected Impl(float priority) {
            this.priority = priority;
        }

        @Override
        public void invalidate() {
            identifiers.clear();
        }

        @Override
        public void add(Object key) {
            identifiers.add(key);
        }

        @Override
        public boolean contains(Object key) {
            return identifiers.contains(key);
        }

        @Override
        public float priority(Object key) {
            return priority;
        }
    }
}
