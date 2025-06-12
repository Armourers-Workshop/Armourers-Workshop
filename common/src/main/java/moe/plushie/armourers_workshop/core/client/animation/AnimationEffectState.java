package moe.plushie.armourers_workshop.core.client.animation;

import java.util.List;

public class AnimationEffectState {

    private final String name;

    private Object value;
    private Object result;

    public AnimationEffectState(String name) {
        this.name = name;
    }

    public void reset() {
        setValue(null, null);
    }

    public void setValue(Object effect, Object result) {
        this.cancel(this.result);
        this.value = effect;
        this.result = result;
    }

    public Object value() {
        return this.value;
    }

    public String name() {
        return name;
    }

    private void cancel(Object result) {
        // we need expand the multiple results.
        if (result instanceof List<?> list) {
            for (var value : list) {
                cancel(value);
            }
        }
        // we need cancel it.
        if (result instanceof Runnable action) {
            action.run();
        }
    }
}
