package moe.plushie.armourers_workshop.compat.client.sound;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

@Available("[16, )")
public class AbstractSimpleSound {

    private final OpenResourceKey id;
    private final String name;

    private final float volume = 1.0f;
    private final float pitch = 1.0f;
    private final int weight = 1; // must > 0
    private final int attenuationDistance = 16;

    public AbstractSimpleSound(OpenResourceKey id, String name) {
        this.id = id;
        this.name = name;
    }

    public static AbstractSimpleSound create(OpenResourceKey id, String name) {
        return new AbstractSimpleSound(id, name);
    }

    public OpenResourceKey id() {
        return id;
    }

    public String name() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return null;
    }

    public float volume() {
        return volume;
    }

    public float pitch() {
        return pitch;
    }

    public int weight() {
        return weight;
    }

    public int attenuationDistance() {
        return attenuationDistance;
    }
}
