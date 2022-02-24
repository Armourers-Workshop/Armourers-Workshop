package moe.plushie.armourers_workshop.core.texture;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PlayerTextureLoader {

    private static final PlayerTextureLoader LOADER = new PlayerTextureLoader();

    private final HashMap<String, GameProfile> profiles = new HashMap<>();
    private final HashMap<PlayerTextureDescriptor, Optional<PlayerTexture>> textures = new HashMap<>();

    private final Executor executor = Executors.newFixedThreadPool(1);

    public static PlayerTextureLoader getInstance() {
        return LOADER;
    }

    @Nullable
    public PlayerTexture getTexture(PlayerTextureDescriptor descriptor) {
        Optional<PlayerTexture> texture = textures.get(descriptor);
        if (texture != null && texture.isPresent()) {
            return texture.get();
        }
        return null;
    }

    @Nullable
    public PlayerTexture loadTexture(PlayerTextureDescriptor descriptor) {
        Optional<PlayerTexture> texture = textures.get(descriptor);
        if (texture == null) {
            loadTextureDescriptor(descriptor, p -> {});
            return null;
        }
        return texture.orElse(null);
    }

    public void loadTextureDescriptor(PlayerTextureDescriptor descriptor, Consumer<Optional<PlayerTextureDescriptor>> complete) {
        Optional<PlayerTexture> texture = textures.get(descriptor);
        if (texture != null) {
            complete.accept(Optional.of(descriptor));
            return;
        }
        // from username
        GameProfile profile = descriptor.getProfile();
        if (profile != null) {
            loadGameProfile(profile, profile1 -> complete.accept(profile1.map(resolvedProfile -> {
                SkinManager manager = Minecraft.getInstance().getSkinManager();
                manager.registerSkins(resolvedProfile, (type, location, texture1) -> onSkinTextureAvailable(type, location, texture1, resolvedProfile, descriptor), true);
                return new PlayerTextureDescriptor(resolvedProfile);
            })));
            return;
        }
        // from url
        String url = descriptor.getURL();
        if (url != null) {
            //            ThreadDownloadImageData
            complete.accept(Optional.of(descriptor));
            return;
        }
        // ignore
        if (complete != null) {
            complete.accept(Optional.of(descriptor));
        }
    }

    private void loadGameProfile(GameProfile profile, Consumer<Optional<GameProfile>> complete) {
        if (profile.isComplete()) {
            complete.accept(Optional.of(profile));
            return;
        }
        String name = profile.getName();
        if (profiles.containsKey(name)) {
            complete.accept(Optional.of(profiles.get(name)));
            return;
        }
        executor.execute(() -> {
            GameProfile profile1 = SkullTileEntity.updateGameprofile(profile);
            if (profile1 != null) {
                profiles.put(name, profile1);
            }
            complete.accept(Optional.ofNullable(profile1));
        });
    }

    public void onSkinTextureAvailable(MinecraftProfileTexture.Type type, ResourceLocation location, MinecraftProfileTexture texture, GameProfile profile, PlayerTextureDescriptor descriptor) {
        if (type != MinecraftProfileTexture.Type.SKIN) {
            return;
        }
        PlayerTexture texture1 = new PlayerTexture(profile, location, texture);
        textures.put(descriptor, Optional.of(texture1));
    }
}
