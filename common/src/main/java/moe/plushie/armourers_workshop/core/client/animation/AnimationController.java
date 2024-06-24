package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationValue;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Supplier;

public class AnimationController {

    private int channel = 0;
    private boolean requiresVirtualMachine = false;

    private final ArrayList<AnimationValue> values = new ArrayList<>();
    private final ArrayList<AnimationOutput> outputs = new ArrayList<>();

    public static AnimationController create(String channel) {
        return switch (channel) {
            case "position" -> new Translation();
            case "rotation" -> new Rotation();
            case "scale" -> new Scale();
            default -> new AnimationController();
        };
    }

    public void add(SkinAnimationValue value) {
        values.add(new AnimationValue(value));
    }

    public void add(AnimationOutput output) {
        outputs.add(output);
    }

    public void process(AnimationState state, float animationTicks, Entity entity, SkinRenderContext context) {
        // populate the results of animation ticks.
        var frame = populate(state, animationTicks);

        var time = animationTicks - frame.getTime();
        var duration = frame.getDuration();
        var from = frame.getFromValue();
        var to = frame.getToValue();

        // calculate the results of interpolate.
        float tx, ty, tz;
        if (time < 0) {
            tx = from.getX();
            ty = from.getY();
            tz = from.getZ();
        } else if (time >= duration) {
            tx = to.getX();
            ty = to.getY();
            tz = to.getZ();
        } else {
            var t = time / duration;
            var f = frame.getFunction();
            tx = f.interpolating(from.getX(), to.getX(), t);
            ty = f.interpolating(from.getY(), to.getY(), t);
            tz = f.interpolating(from.getZ(), to.getZ(), t);
        }

        // upload animated data into applier.
        for (var output : outputs) {
            upload(output, tx, ty, tz);
        }

        // upload animated data into state.
        state.setLastValues(channel, tx, ty, tz);
    }

    public boolean freeze() {
        if (values.isEmpty()) {
            return false;
        }
        values.sort(Comparator.comparing(AnimationValue::getTime));
        int index = 0;
        float time = 0;
        for (var value : values) {
            float duration = value.getTime() - time;
            value.setTime(time);
            value.setDuration(duration);
            value.setIndex(index++);
            time += duration;
        }
        var firstValue = values.get(0);
        firstValue.setFromValue(firstValue.getToValue());
        requiresVirtualMachine = values.stream().anyMatch(AnimationValue::isRequiresVirtualMachine);
        return true;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getChannel() {
        return channel;
    }

    public boolean isRequiresVirtualMachine() {
        return requiresVirtualMachine;
    }

    protected void upload(AnimationOutput output, float x, float y, float z) {
        // nope
    }

    protected AnimationState.Frame populate(AnimationState state, float time) {
        var oldFrame = state.getFrame(channel);
        if (oldFrame != null && oldFrame.contains(time)) {
            return oldFrame;
        }
        var value = findLastFrame(time);
        if (oldFrame != null && oldFrame.getValue() == value) {
            return oldFrame;
        }
        var newFrame = new AnimationState.Frame(value);
        if (oldFrame != null && newFrame.getIndex() < oldFrame.getIndex()) {
            // when the animation is reversed, we need to calculate the previous position.
            var previousValue = findPreviousValue(value);
            if (previousValue != null) {
                state.setLastValues(channel, previousValue.get());
            }
        }
        var fromValue = findLastValue(value.getFromValue(), state, oldFrame);
        var toValue = findLastValue(value.getToValue(), state, oldFrame);
        newFrame.setFromValue(fromValue.get());
        newFrame.setToValue(toValue.get());
        state.setFrame(channel, newFrame);
        return newFrame;
    }

    protected AnimationValue findLastFrame(float time) {
        for (var value : values) {
            if (value.contains(time)) {
                return value;
            }
        }
        return values.get(values.size() - 1);
    }

    protected Supplier<Vector3f> findLastValue(Supplier<Vector3f> supplier, AnimationState state, AnimationState.Frame frame) {
        if (supplier != null) {
            return supplier;
        }
//        if (frame != null) {
//            var value = frame.getToValue().copy();
//            return () -> value;
//        }
        var value = getLastOrDefaultValue(state).copy();
        return () -> value;
    }

    protected Supplier<Vector3f> findPreviousValue(AnimationValue value) {
        var fromValue = value.getFromValue();
        if (fromValue != null) {
            return fromValue;
        }
        if (value.getIndex() != 0) {
            var tmp = values.get(value.getIndex() - 1);
            return tmp.getToValue();
        }
        return null;
    }

    protected Vector3f getLastOrDefaultValue(AnimationState state) {
        var lastValue = state.getLastValue(channel);
        if (lastValue != null) {
            return lastValue;
        }
        if (this instanceof Scale) {
            return Vector3f.ONE;
        }
        return Vector3f.ZERO;
    }

    public static class Translation extends AnimationController {

        @Override
        protected void upload(AnimationOutput output, float x, float y, float z) {
            output.translate(x, y, z);
        }
    }

    public static class Rotation extends AnimationController {

        @Override
        protected void upload(AnimationOutput output, float x, float y, float z) {
            output.rotate(x, y, z);
        }
    }

    public static class Scale extends AnimationController {

        @Override
        protected void upload(AnimationOutput output, float x, float y, float z) {
            output.scale(x, y, z);
        }
    }
}
