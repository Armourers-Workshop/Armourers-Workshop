package moe.plushie.armourers_workshop.api.registry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IEventHandler<E> {

    default <O> IEventHandler<O> flatMap(Function<E, O> transformer) {
        return (priority, receiveCancelled, handler) -> listen(priority, receiveCancelled, event -> {
            O value = transformer.apply(event);
            if (value != null) {
                handler.accept(value);
            }
        });
    }

    default <O> IEventHandler<O> map(Function<E, O> transformer) {
        return (priority, receiveCancelled, handler) -> listen(priority, receiveCancelled, event -> handler.accept(transformer.apply(event)));
    }

    default <O, P> IEventHandler<Pair<O, P>> map(Function<E, O> transformer1, Function<E, P> transformer2) {
        return (priority, receiveCancelled, handler) -> listen(priority, receiveCancelled, event -> {
            O value1 = transformer1.apply(event);
            P value2 = transformer2.apply(event);
            handler.accept(Pair.of(value1, value2));
        });
    }

    default <O, P, Q> IEventHandler<Triple<O, P, Q>> map(Function<E, O> transformer1, Function<E, P> transformer2, Function<E, Q> transformer3) {
        return (priority, receiveCancelled, handler) -> listen(priority, receiveCancelled, event -> {
            O value1 = transformer1.apply(event);
            P value2 = transformer2.apply(event);
            Q value3 = transformer3.apply(event);
            handler.accept(Triple.of(value1, value2, value3));
        });
    }

    default void listen(Consumer<E> consumer) {
        listen(Priority.NORMAL, false, consumer);
    }

    void listen(Priority priority, boolean receiveCancelled, Consumer<E> consumer);

    enum Priority {

        HIGHEST, //First to execute
        HIGH,
        NORMAL,
        LOW,
        LOWEST; //Last to execute
    }
}
