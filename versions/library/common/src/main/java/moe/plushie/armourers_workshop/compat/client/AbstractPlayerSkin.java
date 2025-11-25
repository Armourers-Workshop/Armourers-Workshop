package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkin;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinModel;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.client.player.AbstractClientPlayer;

import java.util.IdentityHashMap;

@Available("[1.21, 1.22)")
public class AbstractPlayerSkin {

    private static final IdentityHashMap<Object, PlayerSkin> CACHES = new IdentityHashMap<>();

    public static PlayerSkin by(AbstractClientPlayer player) {
        var skin = player.getSkin();
        var result = CACHES.get(skin);
        if (result != null) {
            return result;
        }
        var body = Objects.flatMap(skin.texture(), OpenResourceLocation::of);
        var cape = Objects.flatMap(skin.capeTexture(), OpenResourceLocation::of);
        var elytra = Objects.flatMap(skin.elytraTexture(), OpenResourceLocation::of);
        var model = switch (skin.model()) {
            case SLIM -> PlayerSkinModel.SLIM;
            case WIDE -> PlayerSkinModel.WIDE;
        };
        result = new PlayerSkin(body, cape, elytra, model);
        CACHES.put(skin, result);
        return result;
    }
}
