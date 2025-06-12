package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.PlayerSelector;
import net.minecraft.world.entity.player.Player;

public class PlayerSelectorImpl<T extends Player> extends LivingEntitySelectorImpl<T> implements PlayerSelector {

    @Override
    public PlayerSelectorImpl<T> apply(T entity, ContextSelectorImpl context) {
        super.apply(entity, context);
        return this;
    }

    @Override
    public double elytraYaw() {
        return 0;
    }

    @Override
    public double elytraPitch() {
        return 0;
    }

    @Override
    public double elytraRoll() {
        return 0;
    }

    @Override
    public boolean hasCape() {
        return false;
    }

    @Override
    public double capeFlapAmount() {
        return 0;
    }

    @Override
    public int foodLevel() {
        return 0;
    }

    @Override
    public double experience() {
        return 0;
    }

    @Override
    public boolean hasLeftShoulderParrot() {
        return false;
    }

    @Override
    public boolean hasRightShoulderParrot() {
        return false;
    }

    @Override
    public int leftShoulderParrotVariant() {
        return 0;
    }

    @Override
    public int rightShoulderParrotVariant() {
        return 0;
    }

//    double getElytraYaw(); // x
//
//    double getElytraPitch(); // y
//
//    double getElytraRoll(); // z
//
//    boolean hasCape(); //        var("has_cape", ctx -> false);
//
//    double getCapeFlapAmount(); //         var("cape_flap_amount", ctx -> 0);
//
//    int getFoodLevel();
//
//    double getExperience(); //        maidEntityVar("player_level", ctx -> ctx.entity().getExperience());
}
