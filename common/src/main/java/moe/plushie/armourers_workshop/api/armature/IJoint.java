package moe.plushie.armourers_workshop.api.armature;

import org.jetbrains.annotations.Nullable;

public interface IJoint {

    int getId();

    String getName();

    @Nullable
    IJoint getParent();
}
