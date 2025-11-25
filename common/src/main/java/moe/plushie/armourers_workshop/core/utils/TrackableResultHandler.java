package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.init.ModLog;

public class TrackableResultHandler<T> implements IResultHandler<T> {

    private final Object action;
    private final Object key;

    private final IResultHandler<T> impl;

    public TrackableResultHandler(Object action, Object key, IResultHandler<T> impl) {
        this.impl = impl;
        this.action = action;
        this.key = key;
        ModLog.debug("start {} '{}'", action, key);
    }

    @Override
    public void apply(T value, Throwable exception) {
        if (value != null) {
            ModLog.debug("did {} '{}' => {}", action, key, value);
        } else {
            ModLog.warn("abort {} '{}'", action, key, exception);
        }
        impl.apply(value, exception);
    }
}
