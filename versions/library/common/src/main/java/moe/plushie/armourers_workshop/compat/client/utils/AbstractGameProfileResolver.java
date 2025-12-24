package moe.plushie.armourers_workshop.compat.client.utils;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

@Available("[1.21, 1.26)")
public class AbstractGameProfileResolver {

    public void resolve(OpenGameProfile profile, IResultHandler<OpenGameProfile> handler) {
        SkullBlockEntity.fetchGameProfile(profile.name()).thenAcceptAsync(result -> {
            handler.accept(result.map(AbstractGameProfile::wrap).orElse(profile));
        }).exceptionallyAsync((exp) -> {
            handler.abort(new RuntimeException(exp));
            return null;
        });

    }
}
