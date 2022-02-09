package moe.plushie.armourers_workshop.core.bake;

import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SkinManager {

    public final static SkinManager INSTANCE = new SkinManager();

    public final SkinLoader loader = new SkinLoader();



    public Skin getSkin(SkinDescriptor descriptor) {
//        Skin skin = loadedSkins.get(descriptor);
//        if (skin != null) {
//            return skin;
//        }
//        loader.loadSkin(descriptor, newSkin -> {
//            loadedSkins.put(descriptor, newSkin);
//            loadingSkins.remove(descriptor);
//        });
        return null;
    }



}

//@OnlyIn(Dist.CLIENT)
//public class SkinClientManager {
//
//    public BakedSkin getBakedSkin(SkinDescriptor descriptor) {
//        return null;
//    }
//
//    public void bakeSkin(SkinDescriptor descriptor, Skin skin, BiConsumer<BakedSkin, Exception> consumer) {
//
//    }
//}
