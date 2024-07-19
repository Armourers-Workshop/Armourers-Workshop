package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.init.ModDebugger;

public class AnimationState {

    private float startTime;
    private float startTime0;
    private final float duration;

    private int playCount;

    private final boolean requiresVirtualMachine;

    public AnimationState(AnimationController animationController) {
        this.duration = animationController.getDuration();
        this.requiresVirtualMachine = animationController.isRequiresVirtualMachine();
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

    public float getPartialTicks(float animationTicks) {
        float offset = animationTicks - startTime0;
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
}
