package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkin;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinModel;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinPart;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.client.Minecraft;

@Available("[1.21, 1.22)")
public class AbstractPlayerSkinResolver {

    public void resolve(OpenGameProfile profile, IResultHandler<PlayerSkin> handler) {
        Minecraft.getInstance().getSkinManager().getOrLoad(AbstractGameProfile.unwrap(profile)).thenAcceptAsync(skin -> {
            var body = Objects.flatMap(skin.texture(), it -> new PlayerSkinPart(OpenResourceLocation.of(it), skin.textureUrl()));
            var cape = Objects.flatMap(skin.capeTexture(), it -> new PlayerSkinPart(OpenResourceLocation.of(it)));
            var elytra = Objects.flatMap(skin.elytraTexture(), it -> new PlayerSkinPart(OpenResourceLocation.of(it)));
            var model = switch (skin.model()) {
                case WIDE -> PlayerSkinModel.WIDE;
                case SLIM -> PlayerSkinModel.SLIM;
            };
            var descriptor = PlayerSkinDescriptor.fromProfile(profile);
            handler.accept(new PlayerSkin(descriptor.withModel(model), body, cape, elytra, model));
        }).exceptionallyAsync(exception -> {
            handler.abort(new RuntimeException(exception));
            return null;
        });
    }
}


