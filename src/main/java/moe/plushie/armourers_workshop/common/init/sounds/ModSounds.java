package moe.plushie.armourers_workshop.common.init.sounds;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSounds {

    public static final ArrayList<ModSound> SOUND_LIST = new ArrayList<ModSound>();

    public static final ModSound PAGE_TURN = new ModSound(new ResourceLocation(LibModInfo.ID, "pageTurn"));
    public static final ModSound PAINT = new ModSound(new ResourceLocation(LibModInfo.ID, "paint"));
    public static final ModSound BURN = new ModSound(new ResourceLocation(LibModInfo.ID, "burn"));
    public static final ModSound DODGE = new ModSound(new ResourceLocation(LibModInfo.ID, "dodge"));
    public static final ModSound PICKER = new ModSound(new ResourceLocation(LibModInfo.ID, "picker"));
    public static final ModSound NOISE = new ModSound(new ResourceLocation(LibModInfo.ID, "noise"));
    public static final ModSound BOI = new ModSound(new ResourceLocation(LibModInfo.ID, "boi"));

    public ModSounds() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        IForgeRegistry<SoundEvent> reg = event.getRegistry();
        for (int i = 0; i < SOUND_LIST.size(); i++) {
            reg.register(SOUND_LIST.get(i));
        }
    }

    public static class ModSound extends SoundEvent {

        public ModSound(ResourceLocation soundNameIn) {
            super(soundNameIn);
            setRegistryName(soundNameIn);
            SOUND_LIST.add(this);
        }
    }
}
