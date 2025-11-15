package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.client.resources.PlayerSkin;

import java.util.IdentityHashMap;

@Available("[1.21, 1.22)")
public class AbstractPlayerSkinImpl {

    private static final IdentityHashMap<PlayerSkin, AbstractPlayerSkin> CACHES = new IdentityHashMap<>();

    public static AbstractPlayerSkin of(PlayerSkin skin) {
        return CACHES.computeIfAbsent(skin, it -> {
            var body = Objects.flatMap(it.texture(), OpenResourceLocation::of);
            var cape = Objects.flatMap(it.capeTexture(), OpenResourceLocation::of);
            var elytra = Objects.flatMap(it.elytraTexture(), OpenResourceLocation::of);
            var model = EntityTextureDescriptor.Model.SLIM;
            if (it.model() == PlayerSkin.Model.WIDE) {
                model = EntityTextureDescriptor.Model.WIDE;
            }
            return new AbstractPlayerSkin(body, cape, elytra, model);
        });
    }
}
