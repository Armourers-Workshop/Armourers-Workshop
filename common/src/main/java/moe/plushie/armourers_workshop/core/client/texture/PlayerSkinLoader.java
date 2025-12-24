package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.compat.client.texture.AbstractPlayerSkin;
import moe.plushie.armourers_workshop.core.data.LoadableHashMap;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkin;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinModel;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;
import moe.plushie.armourers_workshop.core.utils.TrackableResultHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class PlayerSkinLoader {

    private static final UUID NIL_UUID = new UUID(0, 0);
    private static final PlayerSkinLoader INSTANCE = new PlayerSkinLoader();

    private final LoadableHashMap<PlayerSkinDescriptor, PlayerSkin> allSkins = new LoadableHashMap<>(this::loadSkin);

    private final LoadableHashMap<String, OpenGameProfile> namedProfiles = new LoadableHashMap<>(this::resolveGameProfile);
    private final LoadableHashMap<PlayerSkinDescriptor, PlayerSkin> namedSkins = new LoadableHashMap<>(this::resolveSkin);

    private final PlayerSkinDownloader downloader = new PlayerSkinDownloader();

    public static PlayerSkinLoader getInstance() {
        return INSTANCE;
    }

    public OpenGameProfile getGameProfile(PlayerSkinDescriptor descriptor) {
        var profile = descriptor.profile();
        if (profile != null) {
            return profile;
        }
        var name = descriptor.name();
        if (name != null) {
            return namedProfiles.getOrCreate(name).get();
        }
        return null;
    }

    public PlayerSkin loadSkin(Entity entity) {
        if (entity instanceof MannequinEntity mannequin) {
            return loadSkin(mannequin.getTextureDescriptor());
        }
        if (entity instanceof AbstractClientPlayer player) {
            return AbstractPlayerSkin.by(player);
        }
        return PlayerSkin.DEFAULT;
    }

    public PlayerSkin loadSkin(PlayerSkinDescriptor descriptor) {
        var skin = allSkins.getOrCreate(descriptor).get();
        if (skin != null) {
            return skin;
        }
        return PlayerSkin.DEFAULT;
    }

    public void loadSkin(PlayerSkinDescriptor descriptor, IResultHandler<PlayerSkin> handler) {
        allSkins.getOrCreate(descriptor).listen(handler);
    }

    private void loadSkin(PlayerSkinDescriptor descriptor, LoadableHashMap.Entry<PlayerSkin> task) {
        // always load only the wide version.
        namedSkins.getOrCreate(descriptor.withModel(PlayerSkinModel.WIDE)).listen((skin, exception) -> {
            if (skin != null) {
                skin = skin.withModel(descriptor.model());
            }
            task.apply(skin, exception);
        });
    }

    private void resolveSkin(PlayerSkinDescriptor descriptor, LoadableHashMap.Entry<PlayerSkin> task) {
        // ignore empty descriptor.
        if (descriptor.isEmpty()) {
            task.accept(PlayerSkin.DEFAULT);
            return;
        }
        // load from url
        var url = descriptor.url();
        if (url != null) {
            try {
                var ignored = new URL(url);
                resolveSkinWithURL(url, task);
            } catch (MalformedURLException e) {
                task.abort(new RuntimeException("invalid url: " + url, e));
            }
            return;
        }
        // load from username.
        var name = descriptor.name();
        if (name != null) {
            resolveGameProfile(name, (profile, exception) -> {
                if (profile == null) {
                    task.abort(new RuntimeException("invalid user: " + name, exception));
                    return;
                }
                resolveSkinWithProfile(profile, task);
            });
            return;
        }
        // load from profile.
        var profile = descriptor.profile();
        if (profile != null) {
            resolveSkinWithProfile(profile, task);
            return;
        }
        // ignore
        task.abort(new RuntimeException("invalid descriptor: " + descriptor));
    }

    private void resolveSkinWithURL(String url, LoadableHashMap.Entry<PlayerSkin> task) {
        // we need wait owner complete.
        var owner = namedSkins.getOrCreate(PlayerSkinDescriptor.fromURL(url));
        if (owner != task) {
            owner.listen(task);
            return;
        }
        var handler = new TrackableResultHandler<>("download network texture", url, task);
        downloader.downloadSkin(url, handler);
    }

    private void resolveSkinWithProfile(OpenGameProfile profile, LoadableHashMap.Entry<PlayerSkin> task) {
        // we need wait owner complete.
        var owner = namedSkins.getOrCreate(PlayerSkinDescriptor.fromProfile(profile));
        if (owner != task) {
            owner.listen(task);
            return;
        }
        var handler = new TrackableResultHandler<>("download vanilla texture", profile.name(), task);
        downloader.downloadSkin(profile, handler);
    }

    private void resolveGameProfile(String name, IResultHandler<OpenGameProfile> handler) {
        namedProfiles.getOrCreate(name).listen(handler);
    }

    private void resolveGameProfile(String name, LoadableHashMap.Entry<OpenGameProfile> task) {
        var handler = new TrackableResultHandler<>("download game profile", name, task);
        downloader.downloadProfile(new OpenGameProfile(NIL_UUID, name), handler);
    }
}
