package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.core.registry.Registry;
import net.minecraft.sounds.SoundEvent;

@SuppressWarnings({"unused"})
public class ModSounds {

    public static final IRegistryObject<SoundEvent> PAGE_TURN = register("page-turn");
    public static final IRegistryObject<SoundEvent> PAINT = register("paint");
    public static final IRegistryObject<SoundEvent> BURN = register("burn");
    public static final IRegistryObject<SoundEvent> DODGE = register("dodge");
    public static final IRegistryObject<SoundEvent> PICKER = register("picker");
    public static final IRegistryObject<SoundEvent> NOISE = register("noise");
    public static final IRegistryObject<SoundEvent> BOI = register("boi");

    private static IRegistryObject<SoundEvent> register(String name) {
        return Registry.SOUND_EVENT.register(name, () -> new SoundEvent(ArmourersWorkshop.getResource(name)));
    }

    public static void init() {
    }
}
