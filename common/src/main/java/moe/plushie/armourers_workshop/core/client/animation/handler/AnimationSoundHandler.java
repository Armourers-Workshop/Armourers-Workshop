package moe.plushie.armourers_workshop.core.client.animation.handler;

import moe.plushie.armourers_workshop.core.client.sound.SmartSoundManager;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationPoint;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.BlockEntitySelectorImpl;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.EntitySelectorImpl;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;

public class AnimationSoundHandler implements OptimizedExpression<Object> {

    private final String name;
    private final SoundEvent soundEvent;

    private final float volume;
    private final float pitch;

    public AnimationSoundHandler(SkinAnimationPoint.Sound sound) {
        var soundProvider = sound.provider();
        var soundProperties = soundProvider.properties();
        this.name = sound.effect();
        this.volume = soundProperties.volume();
        this.pitch = soundProperties.pitch();
        this.soundEvent = SmartSoundManager.getInstance().register(soundProvider);
    }

    @Override
    public Runnable evaluate(ExecutionContext context) {
        SmartSoundManager.getInstance().open(soundEvent);
        var sound = createSound(context);
        startPlay(sound);
        return () -> {
            stopPlay(sound);
            RenderSystem.recordRenderCall(() -> SmartSoundManager.getInstance().close(soundEvent));
        };
    }

    private void startPlay(SoundInstance sound) {
        soundManager().play(sound);
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("start play {}", this);
        }
    }

    private void stopPlay(SoundInstance sound) {
        soundManager().stop(sound);
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("stop play {}", this);
        }
    }

    private SoundInstance createSound(ExecutionContext context) {
        // this current entity is block entity?
        if (context instanceof BlockEntitySelectorImpl<?> entity) {
            return SoundInstance.forBlockEntity(soundEvent, entity.getEntity(), volume, pitch);
        }
        // the current entity is entity?
        if (context instanceof EntitySelectorImpl<?> entity) {
            return SoundInstance.forEntity(soundEvent, entity.entity(), volume, pitch);
        }
        // the fallback is gui sounds, maybe?
        return SoundInstance.forUI(soundEvent, volume, pitch);
    }

    private SoundManager soundManager() {
        return Minecraft.getInstance().getSoundManager();
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name);
    }
}
