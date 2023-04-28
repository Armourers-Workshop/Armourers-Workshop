package moe.plushie.armourers_workshop.utils;

public abstract class Proxy<T> {

    public static <S, T> T of(S src) {
        // noinspection unchecked
        return (T) src;
    }

    private T _target;

    public void retain(T target) {
        _target = target;
    }

    public T release() {
        T target = _target;
        _target = null;
        return target;
    }
}
