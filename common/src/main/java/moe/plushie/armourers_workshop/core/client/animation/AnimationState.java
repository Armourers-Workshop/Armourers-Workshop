package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationFunction;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class AnimationState {

    private float startTime;
    private float startTime0;
    private final float duration;

    private int playCount;

    private final Frame[] frames;
    private final Vector3f[] lastValues;

    private final boolean requiresVirtualMachine;

    public AnimationState(BakedSkinAnimation animation) {
        this.duration = animation.getDuration();
        this.frames = new Frame[animation.getChannels()];
        this.lastValues = new Vector3f[animation.getChannels()];
        this.requiresVirtualMachine = animation.getProcessors().stream().anyMatch(AnimationProcessor::isRequiresVirtualMachine);
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
        this.startTime0 = startTime;
    }

    public float getStartTime() {
        return startTime;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setFrame(int channel, Frame frame) {
        frames[channel] = frame;
    }

    public Frame getFrame(int channel) {
        return frames[channel];
    }

    public void setLastValues(int channel, Vector3f value) {
        setLastValues(channel, value.getX(), value.getY(), value.getZ());
    }

    public void setLastValues(int channel, float x, float y, float z) {
        var value = lastValues[channel];
        if (value == null) {
            value = new Vector3f();
            lastValues[channel] = value;
        }
        value.set(x, y, z);
    }

    public Vector3f getLastValue(int channel) {
        return lastValues[channel];
    }

    public float getPartialTicks(float animationTicks) {
        float offset = animationTicks - startTime0;
        offset *= ModDebugger.animationSpeed;
        if (playCount == 0) {
            return offset;
        }
        // 0 -> duration / 0 -> duration ...
        if (offset > duration) {
            if (playCount > 0) {
                playCount -= 1;
            }
            if (playCount != 0) { // reset
                offset -= duration;
                startTime0 = animationTicks - offset;
            }
        }
        return offset;
    }

    public boolean isCompleted(float animationTicks) {
        return playCount == 0 && getPartialTicks(animationTicks) > this.duration;
    }

    public boolean isRequiresVirtualMachine() {
        return requiresVirtualMachine;
    }

    public static class Frame {

        private final float time;
        private final float duration;

        private final AnimationValue value;

        private Vector3f fromValue = Vector3f.ZERO;
        private Vector3f toValue = Vector3f.ZERO;

        public Frame(AnimationValue value) {
            this.value = value;
            this.time = value.getTime();
            this.duration = value.getDuration();
        }

        public boolean contains(float time) {
            return value.contains(time);

        }

        public int getIndex() {
            return value.getIndex();
        }

        public float getTime() {
            return time;
        }

        public float getDuration() {
            return duration;
        }

        public SkinAnimationFunction getFunction() {
            return value.getFunction();
        }

        public AnimationValue getValue() {
            return value;
        }

        public void setFromValue(Vector3f fromValue) {
            this.fromValue = fromValue;
        }

        public Vector3f getFromValue() {
            return fromValue;
        }

        public void setToValue(Vector3f toValue) {
            this.toValue = toValue;
        }

        public Vector3f getToValue() {
            return toValue;
        }
    }

}
