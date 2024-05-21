package moe.plushie.armourers_workshop.core.client.other;

public class SkinAnimationManager {

    public static void init() {
//        try {
//            MolangEnvironment environment = MolangEnvironment.get();
//
//            environment.setValue("query.foo", 4d);
//            environment.setValue("query.bar", 12d);
//
//            MolangValue value1 = environment.create("(query.foo - query.bar) < 0");
//            MolangValue value2 = environment.create("query.foo * query.bar * -math.abs(2)");
//
//            ModLog.debug("{}", value1.get());
//            ModLog.debug("{}", value2.get());
//
//
//            //AnimationController.processCurrentAnimation
//            //final double finalAdjustedTick = adjustedTick;
//            //environment.animTime.set(finalAdjustedTick / 20d);
//
//
//            Minecraft mc = Minecraft.getInstance();
//            ClientLevel level = mc.level;
//            Object animatable = null;
//            double animTime = 0;
//
//            environment.lifeTime.set(() -> animTime / 20d);
//            environment.timeOfDay.set(() -> level.getDayTime() / 24000d);
//
//            environment.actorCount.set(level::getEntityCount);
//            environment.moonPhase.set(level::getMoonPhase);
//
//            if (animatable instanceof Entity entity) {
//                environment.distanceFromCamera.set(() -> mc.gameRenderer.getMainCamera().getPosition().distanceTo(entity.position()));
//
//                environment.isOnGround.set(entity::onGround);
//                environment.isInWater.set(entity::isInWater);
//                environment.isInWaterOrRain.set(entity::isInWaterRainOrBubble);
//
//                if (entity instanceof LivingEntity livingEntity) {
//
//                    environment.health.set(livingEntity::getHealth);
//                    environment.maxHealth.set(livingEntity::getMaxHealth);
//                    environment.isOnFire.set(livingEntity::isOnFire);
//
//                    environment.groundSpeed.set(() -> {
//                        auto velocity = livingEntity.getDeltaMovement();
//                        return MathUtils.sqrt((velocity.x * velocity.x) + (velocity.z * velocity.z));
//                    });
//                    environment.yawSpeed.set(() -> {
//                        float a = livingEntity.getViewYRot((float) animTime - 0.1f);
//                        return livingEntity.getViewYRot((float) animTime - a);
//                    });
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
