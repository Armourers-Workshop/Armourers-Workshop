package moe.plushie.armourers_workshop.core.data.action;

import java.util.ArrayList;
import java.util.List;

public class EntityActionTarget {

    private final float priority;
    private final String name;
    private final List<EntityAction> actions;
    private final double transitionDuration;
    private final int playCount;

    public EntityActionTarget(String name, float priority, List<EntityAction> actions, double transitionDuration, int playCount) {
        this.priority = priority;
        this.name = name;
        this.actions = actions;
        this.transitionDuration = transitionDuration;
        this.playCount = playCount;
    }

    public String getName() {
        return name;
    }

    public float getPriority() {
        return priority;
    }

    public List<EntityAction> getActions() {
        return actions;
    }

    public double getTransitionDuration() {
        return transitionDuration;
    }

    public int getPlayCount() {
        return playCount;
    }

    public static class Builder {

        private ArrayList<EntityAction> actions = new ArrayList<>();
        private float priority = 0;
        private double transitionDuration = 0.1;
        private int playCount = 0;

        public Builder action(EntityAction action) {
            this.actions.add(action);
            return this;
        }

        public Builder priority(float priority) {
            this.priority = priority;
            return this;
        }

        public Builder repeat(int playCount) {
            this.playCount = playCount;
            return this;
        }

        public Builder transition(double transitionDuration) {
            this.transitionDuration = transitionDuration;
            return this;
        }

        public EntityActionTarget build(String name) {
            return new EntityActionTarget(name, priority, actions, transitionDuration, playCount);
        }
    }
}
