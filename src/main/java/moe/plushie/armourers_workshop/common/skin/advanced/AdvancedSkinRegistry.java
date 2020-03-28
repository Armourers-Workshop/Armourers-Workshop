package moe.plushie.armourers_workshop.common.skin.advanced;

import moe.plushie.armourers_workshop.common.ModRegistry;
import moe.plushie.armourers_workshop.common.skin.advanced.action.SkinActionRotate;
import moe.plushie.armourers_workshop.common.skin.advanced.trigger.SkinTriggerAlways;
import moe.plushie.armourers_workshop.common.skin.advanced.value.SkinValueHealthPercentage;
import moe.plushie.armourers_workshop.common.skin.advanced.value.SkinValueInWater;
import moe.plushie.armourers_workshop.common.skin.advanced.value.SkinValueRaining;
import moe.plushie.armourers_workshop.common.skin.advanced.value.SkinValueSneaking;
import moe.plushie.armourers_workshop.common.skin.advanced.value.SkinValueTime;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class AdvancedSkinRegistry {

    private final ModRegistry<AdvancedSkinTrigger> registrySkinTriggers = new ModRegistry<AdvancedSkinTrigger>("Skin Trigger Registry");
    private final ModRegistry<AdvancedSkinAction> registrySkinActions = new ModRegistry<AdvancedSkinAction>("Skin Action Registry");
    private final ModRegistry<AdvancedSkinValue> registrySkinValues = new ModRegistry<AdvancedSkinValue>("Skin Value Registry");

    public AdvancedSkinRegistry() {
        register();
    }

    private void register() {
        // Triggers
        //
        // NONE,
        // ANIMATION_STARTED,
        // ANIMATION_FINISHED,
        // TOOK_DAMAGE,
        // ENTER_COMBAT,
        // LEAVE_COMBAT,
        // LEVEL_UP,
        // PART_ENABLED,
        // PART_DISABLED,
        // HIT_TARGET,
        // TICK,
        // RANDOM_TICK
        registrySkinTriggers.register(new SkinTriggerAlways());

        // Actions
        //
        // NONE,
        // PLAY_SOUND,
        // START_ANIMATION,
        // STOP_ANIMATION,
        // SET_PART_PROPERTY
        registrySkinActions.register(new SkinActionRotate());

        // Values
        //
        // NONE,
        // HEALTH,
        // IN_WATER,
        // SNEAKING,
        // TIME,
        // CLOSEST_MOB_RANGE,
        // TARGET_RANGE,
        // IN_COMBAT,
        // LIGHT_LEVEL,
        // FLYING,
        // IN_RAIN,
        // MOVEMENT_SPEED
        registrySkinValues.register(new SkinValueHealthPercentage());
        registrySkinValues.register(new SkinValueInWater());
        registrySkinValues.register(new SkinValueSneaking());
        registrySkinValues.register(new SkinValueTime());
        registrySkinValues.register(new SkinValueRaining());
    }

    public static abstract class AdvancedSkinTrigger extends ModRegistry.RegistryItem {

        public AdvancedSkinTrigger(String name) {
            super(name);
        }

        public abstract boolean canTrigger(World world, Entity entity, Skin skin, SkinPart skinPart);
    }

    public static abstract class AdvancedSkinAction extends ModRegistry.RegistryItem {

        public AdvancedSkinAction(String name) {
            super(name);
        }

        public abstract void trigger(World world, Entity entity, Skin skin, float... data);

        public abstract String[] getInputs();
    }

    public static abstract class AdvancedSkinValue extends ModRegistry.RegistryItem {

        public AdvancedSkinValue(String name) {
            super(name);
        }

        public abstract float getValue(World world, Entity entity, Skin skin, SkinPart skinPart);
    }

    public static abstract class AdvancedSkinMathValue extends ModRegistry.RegistryItem {

        public AdvancedSkinMathValue(String name) {
            super(name);
        }

        public abstract float getValue(World world, Entity entity, Skin skin, float... data);

        public abstract String[] getInputs();
    }
}
