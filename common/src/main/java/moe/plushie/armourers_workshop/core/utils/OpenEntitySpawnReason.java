package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.common.IEntitySpawnReason;

public enum OpenEntitySpawnReason implements IEntitySpawnReason {
    NATURAL,
    CHUNK_GENERATION,
    SPAWNER,
    STRUCTURE,
    BREEDING,
    MOB_SUMMONED,
    JOCKEY,
    EVENT,
    CONVERSION,
    REINFORCEMENT,
    TRIGGERED,
    BUCKET,
    SPAWN_ITEM_USE,
    COMMAND,
    DISPENSER,
    PATROL,
    TRIAL_SPAWNER,
    LOAD,
    DIMENSION_TRAVEL;
}
