package moe.plushie.armourers_workshop.core.client.sound;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.sound.AbstractSimpleSound;
import moe.plushie.armourers_workshop.compat.client.sound.AbstractSoundManagerImpl;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundData;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

import java.util.IdentityHashMap;

@OnlyIn(Dist.CLIENT)
public class SmartSoundManager {

    private static final SmartSoundManager INSTANCE = new SmartSoundManager();

    protected final IdentityHashMap<Object, SmartSound> sounds = new IdentityHashMap<>();

    public static SmartSoundManager getInstance() {
        return INSTANCE;
    }

    public static void start() {
    }

    public static void stop() {
        // release all registered sounds.
        INSTANCE.sounds.values().forEach(SmartSound::unbind);
        INSTANCE.sounds.clear();
    }

    public void open(SoundEvent soundEvent) {
        var sound = SmartSound.of(soundEvent);
        if (sound != null) {
            sound.retain();
        }
    }

    public void close(SoundEvent soundEvent) {
        var sound = SmartSound.of(soundEvent);
        if (sound != null) {
            sound.release();
        }
    }

    public SmartSound register(SkinSoundData provider) {
        var sound = sounds.get(provider);
        if (sound == null) {
            sound = new SmartSound(provider);
            sounds.put(provider, sound);
        }
        return sound;
    }

    public AbstractSoundManagerImpl getSoundManager() {
        return (AbstractSoundManagerImpl) Minecraft.getInstance().getSoundManager();
    }

    protected void uploadSound(SmartSound sound) {
        var name = sound.name();
        var location = sound.location();
        var id = location.withPath(location.path().replaceAll("^sounds/(.+)\\.(\\w+)$", "$1"));
        getSoundManager().aw2$register(location, AbstractSimpleSound.create(id, name));
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Registering Sound '{}'", location);
        }
    }

    protected void releaseSound(SmartSound sound) {
        var location = sound.location();
        getSoundManager().aw2$unregister(location);
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Unregistering Sound '{}'", location);
        }
    }
}
