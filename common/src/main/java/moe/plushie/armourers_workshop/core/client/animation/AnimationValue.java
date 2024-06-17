package moe.plushie.armourers_workshop.core.client.animation;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationFunction;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationValue;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.util.List;
import java.util.function.Supplier;

public class AnimationValue {

    private int index;

    private int start;
    private int end;

    private float time;
    private float duration;

    private boolean requiresVirtualMachine = false;
    private SkinAnimationFunction function;

    private Supplier<Vector3f> fromValue;
    private Supplier<Vector3f> toValue;

    public AnimationValue(SkinAnimationValue value) {
        this.time = value.getTime();
        this.function = value.getFunction();
        this.updateValues(value.getKey(), value.getPoints());
        this.updateRange();
    }

    public boolean contains(float t) {
        int value = (int) (t * 1000);
        return start <= value && value <= end;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setTime(float time) {
        this.time = time;
        this.updateRange();
    }

    public float getTime() {
        return time;
    }

    public void setDuration(float duration) {
        this.duration = duration;
        this.updateRange();
    }

    public float getDuration() {
        return duration;
    }

    public SkinAnimationFunction getFunction() {
        return function;
    }

    public void setFromValue(Supplier<Vector3f> fromValue) {
        this.fromValue = fromValue;
    }

    public Supplier<Vector3f> getFromValue() {
        return fromValue;
    }

    public void setToValue(Supplier<Vector3f> toValue) {
        this.toValue = toValue;
    }

    public Supplier<Vector3f> getToValue() {
        return toValue;
    }

    public boolean isRequiresVirtualMachine() {
        return requiresVirtualMachine;
    }

    private void updateValues(String key, List<Object> objects) {
        var def = key.equals("scale") ? 1f : 0f;
        if (objects.size() < 3) {
            objects = Lists.newArrayList(def, def, def);
        }
        // build all provider.
        var providers = new Provider[6];
        for (int i = 0; i < providers.length; ++i) {
            if (i < objects.size()) {
                providers[i] = compile(objects[i], def);
            } else {
                providers[i] = providers[i % objects.size()];
            }
        }
        // setup pre value.
        // TODO: no impl @SAGESSE
        //var x0 = providers[0];
        //var y0 = providers[1];
        //var z0 = providers[2];
        //setFromValue(() -> new Vector3f(x0.getAsFloat(), y0.getAsFloat(), z0.getAsFloat()));

        // setup post value.
        var x1 = providers[3];
        var y1 = providers[4];
        var z1 = providers[5];
        setToValue(() -> new Vector3f(x1.getAsFloat(), y1.getAsFloat(), z1.getAsFloat()));
    }

    private void updateRange() {
        this.start = (int) (time * 1000);
        this.end = (int) ((time + duration) * 1000);
    }

    private Provider compile(Object object, float defaultValue) {
        try {
            if (object instanceof Number number) {
                return number::floatValue;
            }
            if (object instanceof String script) {
                var expr = MolangVirtualMachine.get().create(script);
                requiresVirtualMachine = true;
                return () -> (float) expr.get();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return () -> defaultValue;
    }

    public interface Provider {

        float getAsFloat();
    }
}
