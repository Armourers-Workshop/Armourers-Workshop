package moe.plushie.armourers_workshop.core.client.sound;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.skin.sound.ISkinSoundProvider;
import moe.plushie.armourers_workshop.core.client.other.SmartResourceManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundData;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundProperties;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.sounds.SoundEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmartSound extends ReferenceCounted {

    private final String name;
    private final OpenResourceLocation location;
    private final SkinSoundProperties properties;
    private final Map<OpenResourceLocation, ByteBuf> soundBuffers;

    private SoundEvent soundEvent;

    public SmartSound(SkinSoundData provider) {
        this.name = provider.name();
        this.properties = provider.properties();
        this.location = ModConstants.key("sounds/dynamic/" + OpenRandomSource.nextInt(SmartSound.class) + ".ogg");
        this.soundBuffers = resolveSoundBuffers(location, provider);
    }

    public static SmartSound of(SoundEvent soundEvent) {
        return DataContainer.getOrDefault(soundEvent, null);
    }

    @Override
    protected void init() {
        RenderSystem.safeCall(() -> {
            soundBuffers.forEach(SmartResourceManager.getInstance()::register);
            SmartSoundManager.getInstance().uploadSound(this);
        });
    }

    @Override
    protected void dispose() {
        RenderSystem.safeCall(() -> {
            SmartSoundManager.getInstance().releaseSound(this);
            soundBuffers.keySet().forEach(SmartResourceManager.getInstance()::unregister);
        });
    }

    public String name() {
        return name;
    }

    public OpenResourceLocation location() {
        return location;
    }

    public SoundEvent soundEvent() {
        if (soundEvent == null) {
            soundEvent = SoundEvent.createVariableRangeEvent(location.toLocation());
            DataContainer.set(soundEvent, this);
        }
        return soundEvent;
    }

    protected void unbind() {
        DataContainer.set(soundEvent, null);
        // when unbind the object, we must ensure that all resources release.
        while (refCnt() > 0) {
            release();
        }
    }

    private Map<OpenResourceLocation, ByteBuf> resolveSoundBuffers(OpenResourceLocation location, ISkinSoundProvider provider) {
        var results = new LinkedHashMap<OpenResourceLocation, ByteBuf>();
        results.put(location, provider.buffer());
        return results;
    }
}
