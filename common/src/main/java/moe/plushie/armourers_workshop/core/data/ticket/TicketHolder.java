package moe.plushie.armourers_workshop.core.data.ticket;

import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;

import java.util.HashMap;

public class TicketHolder {

    private final String name;
    private final float priority;
    private final int interval;

    private final HashMap<String, Entry> tickets = new HashMap<>();

    public TicketHolder(String name) {
        this(name, 0, 0);
    }

    public TicketHolder(String name, float priority) {
        this(name, 0, priority);
    }

    public TicketHolder(String name, int interval, float priority) {
        this.name = name;
        this.interval = interval;
        this.priority = priority;
    }

    public Ticket<String> get(String identifier) {
        var ticket = tickets.computeIfAbsent(identifier, this::create);
        ticket.update();
        return ticket;
    }

    public Ticket<String> get(SkinDescriptor descriptor) {
        return get(descriptor.identifier());
    }

    public void invalidate() {
        tickets.values().forEach(Entry::invalidate);
        tickets.clear();
    }

    private Entry create(String identifier) {
        if (interval != 0) {
            return new TimeLimitedEntry(identifier);
        }
        return new Entry(identifier);
    }

    private class Entry implements Ticket<String> {

        private final String key;

        private boolean valid = false;

        private Entry(String key) {
            this.key = key;
        }

        protected void update() {
            valid = true;
        }

        protected void invalidate() {
            valid = false;
        }

        protected boolean check() {
            return valid;
        }

        @Override
        public float priority() {
            return priority;
        }

        @Override
        public boolean invalid() {
            return !check();
        }

        @Override
        public String get() {
            return key;
        }

        @Override
        public String toString() {
            return String.format("[%s.%s@%.1f]", name, key, priority);
        }
    }

    private class TimeLimitedEntry extends Entry {

        private long expiredTime = 0;

        private TimeLimitedEntry(String key) {
            super(key);
        }

        @Override
        protected void update() {
            super.update();
            expiredTime = System.currentTimeMillis() + interval;
        }

        @Override
        protected boolean check() {
            return super.check() && expiredTime >= System.currentTimeMillis();
        }
    }
}
