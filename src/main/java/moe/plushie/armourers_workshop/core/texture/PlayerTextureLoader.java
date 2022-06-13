package moe.plushie.armourers_workshop.core.texture;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class PlayerTextureLoader {

    public static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
    public static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");

    private static final PlayerTextureLoader LOADER = new PlayerTextureLoader();

    private final HashMap<String, Optional<GameProfile>> profiles = new HashMap<>();
    private final HashMap<PlayerTextureDescriptor, Optional<PlayerTexture>> resolvedTextures = new HashMap<>();
    private final HashSet<PlayerTextureDescriptor> loadingTextures = new HashSet<>();

    private final HashMap<String, BakedEntityTexture> downloadedTextures = new HashMap<>();
    private final HashMap<ResourceLocation, Optional<PlayerTexture>> bakedTextures = new HashMap<>();

    private final Executor executor = Executors.newFixedThreadPool(1);

    public static PlayerTextureLoader getInstance() {
        return LOADER;
    }

    @Nullable
    public BakedEntityTexture getTextureModel(ResourceLocation location) {
        Optional<PlayerTexture> texture = bakedTextures.get(location);
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
        if (entity instanceof ClientPlayerEntity) {
            return ((ClientPlayerEntity) entity).getSkinTextureLocation();
        }
        return DefaultPlayerSkin.getDefaultSkin();
    }

    @Nullable
    public PlayerTexture loadTexture(PlayerTextureDescriptor descriptor) {
        if (descriptor.isEmpty()) {
            return null;
        }
        Optional<PlayerTexture> texture = resolvedTextures.get(descriptor);
        if (texture != null) {
            return texture.orElse(null);
        }
        loadTextureDescriptor(descriptor, descriptor1 -> {});
        return null;
    }

    public void loadTextureDescriptor(PlayerTextureDescriptor descriptor, Consumer<Optional<PlayerTextureDescriptor>> complete) {
        Optional<PlayerTexture> texture = resolvedTextures.get(descriptor);
        if (texture != null || descriptor.isEmpty() || !loadingTextures.add(descriptor)) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        // load from username
        GameProfile profile = descriptor.getProfile();
        if (profile != null) {
            loadGameProfile(profile, profile1 -> complete.accept(profile1.map(resolvedProfile -> {
                if (resolvedProfile.isComplete()) {
                    loadVanillaTexture(resolvedProfile);
                }
                return new PlayerTextureDescriptor(resolvedProfile);
            })));
            return;
        }
        // load from url
        String url = descriptor.getURL();
        if (url != null) {
            try {
                URL ignored = new URL(url);
                loadCustomTexture(url);
                complete.accept(Optional.of(descriptor));
            } catch (MalformedURLException e) {
                ModLog.info("input a invalid url '{}'", url);
                complete.accept(Optional.empty());
            }
            return;
        }
        // ignore
        if (complete != null) {
            complete.accept(Optional.of(descriptor));
        }
    }

    public GameProfile loadGameProfile(GameProfile profile) {
        if (profile.isComplete()) {
            return profile;
        }
        String name = profile.getName().toLowerCase();
        Optional<GameProfile> profile1 = profiles.get(name);
        if (profile1 != null) {
            return profile1.orElse(profile);
        }
        loadGameProfile(profile, null);
        return profile;
    }

    public void loadGameProfile(GameProfile profile, @Nullable Consumer<Optional<GameProfile>> complete) {
        if (profile.isComplete()) {
            if (complete != null) {
                complete.accept(Optional.of(profile));
            }
            return;
        }
        String name = profile.getName().toLowerCase();
        if (profiles.containsKey(name)) {
            if (complete != null) {
                complete.accept(profiles.get(name));
            }
            return;
        }
        profiles.put(name, Optional.empty());
        executor.execute(() -> {
            GameProfile profile1 = null;
            try {
                profile1 = SkullTileEntity.updateGameprofile(profile);
                profiles.put(name, Optional.ofNullable(profile1));
            } catch (Exception exception) {
                // we called mojang API, it will throw `com.mojang.authlib.exceptions.*`.
                exception.printStackTrace();
                profiles.remove(name);
            }
            if (complete != null) {
                complete.accept(Optional.ofNullable(profile1));
            }
        });
    }

    public void loadDefaultTexture(ResourceLocation location) {
        boolean alex = location.equals(ALEX_SKIN_LOCATION);
        if (!alex && !location.equals(STEVE_SKIN_LOCATION)) {
            return; // not steve and alex
        }
        bakedTextures.put(location, Optional.empty());
        executor.execute(() -> {
            PlayerTexture resolvedTexture = new PlayerTexture("", location, null);
            resolvedTexture.setTexture(new BakedEntityTexture(location, alex));
            bakedTextures.put(location, Optional.of(resolvedTexture));
        });
    }

    private void loadVanillaTexture(GameProfile profile) {
        PlayerTextureDescriptor descriptor = new PlayerTextureDescriptor(profile);
        Optional<PlayerTexture> texture = resolvedTextures.get(descriptor);
        if (texture != null) {
            return;
        }
        ModLog.debug("accept player texture load request => {}", profile);
        SkinManager manager = Minecraft.getInstance().getSkinManager();
        manager.registerSkins(profile, (type, location, profileTexture) -> {
            if (type != MinecraftProfileTexture.Type.SKIN) {
                return;
            }
            String model = profileTexture.getMetadata("model");
            if (model == null) {
                model = "default";
            }
            ModLog.debug("receive player texture from vanilla loader => {}", location);
            receivePlayerTexture(descriptor, location, profileTexture.getUrl(), model);
        }, true);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void loadCustomTexture(String url) {
        PlayerTextureDescriptor descriptor = new PlayerTextureDescriptor(url);
        Optional<PlayerTexture> texture = resolvedTextures.get(descriptor);
        if (texture != null) {
            return;
        }
        ModLog.debug("accept player texture load request => {}", url);
        String identifier = Hashing.sha1().hashUnencodedChars(url).toString();
        ResourceLocation location = new ResourceLocation("skins/aw-" + identifier);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        Texture processingTexture = textureManager.getTexture(location);
        if (processingTexture != null) {
            return;
        }
        String sub = identifier.length() > 2 ? identifier.substring(0, 2) : "xx";
        File path = new File(AWCore.getRootDirectory() + "/skin-textures/" + sub + "/" + identifier);
        DownloadingTexture downloadingTexture = new DownloadingTexture(path, url, DefaultPlayerSkin.getDefaultSkin(), true, () -> {
            ModLog.debug("receive player texture from custom loader => {}", location);
            receivePlayerTexture(descriptor, location, url, null);
        });
        textureManager.register(location, downloadingTexture);
    }

    public void receivePlayerTexture(String url, NativeImage image, boolean slim) {
        if (image == null) {
            return;
        }
        NativeImage newImage = new NativeImage(image.format(), image.getWidth(), image.getHeight(), true);
        newImage.copyFrom(image);
        executor.execute(() -> {
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
