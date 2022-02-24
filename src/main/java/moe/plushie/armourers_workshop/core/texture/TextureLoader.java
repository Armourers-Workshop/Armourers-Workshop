package moe.plushie.armourers_workshop.core.texture;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class TextureLoader {

    private static final TextureLoader LOADER = new TextureLoader();

    public static TextureLoader getInstance() {
        return LOADER;
    }

    @Nullable
    public Texture getTexture(TextureDescriptor descriptor) {
//        Optional<Skin> skin = manager.getOrLoad(descriptor);
//        if (skin != null && skin.isPresent()) {
//            return skin.get();
//        }
        return null;
    }

    @Nullable
    public Texture loadTexture(TextureDescriptor descriptor) {
//        Optional<Skin> skin = manager.getOrLoad(descriptor);
//        if (skin != null && skin.isPresent()) {
//            return skin.get();
//        }
        return null;
    }

    public void loadTexture(TextureDescriptor descriptor, @Nullable Consumer<Optional<Texture>> consumer) {
        // download profile
//            GameProfile profile = .getProfile();
//            if (profile != null) {
//                profile = SkullTileEntity.updateGameprofile(profile);
//            }

//        manager.load(descriptor, false, consumer);
    }

}
