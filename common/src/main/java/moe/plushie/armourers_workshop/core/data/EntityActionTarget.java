package moe.plushie.armourers_workshop.core.data;

import java.util.List;

public class EntityActionTarget {

    private final float priority;
    private final String name;
    private final List<EntityAction> actions;
    private final int playCount;

    public EntityActionTarget(String name, float priority, List<EntityAction> actions, int playCount) {
        this.priority = priority;
        this.name = name;
        this.actions = actions;
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

    public int getPlayCount() {
        return playCount;
    }
}
