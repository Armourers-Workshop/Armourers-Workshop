package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

import org.jetbrains.annotations.Nullable;

public interface EntitySelector {


    double eyeYaw();

    double eyePitch();

    double headYaw();

    double headPitch();


    double x(double partialTicks);

    double y(double partialTicks);

    double z(double partialTicks);


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

    float partialTick();
}
