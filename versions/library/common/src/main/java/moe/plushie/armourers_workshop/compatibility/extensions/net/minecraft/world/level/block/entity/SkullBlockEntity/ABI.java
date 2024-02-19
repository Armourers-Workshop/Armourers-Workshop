package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.level.block.entity.SkullBlockEntity;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.Optional;
import java.util.function.Consumer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.21, )")
public class ABI {

    public static void loadCustomProfile(@ThisClass Class<?> clazz, GameProfile profile, Consumer<Optional<GameProfile>> handler) {
        SkullBlockEntity.fetchGameProfile(profile.getName()).thenAcceptAsync(handler);
    }
}
