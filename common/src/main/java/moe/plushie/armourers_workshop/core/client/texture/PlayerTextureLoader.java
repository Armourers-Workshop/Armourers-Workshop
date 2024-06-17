package moe.plushie.armourers_workshop.core.client.texture;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.TextureUtils;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class PlayerTextureLoader {

    public static final IResourceLocation STEVE_SKIN_LOCATION = OpenResourceLocation.parse("textures/entity/steve.png");
    public static final IResourceLocation ALEX_SKIN_LOCATION = OpenResourceLocation.parse("textures/entity/alex.png");

    private static final PlayerTextureLoader LOADER = new PlayerTextureLoader();

    private final HashMap<String, Optional<GameProfile>> profiles = new HashMap<>();
    private final HashMap<PlayerTextureDescriptor, Optional<PlayerTexture>> resolvedTextures = new HashMap<>();
    private final HashSet<PlayerTextureDescriptor> loadingTextures = new HashSet<>();

    private final HashMap<String, BakedEntityTexture> downloadedTextures = new HashMap<>();
    private final HashMap<IResourceLocation, Optional<PlayerTexture>> bakedTextures = new HashMap<>();

    private final Executor workThread = ThreadUtils.newFixedThreadPool(1, "AW-SKIN/T-LD");

    public static PlayerTextureLoader getInstance() {
        return LOADER;
    }


    public GameProfile getGameProfile(PlayerTextureDescriptor descriptor) {
        var profile = descriptor.getProfile();
        if (profile != null) {
            return profile;
        }
        var name = descriptor.getName();
        if (name != null) {
            var profile1 = profiles.get(name.toLowerCase());
            if (profile1 != null) {
                return profile1.orElse(null);
            }
            loadGameProfileWithName(name, null);
        }
        return null;
    }

    @Nullable
    public BakedEntityTexture getTextureModel(IResourceLocation location) {
        if (location == null) {
            return null;
        }
        var texture = bakedTextures.get(location);
        if (texture != null) {
            return texture.map(PlayerTexture::getTexture).orElse(null);
        }
        loadDefaultTexture(location);
        return null;
    }

    public IResourceLocation getTextureLocation(Entity entity) {
        if (entity instanceof MannequinEntity) {
            var descriptor = entity.getEntityData().get(MannequinEntity.DATA_TEXTURE);
            var texture = loadTexture(descriptor);
            if (texture != null && texture.getLocation() != null) {
                return texture.getLocation();
            }
        }
        return TextureUtils.getTexture(entity);
    }

    public IResourceLocation loadTextureLocation(PlayerTextureDescriptor descriptor) {
        if (!descriptor.isEmpty()) {
            var texture1 = loadTexture(descriptor);
            if (texture1 != null) {
                return texture1.getLocation();
            }
        }
        return ModTextures.MANNEQUIN_DEFAULT;
    }

    @Nullable
    public PlayerTexture loadTexture(PlayerTextureDescriptor descriptor) {
        if (descriptor.isEmpty()) {
            return null;
        }
        var texture = resolvedTextures.get(descriptor);
        if (texture != null && texture.isPresent()) {
            return texture.orElse(null);
        }
        loadTextureDescriptor(descriptor, descriptor1 -> {
        });
        return null;
    }

    public void loadTextureDescriptor(PlayerTextureDescriptor descriptor, Consumer<Optional<PlayerTextureDescriptor>> complete) {
        var texture = resolvedTextures.get(descriptor);
        if (texture != null || descriptor.isEmpty() || !loadingTextures.add(descriptor)) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        // load from url
        var url = descriptor.getURL();
        if (url != null) {
            try {
                URL ignored = new URL(url);
                loadCustomTextureWithURL(url, resolvedDescriptor -> {
                    loadingTextures.remove(descriptor);
                    complete.accept(resolvedDescriptor);
                });
            } catch (MalformedURLException e) {
                ModLog.info("input a invalid url '{}'", url);
                complete.accept(Optional.empty());
            }
            return;
        }
        // load from profile.
        var profile = descriptor.getProfile();
        if (profile != null) {
            loadCustomTextureWithProfile(profile, resolvedDescriptor -> {
                loadingTextures.remove(descriptor);
                complete.accept(resolvedDescriptor);
            });
            return;
        }
        // load from username.
        var name = descriptor.getName();
        if (name != null) {
            loadCustomTextureWithName(name, resolvedDescriptor -> {
                loadingTextures.remove(descriptor);
                complete.accept(resolvedDescriptor);
            });
            return;
        }
        // ignore
        if (complete != null) {
            complete.accept(Optional.empty());
        }
    }

    private void loadGameProfileWithName(String name, @Nullable Consumer<Optional<GameProfile>> complete) {
        var key = name.toLowerCase();
        if (profiles.containsKey(key)) {
            if (complete != null) {
                complete.accept(profiles.get(key));
            }
            return;
        }
        profiles.put(key, Optional.empty());
        workThread.execute(() -> SkullBlockEntity.loadCustomProfile(new GameProfile(PlayerTextureDescriptor.NIL_UUID, name), resolvedProfile -> {
            // when an exception occurs, we need to remove and later retry.
            if (resolvedProfile.isPresent()) {
                profiles.put(name, resolvedProfile);
            } else {
                profiles.remove(name);
            }
            if (complete != null) {
                complete.accept(resolvedProfile);
            }
        }));
    }

    public void loadDefaultTexture(IResourceLocation location) {
        boolean alex = location.equals(ALEX_SKIN_LOCATION);
        if (!alex && !location.equals(STEVE_SKIN_LOCATION)) {
            return; // not steve and alex
        }
        bakedTextures.put(location, Optional.empty());
        workThread.execute(() -> {
            PlayerTexture resolvedTexture = new PlayerTexture("", location, null);
            resolvedTexture.setTexture(new BakedEntityTexture(location, alex));
            bakedTextures.put(location, Optional.of(resolvedTexture));
        });
    }

    private void loadCustomTextureWithName(String name, Consumer<Optional<PlayerTextureDescriptor>> complete) {
        loadGameProfileWithName(name, resolvedProfile -> {
            if (resolvedProfile.isPresent()) {
                loadCustomTextureWithProfile(resolvedProfile.get(), complete);
            } else {
                complete.accept(Optional.empty());
            }
        });
    }

    private void loadCustomTextureWithProfile(GameProfile profile, Consumer<Optional<PlayerTextureDescriptor>> complete) {
        var descriptor = PlayerTextureDescriptor.fromProfile(profile);
        var texture = resolvedTextures.get(descriptor);
        if (texture != null) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        ModLog.debug("load player texture => {}", profile);
        Minecraft.getInstance().getSkinManager().loadCustomSkin(profile, skin -> {
            var url = skin.textureUrl();
            var model = skin.model().id();
            var location = OpenResourceLocation.create(skin.texture());
            ModLog.debug("receive player texture from vanilla loader => {}", location);
            receivePlayerTexture(descriptor, location, url, model);
            complete.accept(Optional.of(descriptor));
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private void loadCustomTextureWithURL(String url, Consumer<Optional<PlayerTextureDescriptor>> complete) {
        var descriptor = PlayerTextureDescriptor.fromURL(url);
        var texture = resolvedTextures.get(descriptor);
        if (texture != null) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        ModLog.debug("load player texture => {}", url);
        var identifier = Hashing.sha1().hashUnencodedChars(url).toString();
        var location = OpenResourceLocation.parse("skins/aw-" + identifier);
        var textureManager = Minecraft.getInstance().getTextureManager();
        var processingTexture = textureManager.getTexture(location.toLocation(), null);
        if (processingTexture != null) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        var sub = identifier.length() > 2 ? identifier.substring(0, 2) : "xx";
        var path = new File(EnvironmentManager.getRootDirectory() + "/skin-textures/" + sub + "/" + identifier);
        var downloadingTexture = new HttpTexture(path, url, ModTextures.MANNEQUIN_DEFAULT.toLocation(), true, () -> {
            ModLog.debug("receive player texture from custom loader => {}", location);
            receivePlayerTexture(descriptor, location, url, null);
            complete.accept(Optional.of(descriptor));
        });
        textureManager.register(location.toLocation(), downloadingTexture);
    }

    public void receivePlayerTexture(String url, NativeImage image, boolean slim) {
        if (image == null) {
            return;
        }
        var newImage = new NativeImage(image.format(), image.getWidth(), image.getHeight(), true);
        newImage.copyFrom(image);
        workThread.execute(() -> {
            var bakedTexture = getDownloadedTexture(url);
            if (bakedTexture.getModel() == null) {
                bakedTexture.setModel("default");
                if (slim) {
                    bakedTexture.setModel("slim");
                }
            }
            bakedTexture.loadImage(newImage, Objects.equals(bakedTexture.getModel(), "slim"));
            ModLog.debug("baked a player texture => {}, url: {}, slim: {}", bakedTexture.getResourceLocation(), url, slim);
        });
    }

    private synchronized void receivePlayerTexture(PlayerTextureDescriptor descriptor, IResourceLocation location, String url, String model) {
        var resolvedTexture = new PlayerTexture(url, location, model);
        var bakedTexture = getDownloadedTexture(url);
        bakedTexture.setResourceLocation(location);
        bakedTexture.setModel(model);
        resolvedTexture.setTexture(bakedTexture);
        // setup texture
        resolvedTextures.put(descriptor, Optional.of(resolvedTexture));
        bakedTextures.put(location, Optional.of(resolvedTexture));
        loadingTextures.remove(descriptor);
    }

    private synchronized BakedEntityTexture getDownloadedTexture(String url) {
        return downloadedTextures.computeIfAbsent(url, k -> new BakedEntityTexture());
    }
}
