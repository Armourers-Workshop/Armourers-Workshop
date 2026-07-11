package moe.plushie.armourers_workshop.core.client.animation.effect;

import moe.plushie.armourers_workshop.core.client.sound.SmartSoundManager;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationData;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.BlockEntitySelectorImpl;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.EntitySelectorImpl;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.ScheduledExpression;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;

public class ClientSoundEffect implements ScheduledExpression<Runnable> {

    private final String name;
    private final SoundEvent sound;

    private final float pitch;
    private final float volume;

    public ClientSoundEffect(SkinAnimationData.Point.Sound sound) {
        var soundProvider = sound.data();
        var soundProperties = soundProvider.properties();
        this.name = sound.effect();
        this.pitch = soundProperties.pitch();
        this.volume = soundProperties.volume();
        this.sound = resolveSoundEvent(soundProvider);
    }

    @Override
    public Runnable submit(final ExecutionContext context) {
        SmartSoundManager.getInstance().open(sound);
        var soundInstance = createSoundInstance(context);
        startPlay(soundInstance);
        return () -> {
            stopPlay(soundInstance);
            RenderSystem.recordRenderCall(() -> SmartSoundManager.getInstance().close(sound));
        };
    }

    @Override
    public void cancel(Runnable result) {
        result.run();
    }

    private void startPlay(SoundInstance soundInstance) {
        getSoundManager().play(soundInstance);
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("start play {}", this);
        }
    }

    private void stopPlay(SoundInstance soundInstance) {
        getSoundManager().stop(soundInstance);
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("stop play {}", this);
        }
    }

    private SoundEvent resolveSoundEvent(SkinSoundData soundData) {
        return SmartSoundManager.getInstance().register(soundData).create(it -> {
            var key = it.location();
            return SoundEvent.createVariableRangeEvent(key.get());
        });
    }

    private SoundInstance createSoundInstance(ExecutionContext context) {
        // this current entity is block entity?
        if (context.entity() instanceof BlockEntitySelectorImpl<?> entity) {
            return SoundInstance.forBlockEntity(sound, entity.entity(), volume, pitch);
        }
        // the current entity is entity?
        if (context.entity() instanceof EntitySelectorImpl<?> entity) {
            return SoundInstance.forEntity(sound, entity.entity(), volume, pitch);
        }
        // the fallback is gui sounds, maybe?
        return SoundInstance.forUI(sound, volume, pitch);
    }

    private SoundManager getSoundManager() {
        return Minecraft.getInstance().getSoundManager();
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name);
    }


}
