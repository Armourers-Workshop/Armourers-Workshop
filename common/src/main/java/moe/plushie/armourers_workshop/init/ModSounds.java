package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.core.registry.Registries;
import net.minecraft.sounds.SoundEvent;

@SuppressWarnings({"unused"})
public class ModSounds {

    public static final IRegistryKey<SoundEvent> PAGE_TURN = register("page-turn");
    public static final IRegistryKey<SoundEvent> PAINT = register("paint");
    public static final IRegistryKey<SoundEvent> BURN = register("burn");
    public static final IRegistryKey<SoundEvent> DODGE = register("dodge");
    public static final IRegistryKey<SoundEvent> PICKER = register("picker");
    public static final IRegistryKey<SoundEvent> NOISE = register("noise");
    public static final IRegistryKey<SoundEvent> BOI = register("boi");

    private static IRegistryKey<SoundEvent> register(String name) {
        return Registries.SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent(ModConstants.key(name)));
    }

    public static void init() {
    }
}
