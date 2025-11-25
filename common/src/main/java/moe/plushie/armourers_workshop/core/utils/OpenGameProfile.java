package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.common.IGameProfile;
import moe.plushie.armourers_workshop.api.core.IDataCodec;

import java.util.UUID;

public class OpenGameProfile implements IGameProfile {

    public static IDataCodec<OpenGameProfile> CODEC = IDataCodec.create(instance -> instance.group(ExtraCodecs.UUID.fieldOf("Id").forGetter(OpenGameProfile::id), IDataCodec.STRING.fieldOf("Name").forGetter(OpenGameProfile::name)).apply(instance, OpenGameProfile::new));

    private final UUID id;
    private final String name;

    public OpenGameProfile(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", name, id);
    }

    public boolean isResolvable() {
        return false;
    }
}
