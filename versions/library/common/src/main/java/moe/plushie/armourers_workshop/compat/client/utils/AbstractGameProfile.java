package moe.plushie.armourers_workshop.compat.client.utils;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IGameProfile;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;

@Available("[1.16, 1.26)")
public class AbstractGameProfile extends OpenGameProfile {

    protected final GameProfile profile;

    public AbstractGameProfile(GameProfile profile) {
        super(profile.getId(), profile.getName());
        this.profile = profile;
    }

    public static OpenGameProfile wrap(GameProfile profile) {
        return new AbstractGameProfile(profile);
    }

    public static GameProfile unwrap(IGameProfile profile) {
        if (profile instanceof AbstractGameProfile profile1) {
            return profile1.profile;
        }
        return new GameProfile(profile.id(), profile.name());
    }


    public GameProfile profile() {
        return profile;
    }

    @Override
    public boolean isResolvable() {
        return !profile.getProperties().isEmpty();
    }
}
