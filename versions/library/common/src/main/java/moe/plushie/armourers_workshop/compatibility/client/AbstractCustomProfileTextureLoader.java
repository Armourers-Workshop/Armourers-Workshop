package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Available("[1.21, )")
@Environment(EnvType.CLIENT)
public class AbstractCustomProfileTextureLoader {

    public static void load(GameProfile profile, ResultHandler handler) {
        var timeout = 30000; // 30s
        load(profile, System.currentTimeMillis() + timeout, handler);
    }

    private static void load(GameProfile profile, long endTime, ResultHandler handler) {
        EnvironmentManager.getClient().getSkinManager().getOrLoad(profile).thenAcceptAsync(skin -> {
            var url = skin.textureUrl();
            var location = OpenResourceLocation.create(skin.texture());
            var modelType = skin.model().id();
            // when this is a unknown user, it only call back a placeholder result.
            if (profile.getProperties().isEmpty()) {
                handler.accept(location, url, modelType);
                return;
            }
            // in some cases will get a placeholder result (url is null),
            // this means the game profile still loading phase,
            // we need to try again in the request valid time.
            if (url == null && System.currentTimeMillis() < endTime) {
                Executors.sleep(500); // 500ms
                load(profile, endTime, handler);
                return;
            }
            handler.accept(location, url, modelType);
        });
    }

    public interface ResultHandler {

        void accept(OpenResourceLocation location, String url, String modelType);
    }
}
