package moe.plushie.armourers_workshop.core.bake;

import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.DataLoader;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;


public class SkinLoader {

    static HashMap<String, String> PATHS = new HashMap<>();

    static {
        String[] paths = {
                "/Users/sagesse/Downloads/胡桃/护摩之杖.armour",
                "/Users/sagesse/Downloads/胡桃/胡桃.armour",
                "/Users/sagesse/Downloads/钟离/钟离.armour",
                "/Users/sagesse/Downloads/浊心斯卡蒂/浊心斯卡蒂+海嗣背饰.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/12531 - 早柚.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/12740 - V1 Wings.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T-SW.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T-RH.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/TR-H.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T2-H.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/T2.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/11152 - Kagutsuchi Overlay (The Fire God).armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/9265 - Luke's Droid Shovel.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/12072 - PINK PICKAXE.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/10293 - Garry's mod Tool gun.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/12162 - Energized Pickaxe.armour",
                "/Users/sagesse/Library/Application Support/minecraft/armourers_workshop/skin-library/downloads/12661 - Arcane Jayce Mercury Hammer - LoL.armour"
        };
        for (int i = 0; i < paths.length; ++i) {
            PATHS.put(String.valueOf(i), paths[i]);
        }
    }

    private final DataLoader<SkinDescriptor, Skin> manager = DataLoader.newBuilder()
            .threadPool(2)
            .build((key, complete) -> {
                Skin skin = loadSkinFromPath(key);
                complete.accept(Optional.ofNullable(skin));
            });

    @Nullable
    public Skin getSkin(SkinDescriptor descriptor) {
        Optional<Skin> skin = manager.get(descriptor);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    @Nullable
    public Skin loadSkin(SkinDescriptor descriptor) {
        Optional<Skin> skin = manager.getOrLoad(descriptor);
        if (skin != null && skin.isPresent()) {
            return skin.get();
        }
        return null;
    }

    public void loadSkin(SkinDescriptor descriptor, @Nullable Consumer<Optional<Skin>> consumer) {
        manager.load(descriptor, false, consumer);
    }

    private Skin loadSkinFromPath(SkinDescriptor descriptor) {
        String identifier = descriptor.getIdentifier();
        if (identifier.isEmpty()) {
            return null;
        }
        String path = PATHS.get(identifier);
        if (path == null) {
            return null;
        }
        return SkinIOUtils.loadSkinFromFile(new File(path));
    }
}
