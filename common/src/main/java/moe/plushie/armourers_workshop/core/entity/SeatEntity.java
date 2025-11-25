package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compat.core.entity.AbstractLivingEntity;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.init.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SeatEntity extends AbstractLivingEntity implements IDataSerializable.Mutable {

    private int holdingTick;
    private BlockPos blockPos = BlockPos.ZERO;

    public SeatEntity(EntityType<? extends SeatEntity> entityType, Level level) {
        super(entityType, level);
        this.setInvisible(true);
        this.setInvulnerable(true);
        this.setYBodyRot(0.0f);
        //this.maxUpStep = 0.0f;
        this.holdingTick = ModConfig.Client.prefersSeatHoldingTick;
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.REFER, blockPos);
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        blockPos = serializer.read(CodingKeys.REFER);
    }

    @Override
    protected void abi$readAdditionalSaveData(IDataSerializer serializer) {
        super.abi$readAdditionalSaveData(serializer);
        this.deserialize(serializer);
    }

    @Override
    protected void abi$writeAdditionalSaveData(IDataSerializer serializer) {
        super.abi$writeAdditionalSaveData(serializer);
        this.serialize(serializer);
    }

    @Override
    protected void abi$tick() {
        super.abi$tick();
        // auto kill the seat entity in the server side.
        if (level() instanceof ServerLevel level) {
            if (holdingTick > 0) {
                holdingTick--;
            }
            if (holdingTick <= 0 && abi$isAlive() && !isWorking()) {
                abi$kill(level);
            }
        }
    }

    @Override
    protected void abi$travel(Vec3 local) {
        if (isAlive() && !getPassengers().isEmpty()) {
            var passenger = getPassengers().get(0);
            abi$setYBodyRot(passenger.getYRot());
        }
    }

    @Override
    protected void abi$addPassenger(Entity entity) {
        super.abi$addPassenger(entity);
    }

    @Override
    protected void abi$removePassenger(Entity entity) {
        super.abi$removePassenger(entity);
        holdingTick = ModConfig.Client.prefersSeatHoldingTick;
    }

    @Override
    protected void abi$kill(ServerLevel level) {
        remove();
    }

    @Override
    protected void abi$setYBodyRot(float f) {
        super.abi$setYBodyRot(f);
        this.abi$setYRot(f);
        yRotO = f;
        yHeadRot = f;
    }


    @Override
    protected boolean abi$isNoGravity() {
        return true;
    }

    @Override
    protected boolean abi$isPushable() {
        return false;
    }

    @Override
    protected boolean abi$isAttackable() {
        return false;
    }

    @Override
    protected boolean abi$canBeCollidedWith(@Nullable Entity entity) {
        return false;
    }

    // REMOVE FROM 1.21
//    @Override
//    protected double getPassengersRidingOffset() {
//        return -0.15f;
//    }

    @Override
    protected boolean abi$shouldRender(double x, double y, double z) {
        return false;
    }

    @Override
    protected boolean abi$shouldRenderAtSqrDistance(double distance) {
        return false;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public boolean isWorking() {
        if (getPassengers().isEmpty()) {
            return false;
        }
        return blockPos != null && level().getBlockEntity(blockPos) instanceof SkinnableBlockEntity;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<BlockPos> REFER = IDataSerializerKey.create("Refer", ExtraCodecs.BLOCK_POS, BlockPos.ZERO);
    }
}
