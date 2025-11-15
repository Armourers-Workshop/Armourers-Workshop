package moe.plushie.armourers_workshop.core.client.texture;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.AbstractRemoteTextureData;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
public class EntityTextureDownloader {

    private static final ScheduledExecutorService TIMER = Executors.newSingleThreadScheduledExecutor();

    public static CompletableFuture<EntityTexture> downloadAndRegisterSkin(EntityTextureDescriptor descriptor) {
        var future = new CompletableFuture<EntityTexture>();
        downloadAndRegisterSkin(descriptor, 0, ((location, url, modelType) -> {
            var texture = new EntityTexture(descriptor, location, url, getModelType(modelType));
            var textureData = AbstractRemoteTextureData.ofNullable(url);
            if (textureData != null) {
                texture.setImage(textureData.data());
            }
            future.complete(texture);
        }));
        return future;
    }

    private static void downloadAndRegisterSkin(EntityTextureDescriptor descriptor, int retryCount, Callback callback) {
        if (descriptor.profile() != null) {
            downloadAndRegisterSkin(descriptor.profile(), retryCount, callback);
        } else if (descriptor.url() != null) {
            downloadAndRegisterSkin(descriptor.url(), retryCount, callback);
        } else {
            callback.apply(ModTextures.MANNEQUIN_DEFAULT, null, null);
        }
    }


    private static void downloadAndRegisterSkin(GameProfile profile, int retryCount, Callback callback) {
        Minecraft.getInstance().getSkinManager().downloadAndRegisterSkin(profile, (location, url, modelType) -> {
            // when this is an unknown user, it only calls back a placeholder result.
            if (profile.properties().isEmpty()) {
                callback.apply(location, url, modelType);
                return;
            }
            // in some cases will get a placeholder result (url is null),
            // this means the game profile still loading phase.
            if (url == null && retryCount < 3) {
                TIMER.schedule(() -> downloadAndRegisterSkin(profile, retryCount + 1, callback), 500, TimeUnit.MILLISECONDS);
                return;
            }
            callback.apply(location, url, modelType);
        });
    }


    private static void downloadAndRegisterSkin(String url, int retryCount, Callback callback) {
        var identifier = Objects.md5(url);
        var location = OpenResourceLocation.parse("skins/aw-" + identifier);
        var file = new File(EnvironmentManager.getRootDirectory() + "/skin-textures/" + identifier.substring(0, 2) + "/" + identifier);
        Minecraft.getInstance().getSkinManager().downloadAndRegisterSkin(url, true, file, location, ModTextures.MANNEQUIN_DEFAULT, callback);
    }

    private static EntityTextureDescriptor.Model getModelType(String name) {
        if (Objects.equals(name, "slim")) {
            return EntityTextureDescriptor.Model.SLIM;
        }
        return EntityTextureDescriptor.Model.WIDE;
    }

    public interface Callback {

        void apply(OpenResourceLocation location, String url, String modelType);
    }
}
