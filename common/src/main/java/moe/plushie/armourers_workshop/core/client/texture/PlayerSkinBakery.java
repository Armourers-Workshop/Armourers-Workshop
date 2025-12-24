package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.texture.AbstractImageTextureDownloader;
import moe.plushie.armourers_workshop.core.client.bake.BakedPlayerSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedPlayerSkinPart;
import moe.plushie.armourers_workshop.core.data.LoadableHashMap;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkin;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.TrackableResultHandler;
import moe.plushie.armourers_workshop.init.ModLog;

import java.awt.image.BufferedImage;

@OnlyIn(Dist.CLIENT)
public class PlayerSkinBakery {

    private static final PlayerSkinBakery EMPTY = new PlayerSkinBakery();
    private static PlayerSkinBakery BAKERY;

    private final LoadableHashMap<PlayerSkin, BakedPlayerSkin> skins = new LoadableHashMap<>(this::bakeSkin);
    private final LoadableHashMap<OpenResourceLocation, BufferedImage> images = new LoadableHashMap<>(this::bakeImage);

    private final AbstractImageTextureDownloader textureDownloader = new AbstractImageTextureDownloader();

    public static PlayerSkinBakery getInstance() {
        if (BAKERY != null) {
            return BAKERY;
        }
        return EMPTY;
    }

    public static void start() {
        if (BAKERY == null) {
            BAKERY = new PlayerSkinBakery();
            ModLog.debug("start player skin bakery");
        }
    }

    public static void stop() {
        if (BAKERY != null) {
            BAKERY.images.clear();
            BAKERY.skins.clear();
            BAKERY = null;
            ModLog.debug("stop player skin bakery");
        }
    }

    public BakedPlayerSkin loadSkin(PlayerSkinDescriptor descriptor) {
        var skin = PlayerSkinLoader.getInstance().loadSkin(descriptor);
        return loadSkin(skin);
    }

    public BakedPlayerSkin loadSkin(PlayerSkin skin) {
        return skins.getOrCreate(skin).get();
    }

    private void bakeSkin(PlayerSkin skin, LoadableHashMap.Entry<BakedPlayerSkin> task) {
        images.getOrCreate(skin.body().texture()).listen((image, exception) -> {
            if (exception != null) {
                task.abort(exception);
                return;
            }
            task.accept(new BakedPlayerSkin(skin, new BakedPlayerSkinPart(image), null, null));
        });
    }

    private void bakeImage(OpenResourceLocation location, LoadableHashMap.Entry<BufferedImage> task) {
        var handler = new TrackableResultHandler<>("download GPU texture ", location, task);
        textureDownloader.download(location, handler);
    }
}
