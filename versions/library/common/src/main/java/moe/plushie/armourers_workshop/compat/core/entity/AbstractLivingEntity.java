package moe.plushie.armourers_workshop.compat.core.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntityDataBuilder;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.compat.api.entity.LivingEntityAccessor;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@Available("[16, )")
public abstract class AbstractLivingEntity extends AbstractLivingEntityImpl implements AbstractEntityHandler, LivingEntityAccessor {

    public AbstractLivingEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected OpenInteractionResult abi$interactAt(Player player, Vec3 pos, OpenInteractionHand hand) {
        return super.interactAt(player, pos, hand, null);
    }

    protected boolean abi$hurt(ServerLevel level, DamageSource source, float amount) {
        return super.hurt(level, source, amount);
    }

    protected void abi$readAdditionalSaveData(IDataSerializer serializer) {
        super.readAdditionalSaveData(serializer);
    }

    protected void abi$writeAdditionalSaveData(IDataSerializer serializer) {
        super.addAdditionalSaveData(serializer);
    }

    protected void abi$defineSynchedData(IEntityDataBuilder builder) {
        super.defineSynchedData(builder);
    }

    protected void abi$onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
    }

    protected void abi$tick() {
        super.tick();
    }

    protected void abi$travel(Vec3 pos) {
        super.travel(pos);
    }

    protected void abi$kill(ServerLevel level) {
        super.kill(level);
    }

    protected void abi$dropEquipment(ServerLevel level) {
        super.dropEquipment(level);
    }

    protected void abi$addPassenger(Entity entity) {
        super.addPassenger(entity);
    }

    protected void abi$removePassenger(Entity entity) {
        super.removePassenger(entity);
    }

    protected float abi$sanitizeScale(float f) {
        return super.sanitizeScale(f);
    }

    protected boolean abi$shouldRender(double x, double y, double z) {
        return super.shouldRender(x, y, z);
    }

    protected boolean abi$shouldRenderAtSqrDistance(double distance) {
        return super.shouldRenderAtSqrDistance(distance);
    }

    protected boolean abi$canBeCollidedWith(@Nullable Entity entity) {
        return super.canBeCollidedWith(entity);
    }

    protected void abi$setNoGravity(boolean bl) {
        super.setNoGravity(bl);
    }

    protected boolean abi$isNoGravity() {
        return super.isNoGravity();
    }

    protected boolean abi$isAlive() {
        return super.isAlive();
    }

    protected boolean abi$isPushable() {
        return super.isPushable();
    }

    protected boolean abi$isAttackable() {
        return super.isAttackable();
    }

    protected float abi$getXRot() {
        return super.getXRot();
    }

    protected void abi$setXRot(float f) {
        super.setXRot(f);
    }

    protected void abi$setYRot(float f) {
        super.setYRot(f);
    }

    protected float abi$getYRot() {
        return super.getYRot();
    }

    protected float abi$getViewXRot(float f) {
        return super.getViewXRot(f);
    }

    protected float abi$getViewYRot(float f) {
        return super.getViewYRot(f);
    }

    protected float abi$getYHeadRot() {
        return super.getYHeadRot();
    }

    protected void abi$setYHeadRot(float f) {
        super.setYHeadRot(f);
    }

    protected float abi$setYBodyRot() {
        return super.yBodyRot;
    }

    protected void abi$setYBodyRot(float f) {
        super.setYBodyRot(f);
    }

    protected AABB abi$getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling();
    }

    protected EntityDimensions abi$getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose);
    }

    protected ItemStack abi$getPickedResult(HitResult target) {
        return ItemStack.EMPTY;
    }

    ///  API Implements

    @Override
    public final OpenInteractionResult interactAt(Player player, Vec3 pos, OpenInteractionHand hand, Object context) {
        return abi$interactAt(player, pos, hand);
    }

    @Override
    public final boolean hurt(ServerLevel level, DamageSource source, float amount) {
        return abi$hurt(level, source, amount);
    }

    @Override
    public final void readAdditionalSaveData(IDataSerializer serializer) {
        abi$readAdditionalSaveData(serializer);
    }

    @Override
    public final void addAdditionalSaveData(IDataSerializer serializer) {
        abi$writeAdditionalSaveData(serializer);
    }

    @Override
    public final void defineSynchedData(IEntityDataBuilder builder) {
        abi$defineSynchedData(builder);
    }

    @Override
    public final void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        abi$onSyncedDataUpdated(accessor);
    }

    @Override
    public final void tick() {
        abi$tick();
    }

    @Override
    public final void travel(Vec3 pos) {
        abi$travel(pos);
    }

    @Override
    public final void kill(ServerLevel level) {
        abi$kill(level);
    }

    @Override
    public final void dropEquipment(ServerLevel level) {
        abi$dropEquipment(level);
    }

    @Override
    public final void addPassenger(Entity entity) {
        abi$addPassenger(entity);
    }

    @Override
    public final void removePassenger(Entity entity) {
        abi$removePassenger(entity);
    }

    @Override
    public final float sanitizeScale(float f) {
        return abi$sanitizeScale(f);
    }

    @Override
    public final boolean shouldRender(double x, double y, double z) {
        return abi$shouldRender(x, y, z);
    }

    @Override
    public final boolean shouldRenderAtSqrDistance(double distance) {
        return abi$shouldRenderAtSqrDistance(distance);
    }

    @Override
    public final boolean canBeCollidedWith(@Nullable Entity entity) {
        return abi$canBeCollidedWith(entity);
    }

    @Override
    public final void setNoGravity(boolean bl) {
        abi$setNoGravity(bl);
    }

    @Override
    public final boolean isNoGravity() {
        return abi$isNoGravity();
    }

    @Override
    public final boolean isAlive() {
        return abi$isAlive();
    }

    @Override
    public final boolean isPushable() {
        return abi$isPushable();
    }

    @Override
    public final boolean isAttackable() {
        return abi$isAttackable();
    }

    @Override
    public final float getXRot() {
        return abi$getXRot();
    }

    @Override
    public final void setXRot(float f) {
        abi$setXRot(f);
    }

    @Override
    public final void setYRot(float f) {
        abi$setYRot(f);
    }

    @Override
    public final float getYRot() {
        return abi$getYRot();
    }

    @Override
    public final float getViewXRot(float f) {
        return abi$getViewXRot(f);
    }

    @Override
    public final float getViewYRot(float f) {
        return abi$getViewYRot(f);
    }

    @Override
    public final float getYHeadRot() {
        return abi$getYHeadRot();
    }

    @Override
    public final void setYHeadRot(float f) {
        abi$setYHeadRot(f);
    }

    @Override
    public final float getYBodyRot() {
        return abi$setYBodyRot();
    }

    @Override
    public final void setYBodyRot(float f) {
        abi$setYBodyRot(f);
    }

    @Override
    public final AABB getBoundingBoxForCulling() {
        return abi$getBoundingBoxForCulling();
    }

    @Override
    public final EntityDimensions getDefaultDimensions(Pose pose) {
        return abi$getDefaultDimensions(pose);
    }

    @Override
    public final ItemStack getPickedResult(HitResult target) {
        return abi$getPickedResult(target);
    }
}
