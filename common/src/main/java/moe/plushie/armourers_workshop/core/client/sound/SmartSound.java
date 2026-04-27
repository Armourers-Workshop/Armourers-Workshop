package moe.plushie.armourers_workshop.core.client.sound;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.skin.sound.ISkinSoundProvider;
import moe.plushie.armourers_workshop.core.client.other.SmartResourceManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundData;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundProperties;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.sounds.SoundEvent;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class SmartSound extends ReferenceCounted {

    private static final DataContainer.Key<SmartSound> KEY = DataContainer.key("SmartSound");

    private final String name;

    private final OpenResourceKey location;
    private final SkinSoundProperties properties;
    private final Map<OpenResourceKey, ByteBuf> soundBuffers;

    private final Set<SoundEvent> binding = new HashSet<>();

    public SmartSound(SkinSoundData provider) {
        this.location = ModConstants.key("sounds/dynamic/" + OpenRandomSource.nextInt(SmartSound.class) + "." + provider.extension());
        this.properties = provider.properties();
        this.soundBuffers = resolveSoundBuffers(location, provider);
        this.name = provider.name();
    }

    public static SmartSound of(SoundEvent soundEvent) {
        return DataContainer.get(soundEvent, KEY);
    }

    public SoundEvent create(Function<SmartSound, SoundEvent> factory) {
        var soundEvent = factory.apply(this);
        if (binding.add(soundEvent)) {
            DataContainer.set(soundEvent, KEY, this);
        }
        return soundEvent;
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

    public OpenResourceKey location() {
        return location;
    }

    protected void unbind() {
        binding.forEach(value -> DataContainer.set(value, KEY, null));
        // when unbind the object, we must ensure that all resources release.
        while (refCnt() > 0) {
            release();
        }
    }

    private Map<OpenResourceKey, ByteBuf> resolveSoundBuffers(OpenResourceKey key, ISkinSoundProvider provider) {
        var results = new LinkedHashMap<OpenResourceKey, ByteBuf>();
        results.put(key, provider.buffer());
        return results;
    }
}
