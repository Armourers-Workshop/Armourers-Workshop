package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.ISoundEventBuilder;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.sounds.SoundEvent;

@SuppressWarnings({"unused"})
public class ModSounds {

    public static final IRegistryHolder<SoundEvent> PAGE_TURN = normal().build("page-turn");
    public static final IRegistryHolder<SoundEvent> PAINT = normal().build("paint");
    public static final IRegistryHolder<SoundEvent> BURN = normal().build("burn");
    public static final IRegistryHolder<SoundEvent> DODGE = normal().build("dodge");
    public static final IRegistryHolder<SoundEvent> PICKER = normal().build("picker");
    public static final IRegistryHolder<SoundEvent> NOISE = normal().build("noise");
    public static final IRegistryHolder<SoundEvent> BOI = normal().build("boi");

    public static void init() {
    }

    private static <T extends SoundEvent> ISoundEventBuilder<T> normal() {
        return BuilderManager.getInstance().createSoundEventBuilder();
    }
}
