package moe.plushie.armourers_workshop.core.texture;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
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

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class PlayerTextureLoader {

    public static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
    public static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");

    private static final PlayerTextureLoader LOADER = new PlayerTextureLoader();

    private final HashMap<String, Optional<GameProfile>> profiles = new HashMap<>();
    private final HashMap<PlayerTextureDescriptor, Optional<PlayerTexture>> resolvedTextures = new HashMap<>();
    private final HashSet<PlayerTextureDescriptor> loadingTextures = new HashSet<>();

    private final HashMap<String, BakedEntityTexture> downloadedTextures = new HashMap<>();
    private final HashMap<ResourceLocation, Optional<PlayerTexture>> bakedTextures = new HashMap<>();

    private final Executor workThread = ThreadUtils.newFixedThreadPool(1, "AW-SKIN/T-LD");

    public static PlayerTextureLoader getInstance() {
        return LOADER;
    }


    public GameProfile getGameProfile(PlayerTextureDescriptor descriptor) {
        auto profile = descriptor.getProfile();
        if (profile != null) {
            return profile;
        }
        auto name = descriptor.getName();
        if (name != null) {
            auto profile1 = profiles.get(name.toLowerCase());
            if (profile1 != null) {
                return profile1.orElse(null);
            }
            loadGameProfileWithName(name, null);
        }
        return null;
    }

    @Nullable
    public BakedEntityTexture getTextureModel(ResourceLocation location) {
        if (location == null) {
            return null;
        }
        auto texture = bakedTextures.get(location);
        if (texture != null) {
            return texture.map(PlayerTexture::getTexture).orElse(null);
        }
        loadDefaultTexture(location);
        return null;
    }

    public ResourceLocation getTextureLocation(Entity entity) {
        if (entity instanceof MannequinEntity) {
            PlayerTextureDescriptor descriptor = entity.getEntityData().get(MannequinEntity.DATA_TEXTURE);
            PlayerTexture texture = loadTexture(descriptor);
            if (texture != null && texture.getLocation() != null) {
                return texture.getLocation();
            }
        }
        if (entity instanceof AbstractClientPlayer) {
            return ((AbstractClientPlayer) entity).getSkin().texture();
        }
        return ModTextures.MANNEQUIN_DEFAULT;
    }

    public ResourceLocation loadTextureLocation(PlayerTextureDescriptor descriptor) {
        if (!descriptor.isEmpty()) {
            PlayerTexture texture1 = loadTexture(descriptor);
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
        auto texture = resolvedTextures.get(descriptor);
        if (texture != null && texture.isPresent()) {
            return texture.orElse(null);
        }
        loadTextureDescriptor(descriptor, descriptor1 -> {
        });
        return null;
    }

    public void loadTextureDescriptor(PlayerTextureDescriptor descriptor, Consumer<Optional<PlayerTextureDescriptor>> complete) {
        auto texture = resolvedTextures.get(descriptor);
        if (texture != null || descriptor.isEmpty() || !loadingTextures.add(descriptor)) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        // load from url
        auto url = descriptor.getURL();
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
        auto profile = descriptor.getProfile();
        if (profile != null) {
            loadCustomTextureWithProfile(profile, resolvedDescriptor -> {
                loadingTextures.remove(descriptor);
                complete.accept(resolvedDescriptor);
            });
            return;
        }
        // load from username.
        auto name = descriptor.getName();
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
        auto key = name.toLowerCase();
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

    public void loadDefaultTexture(ResourceLocation location) {
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
        PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.fromProfile(profile);
        Optional<PlayerTexture> texture = resolvedTextures.get(descriptor);
        if (texture != null) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        ModLog.debug("load player texture => {}", profile);
        SkinManager manager = Minecraft.getInstance().getSkinManager();
        manager.loadCustomSkin(profile, skin -> {
            auto url = skin.textureUrl();
            auto model = skin.model().id();
            auto location = skin.texture();
            ModLog.debug("receive player texture from vanilla loader => {}", location);
            receivePlayerTexture(descriptor, location, url, model);
            complete.accept(Optional.of(descriptor));
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private void loadCustomTextureWithURL(String url, Consumer<Optional<PlayerTextureDescriptor>> complete) {
        PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.fromURL(url);
        Optional<PlayerTexture> texture = resolvedTextures.get(descriptor);
        if (texture != null) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        ModLog.debug("load player texture => {}", url);
        String identifier = Hashing.sha1().hashUnencodedChars(url).toString();
        ResourceLocation location = new ResourceLocation("skins/aw-" + identifier);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture processingTexture = textureManager.getTexture(location, null);
        if (processingTexture != null) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        String sub = identifier.length() > 2 ? identifier.substring(0, 2) : "xx";
        File path = new File(EnvironmentManager.getRootDirectory() + "/skin-textures/" + sub + "/" + identifier);
        HttpTexture downloadingTexture = new HttpTexture(path, url, ModTextures.MANNEQUIN_DEFAULT, true, () -> {
            ModLog.debug("receive player texture from custom loader => {}", location);
            receivePlayerTexture(descriptor, location, url, null);
            complete.accept(Optional.of(descriptor));
        });
        textureManager.register(location, downloadingTexture);
    }

    public void receivePlayerTexture(String url, NativeImage image, boolean slim) {
        if (image == null) {
            return;
        }
        NativeImage newImage = new NativeImage(image.format(), image.getWidth(), image.getHeight(), true);
        newImage.copyFrom(image);
        workThread.execute(() -> {
            BakedEntityTexture bakedTexture = getDownloadedTexture(url);
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

    private synchronized void receivePlayerTexture(PlayerTextureDescriptor descriptor, ResourceLocation location, String url, String model) {
        PlayerTexture resolvedTexture = new PlayerTexture(url, location, model);
        BakedEntityTexture bakedTexture = getDownloadedTexture(url);
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
