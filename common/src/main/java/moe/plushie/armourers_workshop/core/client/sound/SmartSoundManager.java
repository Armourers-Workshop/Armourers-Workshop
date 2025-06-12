package moe.plushie.armourers_workshop.core.client.sound;

import moe.plushie.armourers_workshop.compatibility.client.AbstractSimpleSound;
import moe.plushie.armourers_workshop.compatibility.client.AbstractSoundManagerImpl;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundData;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

import java.util.IdentityHashMap;

@Environment(EnvType.CLIENT)
public class SmartSoundManager {

    private static final SmartSoundManager INSTANCE = new SmartSoundManager();

    protected final IdentityHashMap<Object, SmartSound> sounds = new IdentityHashMap<>();

    public static SmartSoundManager getInstance() {
        return INSTANCE;
    }

    public synchronized void start() {
    }

    public synchronized void stop() {
        // release all registered sounds.
        sounds.values().forEach(SmartSound::unbind);
        sounds.clear();
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

    public synchronized SoundEvent register(SkinSoundData provider) {
        var sound = sounds.get(provider);
        if (sound == null) {
            sound = new SmartSound(provider);
            sounds.put(provider, sound);
        }
        return sound.soundEvent();
    }

    public AbstractSoundManagerImpl getSoundManager() {
        return (AbstractSoundManagerImpl) Minecraft.getInstance().getSoundManager();
    }

    protected void uploadSound(SmartSound sound) {
        var name = sound.name();
        var location = sound.location();
        var id = location.withPath(location.path().replaceFirst("sounds/(.+)\\.ogg", "$1"));
        getSoundManager().aw2$register(location.toLocation(), AbstractSimpleSound.create(id.toLocation(), name));
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Registering Sound '{}'", location);
        }
    }

    protected void releaseSound(SmartSound sound) {
        var location = sound.location();
        getSoundManager().aw2$unregister(location.toLocation());
        if (ModConfig.Client.enableResourceDebug) {
            ModLog.debug("Unregistering Sound '{}'", location);
        }
    }
}
