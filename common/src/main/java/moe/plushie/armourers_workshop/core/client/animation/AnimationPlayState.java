package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.HashMap;
import java.util.Map;

public abstract class AnimationPlayState {

    protected double time = 0.0;
    protected double duration = 0.0;

    protected int loopCount = 0;

    protected double adjustedTime = 0.0;
    protected boolean isCompleted = false;

    protected final Map<String, AnimationEffectState> effects = new HashMap<>();

    public static AnimationPlayState create(double time, int loopCount, double speed, AnimationController controller) {
        // ..
        if (loopCount == 0) {
            loopCount = switch (controller.loop()) {
                case NONE -> 1;
                case LAST_FRAME -> 0;
                case LOOP -> -1;
            };
        }
        // ..
        var playState = createVariant(speed, controller.duration());
        playState.setTime(time);
        playState.setDuration(controller.duration());
        playState.setLoopCount(loopCount);
        return playState;
    }

    private static AnimationPlayState createVariant(double speed, double duration) {
        // if speed or duration is zero, this means no need adjusted time.
        if (Double.compare(speed * duration, 0.0) == 0) {
            return new None();
        }
        // If speed is one, this means no need speed adjust, we will use better performing version.
        if (Double.compare(speed, 1.0) == 0) {
            return new Normal();
        }
        return new Modulate(speed);
    }

    protected boolean update(double animationTime) {
        return false;
    }

    protected void reset() {
        effects.forEach((name, effectState) -> effectState.reset());
    }

    public void tick(double animationTime) {
        // when loop count is 0 (keep last frame), we never call reset again.
        var working = update(animationTime);
        if (working || loopCount == 0) {
            return;
        }
        // when loop count > 0, we will reduce it until to 0, and then keep last frame.
        // when loop count < 0, we never reduce it, because it is infinite.
        if (loopCount > 0) {
            loopCount -= 1;
        }
        // when loop count is manual reduced to 0, it means the animation complete.
        if (loopCount == 0) {
            isCompleted = true;
        }
        reset();
    }

    public double adjustedTime(double animationTime) {
        // this is a future animation?
        if (animationTime < time) {
            return 0;
        }
        tick(animationTime);
        return adjustedTime;
    }


    public void setTime(double time) {
        this.time = time;
    }

    public double time() {
        return time;
    }

    public double adjustedTime() {
        return adjustedTime;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double duration() {
        return duration;
    }

    public void setLoopCount(int playCount) {
        this.loopCount = playCount;
    }

    public int loopCount() {
        return loopCount;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public AnimationEffectState effectByName(String name) {
        return effects.computeIfAbsent(name, AnimationEffectState::new);
    }

    @Override
    public String toString() {
        return Objects.toString(this, "time", time, "duration", duration, "loop", loopCount, "completed", isCompleted);
    }


    private static class None extends AnimationPlayState {
    }

    private static class Normal extends AnimationPlayState {

        private double lastResetTime = 0.0;

        @Override
        protected boolean update(double animationTime) {
            adjustedTime = animationTime - lastResetTime;
            return adjustedTime < duration;
        }

        @Override
        protected void reset() {
            super.reset();
            if (loopCount != 0) {
                lastResetTime += duration;
            }
        }

        @Override
        public void setTime(double time) {
            this.time = time;
            this.lastResetTime = time;
        }
    }

    private static class Modulate extends AnimationPlayState {

        private double lastResetTime = 0.0;
        private double adjustedDuration = 0.0;

        private final double speed;

        public Modulate(double speed) {
            this.speed = speed;
        }

        @Override
        protected boolean update(double animationTime) {
            // convert time to progress and then remap to duration.
            var progress = (animationTime - lastResetTime) / adjustedDuration;
            adjustedTime = progress * duration;
            return progress < 1.0;
        }

        @Override
        protected void reset() {
            super.reset();
            if (loopCount != 0) {
                lastResetTime += adjustedDuration;
            }
        }

        @Override
        public void setTime(double time) {
            this.time = time;
            this.lastResetTime = time;
        }

        @Override
        public void setDuration(double duration) {
            this.duration = duration;
            this.adjustedDuration = Math.max(duration / speed, 0.00001);
        }
    }
}
