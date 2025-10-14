package moe.plushie.armourers_workshop.core.data.ticket;

import java.util.function.Supplier;

public interface Ticket<K> extends Supplier<K> {

    float priority();

    boolean invalid();
}

