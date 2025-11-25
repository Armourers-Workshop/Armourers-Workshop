package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkin;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import org.jetbrains.annotations.Nullable;

public class BakedPlayerSkin {

    private final int id = OpenRandomSource.nextInt(BakedPlayerSkin.class);

    private final BakedPlayerSkinPart body;
    private final BakedPlayerSkinPart cape;
    private final BakedPlayerSkinPart elytra;

    public BakedPlayerSkin(PlayerSkin skin, BakedPlayerSkinPart body, BakedPlayerSkinPart cape, BakedPlayerSkinPart elytra) {
        this.body = body;
        this.cape = cape;
        this.elytra = elytra;
    }

    public BakedPlayerSkinPart body() {
        return body;
    }

    @Nullable
    public BakedPlayerSkinPart cape() {
        return cape;
    }

    @Nullable
    public BakedPlayerSkinPart elytra() {
        return elytra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BakedPlayerSkin that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
