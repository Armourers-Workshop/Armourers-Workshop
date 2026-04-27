package moe.plushie.armourers_workshop.core.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Collections {


    public static <S> ArrayList<S> filter(S[] in, Predicate<? super S> predicate) {
        return filter(newList(in), predicate);
    }

    public static <S> ArrayList<S> filter(Collection<? extends S> in, Predicate<? super S> predicate) {
        return compactMap(in, it -> {
            if (predicate.test(it)) {
                return it;
            }
            return null;
        });
    }

    public static <S> Iterable<S> filter(Iterable<? extends S> in, Predicate<? super S> predicate) {
        return compactMap(in, it -> {
            if (predicate.test(it)) {
                return it;
            }
            return null;
        });
    }


    public static <S, R> ArrayList<R> collect(Collection<? extends S> in, Class<? extends R> clazz) {
        return compactMap(in, it -> {
            if (clazz.isInstance(it)) {
                return clazz.cast(it);
            }
            return null;
        });
    }

    public static <S, R> Iterable<R> collect(Iterable<? extends S> in, Class<? extends R> clazz) {
        return compactMap(in, it -> {
            if (clazz.isInstance(it)) {
                return clazz.cast(it);
            }
            return null;
        });
    }

    public static <S, R> ArrayList<R> compactMap(S[] in, Function<S, ? extends R> transform) {
        return compactMap(newList(in), transform);
    }

    public static <S, R> ArrayList<R> compactMap(Collection<? extends S> in, Function<S, ? extends R> transform) {
        var results = new ArrayList<R>(in.size());
        for (var value : in) {
            if (value == null) {
                continue;
            }
            var result = transform.apply(value);
            if (result == null) {
                continue;
            }
            results.add(result);
        }
        return results;
    }

    public static <S, R> Iterable<R> compactMap(Iterable<? extends S> in, Function<S, ? extends R> transform) {
        return () -> new Iterator<R>() {
            final Iterator<? extends S> baseIterator = in.iterator();
            R nextValue = null;

            @Override
            public boolean hasNext() {
                return peek() != null;
            }

            @Override
            public R next() {
                R value = peek();
                nextValue = null;
                return value;
            }

            private R peek() {
                while (nextValue == null && baseIterator.hasNext()) {
                    S value = baseIterator.next();
                    if (value != null) {
                        nextValue = transform.apply(value);
                    }
                }
                return nextValue;
            }
        };
    }

    public static <S, R> ArrayList<R> flatMap(S[] in, Function<S, Collection<? extends R>> transform) {
        return flatMap(newList(in), transform);
    }

    public static <S, R> ArrayList<R> flatMap(Collection<? extends S> in, Function<S, Collection<? extends R>> transform) {
        var results = new ArrayList<R>(in.size());
        for (var value : in) {
            if (value == null) {
                continue;
            }
            var result = transform.apply(value);
            if (result == null) {
                continue;
            }
            results.addAll(result);
        }
        return results;
    }


    public static <T> List<T> emptyList() {
        return java.util.Collections.emptyList();
    }

    public static <K, V> Map<K, V> emptyMap() {
        return java.util.Collections.emptyMap();
    }

    @SafeVarargs
    public static <T> HashSet<T> newSet(T... elements) {
        return Sets.newHashSet(elements);
    }

    public static <T> HashSet<T> newSet(Iterable<? extends T> elements) {
        return Sets.newHashSet(elements);
    }

    @SafeVarargs
    public static <T> ArrayList<T> newList(T... elements) {
        return Lists.newArrayList(elements);
    }

    public static <T> ArrayList<T> newList(Iterable<? extends T> elements) {
        return Lists.newArrayList(elements);
    }

    public static <T> ArrayList<T> newList(int size, Function<Integer, T> builder) {
        ArrayList<T> results = new ArrayList<>();
        results.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            results.add(builder.apply(i));
        }
        return results;
    }

    public static <T> ImmutableSet<T> immutableSet(Consumer<ImmutableSet.Builder<T>> builder) {
        var setBuilder = ImmutableSet.<T>builder();
        builder.accept(setBuilder);
        return setBuilder.build();
    }

    public static <T> ImmutableList<T> immutableList(Consumer<ImmutableList.Builder<T>> builder) {
        var listBuilder = ImmutableList.<T>builder();
        builder.accept(listBuilder);
        return listBuilder.build();
    }

    public static <K, V> ImmutableMap<K, V> immutableMap(Consumer<ImmutableMap.Builder<K, V>> builder) {
        var mapBuilder = ImmutableMap.<K, V>builder();
        builder.accept(mapBuilder);
        return mapBuilder.build();
    }

    public static <T> Set<T> singleton(T o) {
        return java.util.Collections.singleton(o);
    }

    public static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp) {
        return java.util.Collections.max(coll, comp);
    }


    public static <T> void eachTree(Iterable<T> collection, Function<T, Iterable<T>> children, Consumer<T> consumer) {
        for (T value : collection) {
            consumer.accept(value);
            eachTree(children.apply(value), children, consumer);
        }
    }


    public static <T> Iterator<T> cycle(Iterable<T> iterable) {
        return Iterators.cycle(iterable);
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b) {
        return Iterables.concat(a, b);
    }

    public static <T> int indexOf(Iterable<T> iterable, Predicate<? super T> predicate) {
        return Iterables.indexOf(iterable, predicate::test);
    }
}
