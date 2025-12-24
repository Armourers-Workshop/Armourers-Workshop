package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.data.EntityDataStorage;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.VariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.MathHelper;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.BiomeSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.BlockSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

public class EntitySelectorImpl<T extends Entity> implements EntitySelector, VariableStorage {

    protected T entity;
    protected VariableStorage variableStorage;
    protected ContextSelectorImpl contextSelector;

    private final BiomeSelectorImpl biomeSelector = new BiomeSelectorImpl();
    private final BlockSelectorImpl blockSelector = new BlockSelectorImpl();

    public EntitySelectorImpl<T> apply(T entity, ContextSelectorImpl contextSelector) {
        this.entity = entity;
        this.contextSelector = contextSelector;
        this.variableStorage = EntityDataStorage.of(entity).variableStorage().map(it -> it.get(contextSelector)).orElse(null);
        return this;
    }

    public T entity() {
        return entity;
    }

    @Override
    public float partialTick() {
        return contextSelector.partialTick();
    }

    @Override
    public double getX(float partialTick) {
        return MathHelper.lerp(partialTick, entity.xo, entity.getX());
    }

    @Override
    public double getY(float partialTick) {
        return MathHelper.lerp(partialTick, entity.yo, entity.getY());
    }

    @Override
    public double getZ(float partialTick) {
        return MathHelper.lerp(partialTick, entity.zo, entity.getZ());
    }

    @Override
    public double getEyeYaw(float partialTick) {
        return entity.getViewXRot(partialTick);
    }

    @Override
    public double getEyePitch(float partialTick) {
        return entity.getViewYRot(partialTick);
    }

    @Override
    public double getHeadYaw(float partialTick) {
        return entity.getHeadYaw(partialTick);
    }

    @Override
    public double getHeadPitch(float partialTick) {
        return entity.getHeadPatch(partialTick);
    }

    @Override
    public int cardinalFacing() {
        // 2 north, 3 south, 4 west, 5 east
        return entity.getDirection().get3DDataValue();
    }

    @Override
    public double distanceFromCamera() {
        return contextSelector.getCameraDistanceFormEntity(entity);
    }

    @Override
    public double distanceFromMove() {
        return entity.walkDist();
    }

    @Override
    public double distanceFromWalk() {
        return entity.moveDist();
    }

    @Override
    public double yawSpeed() {
//        float a = entity.getViewYRot((float) animTime - 0.1f);
//        return entity.getViewYRot((float) animTime - a);
        return 20 * (entity.getYRot() - entity.yRotO);
    }

    @Override
    public double groundSpeed() {
        var velocity = entity.getDeltaMovement();
        return 20 * Math.sqrt(((velocity.x * velocity.x) + (velocity.z * velocity.z)));
    }

    @Override
    public double verticalSpeed() {
        return 20 * (entity.position().y - entity.yo);
    }

    @Override
    public boolean isVehicle() {
        return entity.isVehicle();
    }

    @Override
    public boolean isPassenger() {
        return entity.isPassenger();
    }

    @Override
    public boolean isInWater() {
        return entity.isInWater();
    }

    @Override
    public boolean isInWaterRainOrBubble() {
        return entity.isInWaterRainOrBubble();
    }

    @Override
    public boolean isOnFire() {
        return entity.isOnFire();
    }

    @Override
    public boolean isOnGround() {
        return entity.onGround();
    }

    @Override
    public boolean isSneaking() {
        return entity.onGround() && entity.getPose() == Pose.CROUCHING;
    }

    @Override
    public boolean isJumping() {
        return !entity.isPassenger() && !entity.onGround() && !entity.isInWater();
    }

    @Override
    public boolean isSprinting() {
        return entity.isSprinting();
    }

    @Override
    public boolean isSwimming() {
        return entity.isSwimming();
    }

    @Override
    public boolean isSleeping() {
        return entity.getPose() == Pose.SLEEPING;
    }

    @Override
    public boolean isSpectator() {
        return entity.isSpectator();
    }

    @Override
    public boolean isUnderWater() {
        return entity.isUnderWater();
    }

    @Override
    public boolean isCloseEyes() {
        if (isSleeping()) {
            return true;
        }
        var noise = (entity.getId() * 0.05);
        var time = (contextSelector.animationTick() + noise) % 4.5;
        return time > 4.25;
    }

    @Override
    public boolean canSeeSky() {
        var level = entity.level();
        var pos = entity.blockPosition();
        if (!level.canSeeSky(pos)) {
            return false;
        }
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() <= pos.getY();
    }

    @Override
    public double ticksFrozen() {
        return entity.getTicksFrozen();
    }

    @Override
    public double airSupply() {
        return entity.getAirSupply();
    }

    @Nullable
    @Override
    public BiomeSelector biome() {
        var level = entity.level();
        var biome = AbstractRegistryManager.getBiome(level, entity.blockPosition());
        return biomeSelector.apply(biome);
    }

    @Override
    public BlockSelector relativeBlock(int offsetX, int offsetY, int offsetZ) {
        var level = entity.level();
        double x = entity.getX() + offsetX;
        double y = entity.getX() + offsetX;
        double z = entity.getX() + offsetX;
        var blockState = level.getBlockState(new BlockPos((int) x, (int) y, (int) z));
        return blockSelector.apply(blockState);
    }

    @Override
    public void setVariable(Name name, Result value) {
        variableStorage.setVariable(name, value);
    }

    @Override
    public Result getVariable(Name name) {
        return variableStorage.getVariable(name);
    }
}
