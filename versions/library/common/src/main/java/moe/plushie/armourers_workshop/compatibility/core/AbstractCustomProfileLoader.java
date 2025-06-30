package moe.plushie.armourers_workshop.compatibility.core;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

@Available("[1.21, )")
public class AbstractCustomProfileLoader {

    public static void load(GameProfile profile, IResultHandler<GameProfile> handler) {
        SkullBlockEntity.fetchGameProfile(profile.getName()).thenAcceptAsync(result -> {
            handler.accept(result.orElse(profile));
        });
    }
}
