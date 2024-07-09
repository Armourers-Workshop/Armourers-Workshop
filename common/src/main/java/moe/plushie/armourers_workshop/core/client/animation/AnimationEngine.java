package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.math.LazyVariable;
import moe.plushie.armourers_workshop.core.skin.molang.math.Literal;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class AnimationEngine {

    private static final MolangVirtualMachine VM = MolangVirtualMachine.get();

    private static final LazyVariable ANIM_TIME = VM.animTime;
    private static final LazyVariable LIFE_TIME = VM.lifeTime;
    private static final LazyVariable ACTOR_COUNT = VM.actorCount;
    private static final LazyVariable TIME_OF_DAY = VM.timeOfDay;
    private static final LazyVariable MOON_PHASE = VM.moonPhase;
    private static final LazyVariable DISTANCE_FROM_CAMERA = VM.distanceFromCamera;
    private static final LazyVariable IS_ON_GROUND = VM.isOnGround;
    private static final LazyVariable IS_IN_WATER = VM.isInWater;
    private static final LazyVariable IS_IN_WATER_OR_RAIN = VM.isInWaterOrRain;
    private static final LazyVariable HEALTH = VM.health;
    private static final LazyVariable MAX_HEALTH = VM.maxHealth;
    private static final LazyVariable IS_ON_FIRE = VM.isOnFire;
    private static final LazyVariable GROUND_SPEED = VM.groundSpeed;
    private static final LazyVariable YAW_SPEED = VM.yawSpeed;

    public static void start() {
    }

    public static void stop() {
        // clear all binding.
        VM.getVariables().forEach((name, variable) -> {
            if (!(variable instanceof Literal)) {
                variable.set(0);
            }
        });
    }

    public static void apply(Object source, BakedSkin skin, float animationTicks, AnimationManager animationManager) {
        // we can't apply when missing animation manager.
        if (animationManager == null) {
            return;
        }
        for (var animationController : skin.getAnimationControllers()) {
            // we needs reset the applier.
            var state = animationManager.getAnimationState(animationController);
            if (state == null) {
                animationController.reset();
                continue;
            }
            // we only bind it when transformer use the molang environment.
            var partialTicks = state.getPartialTicks(animationTicks);
            if (state.isRequiresVirtualMachine()) {
                upload(source, partialTicks, state.getStartTime());
            }
            // check/switch frames of animation and write to applier.
            animationController.process(partialTicks);
        }
    }

    public static void upload(Object source, double animTime, double startAnimTime) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) {
            return;
        }

        ANIM_TIME.set(animTime);
        LIFE_TIME.set(startAnimTime);
        TIME_OF_DAY.set(level.getDayTime() / 24000d);

        ACTOR_COUNT.set(level.getEntityCount());
        MOON_PHASE.set(level.getMoonPhase());

        if (source instanceof Entity entity) {
            uploadEntity(entity, animTime, startAnimTime);
        }
        if (source instanceof LivingEntity livingEntity) {
            uploadLivingEntity(livingEntity, animTime, startAnimTime);
        }
    }

    private static void uploadEntity(Entity entity, double animTime, double startAnimTime) {
        DISTANCE_FROM_CAMERA.set(() -> Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.position()));

        IS_ON_GROUND.set(entity.onGround());
        IS_IN_WATER.set(entity.isInWater());
        IS_IN_WATER_OR_RAIN.set(entity.isInWaterRainOrBubble());
    }

    private static void uploadLivingEntity(LivingEntity livingEntity, double animTime, double startAnimTime) {
        HEALTH.set(livingEntity.getHealth());
        MAX_HEALTH.set(livingEntity.getMaxHealth());
        IS_ON_FIRE.set(livingEntity.isOnFire());

        GROUND_SPEED.set(() -> {
            var velocity = livingEntity.getDeltaMovement();
            return MathUtils.sqrt((velocity.x * velocity.x) + (velocity.z * velocity.z));
        });
        YAW_SPEED.set(() -> {
            float a = livingEntity.getViewYRot((float) animTime - 0.1f);
            return livingEntity.getViewYRot((float) animTime - a);
        });
    }
}
