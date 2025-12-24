package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

import org.jetbrains.annotations.Nullable;

public interface EntitySelector {

    float partialTick();

    default double x() {
        return getX(partialTick());
    }

    default double y() {
        return getY(partialTick());
    }

    default double z() {
        return getZ(partialTick());
    }

    default double eyeYaw() {
        return getEyeYaw(partialTick());
    }

    default double eyePitch() {
        return getEyePitch(partialTick());
    }

    default double headYaw() {
        return getHeadYaw(partialTick());
    }

    default double headPitch() {
        return getHeadPitch(partialTick());
    }

    double getX(float partialTick);

    double getY(float partialTick);

    double getZ(float partialTick);

    double getEyeYaw(float partialTick);

    double getEyePitch(float partialTick);

    double getHeadYaw(float partialTick);

    double getHeadPitch(float partialTick);

    int cardinalFacing();

    double distanceFromCamera();

    double distanceFromMove();

    double distanceFromWalk();

    double yawSpeed();

    double groundSpeed();

    double verticalSpeed();

    boolean isVehicle();

    boolean isPassenger();

    boolean isInWater();

    boolean isInWaterRainOrBubble();

    boolean isOnFire();

    boolean isOnGround();

    boolean isSneaking();

    boolean isJumping();

    boolean isSprinting();

    boolean isSwimming();

    boolean isSleeping();

    boolean isSpectator();

    boolean isUnderWater();

    boolean isCloseEyes();

    boolean canSeeSky();

    double ticksFrozen();

    double airSupply();

    @Nullable
    BiomeSelector biome();

    @Nullable
    BlockSelector relativeBlock(int offsetX, int offsetY, int offsetZ);
}
