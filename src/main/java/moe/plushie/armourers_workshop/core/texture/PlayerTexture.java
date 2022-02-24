package moe.plushie.armourers_workshop.core.texture;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.ResourceLocation;

public class PlayerTexture {

    public static final PlayerTexture DEFAULT = new PlayerTexture(null, null, null);

    private final GameProfile profile;
    private final ResourceLocation location;
    private final MinecraftProfileTexture texture;

    public PlayerTexture(GameProfile profile, ResourceLocation location, MinecraftProfileTexture texture) {
        this.profile = profile;
        this.location = location;
        this.texture = texture;
    }

    public ResourceLocation getLocation() {
        return location;
    }
}
