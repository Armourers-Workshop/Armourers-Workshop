package moe.plushie.armourers_workshop.core.client.animation;

public enum AnimatedOutputMode {

    PRE_MAIN(-100, false), MAIN(0, false), POST_MAIN(100, true);

    private final int priority;
    private final boolean isMixMode;

    AnimatedOutputMode(int priority, boolean isMixMode) {
        this.priority = priority;
        this.isMixMode = isMixMode;
    }

    public int priority() {
        return priority;
    }

    public boolean isMixMode() {
        return isMixMode;
    }
}
