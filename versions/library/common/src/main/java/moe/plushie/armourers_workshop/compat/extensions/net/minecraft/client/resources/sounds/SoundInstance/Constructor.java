package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.resources.sounds.SoundInstance;


import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.19, )")
@Extension
public class Constructor {

    private static final RandomSource RANDOM_SOURCE = RandomSource.create();

    public static SimpleSoundInstance forUI(@ThisClass Class<?> clazz, SoundEvent soundEvent, float volume, float pitch) {
        return SimpleSoundInstance.forUI(soundEvent, volume, pitch);
    }

    public static EntityBoundSoundInstance forEntity(@ThisClass Class<?> clazz, SoundEvent soundEvent, Entity entity, float volume, float pitch) {
        var soundSource = entity.getSoundSource();
        return new EntityBoundSoundInstance(soundEvent, soundSource, volume, pitch, entity, RANDOM_SOURCE.nextLong());
    }

    public static SimpleSoundInstance forBlockEntity(@ThisClass Class<?> clazz, SoundEvent soundEvent, BlockEntity blockEntity, float volume, float pitch) {
        var pos = blockEntity.getBlockPos();
        return new SimpleSoundInstance(soundEvent, SoundSource.BLOCKS, volume, pitch, RANDOM_SOURCE, pos);
    }
}
