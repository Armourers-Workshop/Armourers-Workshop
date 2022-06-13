package moe.plushie.armourers_workshop.init.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings({"unused"})
public class ModSounds {

    private static final ArrayList<SoundEvent> REGISTERED_ITEMS = new ArrayList<>();

    public static final SoundEvent PAGE_TURN = register("page-turn");
    public static final SoundEvent PAINT = register("paint");
    public static final SoundEvent BURN = register("burn");
    public static final SoundEvent DODGE = register("dodge");
    public static final SoundEvent PICKER = register("picker");
    public static final SoundEvent NOISE = register("noise");
    public static final SoundEvent BOI = register("boi");

    private static SoundEvent register(String name) {
        ResourceLocation registryName = AWCore.resource(name);
        SoundEvent event = new SoundEvent(registryName);
        event.setRegistryName(registryName);
        REGISTERED_ITEMS.add(event);
        return event;
    }

    public static void forEach(Consumer<SoundEvent> action) {
        REGISTERED_ITEMS.forEach(action);
    }
}
