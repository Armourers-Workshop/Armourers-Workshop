package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.level.GameRules;

@Available("[16, 26)")
public class AbstractGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> KEEP_INVENTORY = GameRules.RULE_KEEPINVENTORY;
}
