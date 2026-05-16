package moe.plushie.armourers_workshop.compat.client.sound;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

@Available("[16, )")
public class AbstractSimpleSound {

    private final OpenResourceKey id;
    private final String name;

    private final float volume;
    private final float pitch;
    private final int weight = 1; // must > 0
    private final int attenuationDistance;

    public AbstractSimpleSound(OpenResourceKey id, String name, float volume, float pitch, int attenuationDistance) {
        this.id = id;
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
        this.attenuationDistance = attenuationDistance;
    }

    public static AbstractSimpleSound create(OpenResourceKey id, String name, float volume, float pitch, int attenuationDistance) {
        return new AbstractSimpleSound(id, name, volume, pitch, attenuationDistance);
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
