package moe.plushie.armourers_workshop.core.bake;

import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.DataLoader;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.core.utils.SkinLog;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;


public class SkinLoader {

    static HashMap<String, String> PATHS = new HashMap<>();

    static {
        String[] paths = {
                "护摩之杖.armour",
                "胡桃.armour",
                "钟离.armour",
                "浊心斯卡蒂+海嗣背饰.armour",
                "12531 - 早柚.armour",
                "12740 - V1 Wings.armour",
                "T.armour",
                "T-SW.armour",
                "T-RH.armour",
                "TR-H.armour",
                "T2-H.armour",
                "T2.armour",
                "11152 - Kagutsuchi Overlay (The Fire God).armour",
                "9265 - Luke's Droid Shovel.armour",
                "12072 - PINK PICKAXE.armour",
                "10293 - Garry's mod Tool gun.armour",
                "12162 - Energized Pickaxe.armour",
                "12661 - Arcane Jayce Mercury Hammer - LoL.armour",
                "10564 - Rose Glass Shield.armour",
                "12626 - 飞雷之弦振.armour",
                "12418 - [Random] - Starlight Axe.armour",
                "12729 - White Bat Ears.armour",
                "12902 - Winter Before.armour",
                "12414 - Komi.armour",
                "10032 - ?.armour",
                "2/5818 - æ˜Žæ—¥æ–¹èˆŸé›ªäººé™ˆ1.armour",
                "2/5819 - æ˜Žæ—¥æ–¹èˆŸé›ªäººé™ˆ2.armour",
                "2/5820 - æ˜Žæ—¥æ–¹èˆŸé›ªäººé™ˆ3.armour",
                "2/5821 - æ˜Žæ—¥æ–¹èˆŸé›ªäººé™ˆ4.armour",
                "2/6390 - å†°ä¸Žç\u0081«ä¹‹æ\u00ADŒ.armour",
                "2/6397 - é—ªè€€è“\u009Dé“\u0081ä¹‹å‰‘.armour",
                "2/6462 - ç¬¦æ–‡-æµ\u0081ç\u0081«.armour",
                "2/6463 - ç¬¦æ–‡-å‡\u009Déœœ.armour",
                "12388 - Light rifle (Halo).armour",
                "T/T-H.armour",
                "T/T-C.armour",
                "T/T-F.armour",
                "T/T-L.armour",
                "T/T-W.armour",
                "TP/TP-H.armour",
                "TP/TP-C.armour",
                "TP/TP-F.armour",
                "TP/TP-L.armour",
                "TP/TP-W.armour",
        };
        for (int i = 0; i < paths.length; ++i) {
            PATHS.put(String.valueOf(i), "./armoures/" + paths[i]);
        }
    }

    private final DataLoader<SkinDescriptor, Skin> manager = DataLoader.newBuilder()
            .threadPool(2)
            .build((key, complete) -> {
                Skin skin = loadSkinFromPath(key);
                complete.accept(Optional.ofNullable(skin));
            });

    @Nullable
    public Skin getSkin(ItemStack itemStack) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return null;
        }
        return getSkin(descriptor);
    }


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
        Skin skin = SkinIOUtils.loadSkinFromFile(new File(path));
        SkinLog.debug("Loading skin " + descriptor + " did complete !");
        return skin;
    }
}
